package com.hhrpc.hhrpc.core.consumer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hhrpc.hhrpc.core.api.*;
import com.hhrpc.hhrpc.core.utils.HhrpcMethodUtils;
import com.hhrpc.hhrpc.core.utils.TypeUtils;
import okhttp3.*;
import org.springframework.context.annotation.TypeFilterUtils;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2024/3/12
 * @Author lifei
 */
public class HhRpcConsumerInvocationHandler implements InvocationHandler {

    private final String serviceName;
    private RpcContent rpcContent;
    private List<String> providers;

    public HhRpcConsumerInvocationHandler(String serviceName, RpcContent rpcContent, List<String> providers) {
        this.serviceName = serviceName;
        this.rpcContent = rpcContent;
        this.providers = providers;
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
        RpcResponse rpcResponse = getRpcResponse(request, method);
        if (rpcResponse.getStatus()) {
            return rpcResponse.getData();
        }else {
            return null;
        }
    }



    /**
     * 通过HTTP+json请求，获取RpcResponse
     * @param request
     * @return
     */
    private RpcResponse getRpcResponse(RpcRequest request, Method method) {
        try {
            String url = (String)rpcContent.getLoadBalance().choose(rpcContent.getRouter().rout(providers));
            RpcResponse<?> rpcResponse = rpcContent.getHttpInvoker().post(request, url);
            RpcResponse result = TypeUtils.getRpcResponse(method, rpcResponse);
            return result;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
