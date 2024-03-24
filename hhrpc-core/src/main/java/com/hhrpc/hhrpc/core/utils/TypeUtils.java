package com.hhrpc.hhrpc.core.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import okhttp3.Response;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Date 2024/3/20
 * @Author lifei
 */
public class TypeUtils {

    public static Class<?> cast(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return null;
        }
        if (Integer.class.isAssignableFrom(clazz) || Integer.TYPE.equals(clazz)) {
            return Integer.class;
        } else if (Short.class.isAssignableFrom(clazz) || Short.TYPE.equals(clazz)) {
            return Short.class;
        }else if (Character.class.isAssignableFrom(clazz) || Character.TYPE.equals(clazz)) {
            return Character.class;
        }else if (Long.class.isAssignableFrom(clazz) || Long.TYPE.equals(clazz)) {
            return Long.class;
        }else if (Boolean.class.isAssignableFrom(clazz) || Boolean.TYPE.equals(clazz)) {
            return Boolean.class;
        }else if (Double.class.isAssignableFrom(clazz) || Double.TYPE.equals(clazz)) {
            return Double.class;
        }else if (Float.class.isAssignableFrom(clazz) || Float.TYPE.equals(clazz)) {
            return Float.class;
        }

        return clazz;
    }

    private static Object cast(Object origin, Class<?> parameterType, Type genericParameterType) {
        if (Objects.isNull(origin)) {
            return null;
        }
        if (Integer.class.isAssignableFrom(parameterType) || Integer.TYPE.equals(parameterType)) {
            return Integer.valueOf(origin.toString());
        } else if (Short.class.isAssignableFrom(parameterType) || Short.TYPE.equals(parameterType)) {
            return Short.valueOf(origin.toString());
        }else if (Character.class.isAssignableFrom(parameterType) || Character.TYPE.equals(parameterType)) {
            return Character.valueOf(origin.toString().charAt(0));
        }else if (Long.class.isAssignableFrom(parameterType) || Long.TYPE.equals(parameterType)) {
            return Long.valueOf(origin.toString());
        }else if (Boolean.class.isAssignableFrom(parameterType) || Boolean.TYPE.equals(parameterType)) {
            return Boolean.valueOf(origin.toString());
        }else if (Double.class.isAssignableFrom(parameterType) || Double.TYPE.equals(parameterType)) {
            return Double.valueOf(origin.toString());
        }else if (Float.class.isAssignableFrom(parameterType) || Float.TYPE.equals(parameterType)) {
            return Float.valueOf(origin.toString());
        }

        Gson gson = new Gson();
        String json = gson.toJson(origin);
        if (List.class.isAssignableFrom(parameterType)) {
            if (genericParameterType instanceof ParameterizedType parameterizedType) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                TypeToken<?> parameterized = TypeToken.getParameterized(List.class, actualTypeArguments);
                return gson.fromJson(json, parameterized.getType());
            }
        }
        if (Map.class.isAssignableFrom(parameterType)) {
            if (genericParameterType instanceof ParameterizedType parameterizedType) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                TypeToken<?> parameterized = TypeToken.getParameterized(Map.class, actualTypeArguments);
                return gson.fromJson(json, parameterized.getType());
            }
        }
        Object result = gson.fromJson(json, parameterType);
        return result;
    }

    public static Object[] processArgs(Object[] args, Class<?>[] parameterTypes, Type[] genericParameterTypes) {
        if (args==null || args.length==0) {
            return args;
        }
        Object[] result = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = cast(args[i], parameterTypes[i], genericParameterTypes[i]);
        }
        return result;
    }

    public static RpcResponse getRpcResponse(Method method, RpcResponse<?> rpcResponse) {
        try {
            Gson gson  = new Gson();
            String responseData = gson.toJson(rpcResponse);
            // 反序列结果
            Class<?> realClazz = TypeUtils.cast(method.getReturnType());
            TypeToken<?> parameterized = TypeToken.getParameterized(RpcResponse.class, realClazz);
            Type genericReturnType = method.getGenericReturnType();
            if (List.class.isAssignableFrom(realClazz)) {
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    TypeToken<?> listTypeToken = TypeToken.getParameterized(List.class, actualTypeArguments);
                    parameterized = TypeToken.getParameterized(RpcResponse.class, listTypeToken.getType());
                }
            }
            if (Map.class.isAssignableFrom(realClazz)) {
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    TypeToken<?> mapTypeToken = TypeToken.getParameterized(Map.class, actualTypeArguments);
                    parameterized = TypeToken.getParameterized(RpcResponse.class, mapTypeToken.getType());
                }
            }
            RpcResponse result = gson.fromJson(responseData, parameterized.getType());
            return result;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
