package com.hhrpc.hhrpc.core.consumer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2024/3/12
 * @Author lifei
 */
public class HhRpcConsumerInvocationHandler implements InvocationHandler {

    private final String serviceName;

    public HhRpcConsumerInvocationHandler(String serviceName) {
        this.serviceName = serviceName;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setServiceName(serviceName);
        request.setArgs(args);
        request.setMethodName(method.getName());

        RpcResponse rpcResponse = getRpcResponse(request, method.getReturnType());
        if (rpcResponse.getStatus()) {
            return rpcResponse.getData();
        }else {
            return null;
        }
    }

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build();

    /**
     * 通过HTTP+json请求，获取RpcResponse
     * @param request
     * @return
     */
    private RpcResponse getRpcResponse(RpcRequest request, Class<?> returnType) {
        try {
            Gson gson = new Gson();
            String requestData = gson.toJson(request);
            Response response = client.newCall(new Request.Builder()
                    .url("http://localhost:8080/")
                    .post(RequestBody.create(requestData, MediaType.get("application/json; charset=utf-8")))
                    .build()).execute();
            String responseData = response.body().string();
            // 反序列结果
            TypeToken<?> parameterized = TypeToken.getParameterized(RpcResponse.class, returnType);
            RpcResponse rpcResponse = gson.fromJson(responseData, parameterized.getType());
            return rpcResponse;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
