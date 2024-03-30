package com.hhrpc.hhrpc.core.consumer;

import com.hhrpc.hhrpc.core.api.Filter;
import com.hhrpc.hhrpc.core.api.RpcContent;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.utils.HhrpcMethodUtils;
import com.hhrpc.hhrpc.core.utils.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
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
        List<Filter> filterList = rpcContent.getFilterList();
        for (Filter filter : filterList) {
            Object result = filter.preFilter(request);
            if (Objects.nonNull(result)) {
                return result;
            }
        }
        log.debug("==> invoke: " + request);
        RpcResponse rpcResponse = getRpcResponse(request, method);
        log.debug("==> post result: " + rpcResponse);
        if (!rpcResponse.getStatus()) {
            return null;
        }
        for (Filter filter : filterList) {
            Object result = filter.postFilter(request, rpcResponse, rpcResponse.getData());
            if (Objects.nonNull(result)) {
                return result;
            }
        }
        return rpcResponse.getData();
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
