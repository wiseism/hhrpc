package com.hhrpc.hhrpc.core.consumer;

import com.hhrpc.hhrpc.core.api.*;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.utils.HhrpcMethodUtils;
import com.hhrpc.hhrpc.core.utils.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Objects;

/**
 * @Date 2024/3/12
 * @Author lifei
 */
public class HhRpcConsumerInvocationHandler implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(HhRpcConsumerInvocationHandler.class);

    private final String serviceName;
    private RpcContent rpcContent;
    private List<InstanceMeta> instanceMetaList;

    public HhRpcConsumerInvocationHandler(String serviceName, RpcContent rpcContent, List<InstanceMeta> instanceMetaList) {
        this.serviceName = serviceName;
        this.rpcContent = rpcContent;
        this.instanceMetaList = instanceMetaList;
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
        int retries = Integer.parseInt(rpcContent.getParameters().get("app.retries"));
        String url = null;
        Object result = null;
        while (retries-- >0) {
            try {
                log.debug("===> reties: {}", retries);
                List<Filter> filterList = rpcContent.getFilterList();
                for (Filter filter : filterList) {
                    result = filter.preFilter(request);
                    if (Objects.nonNull(result)) {
                        log.debug("===> filter: " + result);
                        return result;
                    }
                }
                InstanceMeta instanceMeta = rpcContent.getLoadBalance().choose(rpcContent.getRouter().rout(instanceMetaList));
                url = instanceMeta.toUrl();
                log.debug("===> url: " + url);
                RpcResponse<?> rpcResponse = rpcContent.getHttpInvoker().post(request, url);
                if (!rpcResponse.getStatus()) {
                    HhRpcExceptionEnum hhRpcExceptionEnum = HhRpcExceptionEnum.findHhRpcExceptionEnum(rpcResponse.getErrorCode());
                    throw new HhRpcException(hhRpcExceptionEnum.getErrorMessage());
                }
//        Object result = rpcResponse.getData();
//        result  = TypeUtils.castFastJsonReturnObject(result, method);
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
     * 通过HTTP+json请求，获取RpcResponse
     * @param request
     * @return
     */
    private RpcResponse getRpcResponse(RpcRequest request, Method method) {
        try {
            InstanceMeta instanceMeta = rpcContent.getLoadBalance().choose(rpcContent.getRouter().rout(instanceMetaList));
            RpcResponse<?> rpcResponse = rpcContent.getHttpInvoker().post(request, instanceMeta.toUrl());
            RpcResponse result = TypeUtils.getRpcResponse(method, rpcResponse);
            return result;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
