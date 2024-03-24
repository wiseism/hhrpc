package com.hhrpc.hhrpc.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    /**
     * 获取所有带有  @HhRpcConsumer 注解的字段
     * @param clazz
     * @return
     */
    public static List<Field> findAnnotationFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        List<Field> result = new ArrayList<>();
        while (Objects.nonNull(clazz)) {
            List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(annotationClass))
                    .toList();
            if (Objects.nonNull(fields) && fields.size() > 0) {
                result.addAll(fields);
            }
            clazz = clazz.getSuperclass();
        }
        return result;
    }
}
