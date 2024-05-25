package com.hhrpc.hhrpc.core.consumer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.consumer.http.OkHttpInvoker;

import java.util.Objects;

public interface HttpInvoker {

    RpcResponse<?> post(RpcRequest request, String url);

    String get(String url);
    String post(String url, String requestBody);

    HttpInvoker DEFAULT = new OkHttpInvoker(5000);

    static <T> T httpGet(String url, Class<T> clazz) {
        try {
            String response = DEFAULT.get(url);
            if (Objects.isNull(response)) {
                return null;
            }
            return new Gson().fromJson(response, clazz);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static <T> T httpPost(String url, String requestBody, Class<T> clazz) {
        try {
            String response = DEFAULT.post(url, requestBody);
            if (Objects.isNull(response)) {
                return null;
            }
            return new Gson().fromJson(response, clazz);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static <T> T httpGet(String url, TypeToken<T> typeToken) {
        try {
            String response = DEFAULT.get(url);
            if (Objects.isNull(response)) {
                return null;
            }
            return new Gson().fromJson(response, typeToken.getType());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static <T> T httpPost(String url, String requestBody, TypeToken<T> typeToken) {
        try {
            String response = DEFAULT.post(url, requestBody);
            if (Objects.isNull(response)) {
                return null;
            }
            return new Gson().fromJson(response, typeToken.getType());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
