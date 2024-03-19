package com.hhrpc.hhrpc.core.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @Date 2024/3/20
 * @Author lifei
 */
public class HhrpcMethodUtils {

    private static final String ARG_CONNECT = "&";

    /**
     * 检查是否是本地方法
     * @param method
     * @return
     */
    public static boolean checkLocalMethod(Method method) {
        return method.getDeclaringClass().equals(Object.class);
    }

    public static String createeMethodSign(Method method) {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String args = "";
        if (parameterTypes!=null || parameterTypes.length>0) {
            args = Arrays.stream(parameterTypes).map(Class::getCanonicalName)
                    .collect(Collectors.joining(ARG_CONNECT));
        }
        return String.format("%s#%s", methodName, args);
    }
}
