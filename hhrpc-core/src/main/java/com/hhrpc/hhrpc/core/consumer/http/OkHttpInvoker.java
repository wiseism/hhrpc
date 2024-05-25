package com.hhrpc.hhrpc.core.consumer.http;

import static com.google.common.base.Preconditions.*;
import com.google.gson.Gson;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.consumer.HttpInvoker;
import okhttp3.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2024/3/24
 * @Author lifei
 */
public class OkHttpInvoker implements HttpInvoker {

    private final OkHttpClient client;

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    public OkHttpInvoker() {
        this(1_000L);
    }

    public OkHttpInvoker(long timeout) {
        client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
    }
    @Override
    public RpcResponse<?> post(RpcRequest request, String url) {
        try {
            Gson gson = new Gson();
            String requestData = gson.toJson(request);
            Response response = client.newCall(new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(requestData, MEDIA_TYPE_JSON))
                    .build()).execute();
            String resultJson = response.body().string();
            return gson.fromJson(resultJson, RpcResponse.class);
//            return JSONObject.parseObject(resultJson, RpcResponse.class);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String get(String url) {
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()){
            checkState(response.isSuccessful(), "Unexpected code " + response);
            ResponseBody body = response.body();
            if (Objects.nonNull(body)) {
                return body.string();
            }
            return null;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public String post(String url, String requestBody) {
        Request request = new Request.Builder().url(url).post(RequestBody.create(requestBody, MEDIA_TYPE_JSON)).build();
        try (Response response = client.newCall(request).execute()){
            checkState(response.isSuccessful(), "Unexpected code " + response);
            ResponseBody body = response.body();
            if (Objects.nonNull(body)) {
                return body.string();
            }
            return null;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
