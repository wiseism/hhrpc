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
        RpcResponse rpcResponse = getRpcResponse(request, method.getReturnType(), method.getGenericReturnType());
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
    private RpcResponse getRpcResponse(RpcRequest request, Class<?> returnType, Type genericReturnType) {
        try {
            Gson gson = new Gson();
            String requestData = gson.toJson(request);
            String url = (String)rpcContent.getLoadBalance().choose(rpcContent.getRouter().rout(providers));
            Response response = client.newCall(new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(requestData, MediaType.get("application/json; charset=utf-8")))
                    .build()).execute();
            String responseData = response.body().string();
            // 反序列结果
            Class<?> realClazz = TypeUtils.cast(returnType);
            if (List.class.isAssignableFrom(realClazz)) {
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    TypeToken<?> listTypeToken = TypeToken.getParameterized(List.class, actualTypeArguments);
                    TypeToken<?> parameterized = TypeToken.getParameterized(RpcResponse.class, listTypeToken.getType());
                    RpcResponse rpcResponse = gson.fromJson(responseData, parameterized.getType());
                    return rpcResponse;
                }
            }
            if (Map.class.isAssignableFrom(realClazz)) {
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    TypeToken<?> mapTypeToken = TypeToken.getParameterized(Map.class, actualTypeArguments);
                    TypeToken<?> parameterized = TypeToken.getParameterized(RpcResponse.class, mapTypeToken.getType());
                    RpcResponse rpcResponse = gson.fromJson(responseData, parameterized.getType());
                    return rpcResponse;
                }
            }
            TypeToken<?> parameterized = TypeToken.getParameterized(RpcResponse.class, realClazz);
            RpcResponse rpcResponse = gson.fromJson(responseData, parameterized.getType());
            return rpcResponse;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
