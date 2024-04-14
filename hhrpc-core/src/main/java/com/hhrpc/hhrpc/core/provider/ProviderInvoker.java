package com.hhrpc.hhrpc.core.provider;

import com.hhrpc.hhrpc.core.api.*;
import com.hhrpc.hhrpc.core.meta.ProviderMeta;
import com.hhrpc.hhrpc.core.utils.TypeUtils;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @Date 2024/3/24
 * @Author lifei
 */
public class ProviderInvoker {

    private MultiValueMap<String, ProviderMeta> serviceMap;

    public ProviderInvoker(ProviderBootStarp providerBootStarp) {
        this.serviceMap = providerBootStarp.getServiceMap();
    }

    public RpcResponse<Object> invokeMethod(RpcRequest rpcRequest) {
        RpcResponse<Object> result = new RpcResponse<>();
        result.setStatus(false);
        try {
            if (Objects.nonNull(rpcRequest.getParameters()) && !rpcRequest.getParameters().isEmpty()) {
                RpcContext.contextParameters.get().putAll(rpcRequest.getParameters());
            }
            ProviderMeta providerMeta = fetchProviderMeta(rpcRequest);
            Method method = providerMeta.getMethod();
            Object[] args = TypeUtils.processArgs(rpcRequest.getArgs(), method.getParameterTypes(), method.getGenericParameterTypes());
            Object data = method.invoke(providerMeta.getServiceImpl(), args);
            result.setStatus(true);
            result.setData(data);
            return result;
        } catch (InvocationTargetException e) {
            result.setErrorCode(HhRpcExceptionEnum.X002.getErrorCode());
        } catch (IllegalAccessException e) {
            result.setErrorCode(HhRpcExceptionEnum.Z001.getErrorCode());
        }
        return result;
    }

    private ProviderMeta fetchProviderMeta(RpcRequest rpcRequest) {
        return serviceMap.get(rpcRequest.getServiceName()).stream()
                .filter(item->item.getMethodSign().equals(rpcRequest.getMethodSign()))
                .findAny()
                .orElse(null);
    }



}
