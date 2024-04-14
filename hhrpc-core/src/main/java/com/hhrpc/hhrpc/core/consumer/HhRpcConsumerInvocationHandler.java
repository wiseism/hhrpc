package com.hhrpc.hhrpc.core.consumer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hhrpc.hhrpc.core.api.*;
import com.hhrpc.hhrpc.core.governance.SlidingTimeWindow;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.utils.HhrpcMethodUtils;
import com.hhrpc.hhrpc.core.utils.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2024/3/12
 * @Author lifei
 */
public class HhRpcConsumerInvocationHandler implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(HhRpcConsumerInvocationHandler.class);

    private final String serviceName;
    private RpcContext rpcContext;
    private List<InstanceMeta> providers;
    // 隔离的providers
    private Set<InstanceMeta> isolatedProviders = Sets.newHashSet();
    // 探活列表
    private List<InstanceMeta> halfOpenProviders = Lists.newArrayList();
    // 滑动时间窗口的map
    private Map<String, SlidingTimeWindow> slidingTimeWindowMap = new HashMap<>();
    private final ScheduledExecutorService scheduledExecutorService;

    public HhRpcConsumerInvocationHandler(String serviceName, RpcContext rpcContext, List<InstanceMeta> providers) {
        this.serviceName = serviceName;
        this.rpcContext = rpcContext;
        this.providers = providers;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        long initDelay = Long.parseLong(rpcContext.param("consumer.halfOpenInitDelay"));
        long delay = Long.parseLong(rpcContext.param("consumer.halfOpenDelay"));
        this.scheduledExecutorService.scheduleWithFixedDelay(this::halfOpen, initDelay, delay, TimeUnit.MILLISECONDS);
    }

    public void halfOpen() {
        this.halfOpenProviders.clear();
        this.halfOpenProviders.addAll(isolatedProviders);
        log.debug("===> halfOpenProviders: {}, isolatedProviders: {}, providers: {}", halfOpenProviders, isolatedProviders, providers);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setServiceName(serviceName);
        request.setArgs(args);
        request.setMethodSign(HhrpcMethodUtils.createeMethodSign(method));
        if (HhrpcMethodUtils.checkLocalMethod(method)){
            return null;
        }
        int retries = Integer.parseInt(rpcContext.param("consumer.retries"));
        int faultLimit = Integer.parseInt(rpcContext.param("consumer.faultLimit"));
        String url = null;
        Object result = null;
        while (retries-- >0) {
            try {
                log.debug("===> reties: {}", retries);
                List<Filter> filterList = rpcContext.getFilterList();
                for (Filter filter : filterList) {
                    result = filter.preFilter(request);
                    if (Objects.nonNull(result)) {
                        log.debug("===> filter: " + result);
                        return result;
                    }
                }
                RpcResponse<?> rpcResponse = null;
                InstanceMeta instanceMeta = null;
                try {
                    synchronized (halfOpenProviders) {
                        if (halfOpenProviders.isEmpty()) {
                            instanceMeta = rpcContext.getLoadBalance().choose(rpcContext.getRouter().rout(providers));
                        } else {
                            instanceMeta = halfOpenProviders.remove(0);
                            log.debug("===> try life instance: {}", instanceMeta);
                        }
                    }
                    url = instanceMeta.toUrl();
                    log.debug("===> url: " + url);
                    rpcResponse = rpcContext.getHttpInvoker().post(request, url);
                    if (!rpcResponse.getStatus()) {
                        HhRpcExceptionEnum hhRpcExceptionEnum = HhRpcExceptionEnum.findHhRpcExceptionEnum(rpcResponse.getErrorCode());
                        throw new HhRpcException(hhRpcExceptionEnum.getErrorMessage());
                    }
                }catch (Exception e) {
                    log.info("===> fault url: {}", url);
                    slidingTimeWindowMap.putIfAbsent(url, new SlidingTimeWindow());
                    SlidingTimeWindow slidingTimeWindow = slidingTimeWindowMap.get(url);
                    slidingTimeWindow.record(System.currentTimeMillis());
                    int sum = slidingTimeWindow.getSum();
                    log.debug("===> url({}) fault count: {}" , url, sum);
                    if (sum>=faultLimit) {
                        isolated(instanceMeta);
                    }
                    throw e;
                }

                synchronized (providers) {
                    // 探活成功
                    if (!providers.contains(instanceMeta)) {
                        isolatedProviders.remove(instanceMeta);
                        providers.add(instanceMeta);
                    }
                }
                rpcResponse = TypeUtils.getRpcResponse(method, rpcResponse);
                result = rpcResponse.getData();
                log.debug("==> post result: " + result);

                for (Filter filter : filterList) {
                    result = filter.postFilter(request, rpcResponse, result);
                    if (Objects.nonNull(result)) {
                        return result;
                    }
                }
                return result;
            } catch (Exception e) {
                if (!(e.getCause() instanceof SocketTimeoutException)) {
                    throw e;
                }
                log.error("===> SocketTimeoutException: {}", url);
            }
        }
        return result;
    }

    /**
     * 隔离
     * @param instanceMeta
     */
    private void isolated(InstanceMeta instanceMeta) {
        log.debug("===> isolated: {}", instanceMeta);
        providers.remove(instanceMeta);
        isolatedProviders.add(instanceMeta);
    }


    /**
     * 通过HTTP+json请求，获取RpcResponse
     * @param request
     * @return
     */
    private RpcResponse getRpcResponse(RpcRequest request, Method method) {
        try {
            InstanceMeta instanceMeta = rpcContext.getLoadBalance().choose(rpcContext.getRouter().rout(providers));
            RpcResponse<?> rpcResponse = rpcContext.getHttpInvoker().post(request, instanceMeta.toUrl());
            RpcResponse result = TypeUtils.getRpcResponse(method, rpcResponse);
            return result;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
