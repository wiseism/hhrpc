package com.hhrpc.hhrpc.core.api;

import java.util.Arrays;

/**
 * @Date 2024/3/9
 * @Author lifei
 */
public class RpcRequest {

    private String serviceName;
    private String methodSign;
    private Object[] args;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodSign() {
        return methodSign;
    }

    public void setMethodSign(String methodSign) {
        this.methodSign = methodSign;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "serviceName='" + serviceName + '\'' +
                ", methodSign='" + methodSign + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
