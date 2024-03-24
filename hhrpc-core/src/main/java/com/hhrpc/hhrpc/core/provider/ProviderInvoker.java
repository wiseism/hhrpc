package com.hhrpc.hhrpc.core.provider;

import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.meta.ProviderMeta;
import com.hhrpc.hhrpc.core.utils.TypeUtils;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;

/**
 * @Date 2024/3/24
 * @Author lifei
 */
public class ProviderInvoker {

    private MultiValueMap<String, ProviderMeta> serviceMap;

    public ProviderInvoker(ProviderBootStarp providerBootStarp) {
        this.serviceMap = providerBootStarp.getServiceMap();
    }

    public RpcResponse invokeMethod(RpcRequest rpcRequest) {
        RpcResponse result = new RpcResponse();
        try {
            ProviderMeta providerMeta = fetchProviderMeta(rpcRequest);
            Method method = providerMeta.getMethod();
            Object[] args = TypeUtils.processArgs(rpcRequest.getArgs(), method.getParameterTypes(), method.getGenericParameterTypes());
            Object data = method.invoke(providerMeta.getServiceImpl(), args);
            result.setStatus(true);
            result.setData(data);
            return result;
        }catch (Exception e) {
            e.printStackTrace();
            result.setStatus(false);
            result.setData(null);
            return result;
        }
    }

    private ProviderMeta fetchProviderMeta(RpcRequest rpcRequest) {
        return serviceMap.get(rpcRequest.getServiceName()).stream()
                .filter(item->item.getMethodSign().equals(rpcRequest.getMethodSign()))
                .findAny()
                .orElse(null);
    }



}
