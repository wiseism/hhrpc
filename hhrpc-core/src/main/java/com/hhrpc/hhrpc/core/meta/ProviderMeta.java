package com.hhrpc.hhrpc.core.meta;

import java.lang.reflect.Method;

/**
 * @Date 2024/3/20
 * @Author lifei
 */
public class ProviderMeta {

    private String methodSign;
    private Method method;
    private Object serviceImpl;

    public String getMethodSign() {
        return methodSign;
    }

    public void setMethodSign(String methodSign) {
        this.methodSign = methodSign;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getServiceImpl() {
        return serviceImpl;
    }

    public void setServiceImpl(Object serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public String toString() {
        return "ProviderMeta{" +
                "methodSign='" + methodSign + '\'' +
                ", method=" + method +
                ", serviceImpl=" + serviceImpl +
                '}';
    }
}
