package com.hhrpc.hhrpc.core.api;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

/**
 * @Date 2024/3/9
 * @Author lifei
 */
public class RpcRequest {

    private String serviceName;
    private String methodSign;
    private Object[] args;
    private Map<String, String> parameters = Maps.newHashMap();

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

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(RpcRequest.class)
                .add("serviceName", serviceName)
                .add("methodSign", methodSign)
                .add("args", Arrays.toString(args))
                .add("parameters", parameters)
                .toString();
    }
}
