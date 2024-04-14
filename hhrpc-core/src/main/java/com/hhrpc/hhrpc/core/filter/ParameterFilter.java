package com.hhrpc.hhrpc.core.filter;

import com.hhrpc.hhrpc.core.api.Filter;
import com.hhrpc.hhrpc.core.api.RpcContent;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;

import java.util.Map;

/**
 * @Date 2024/4/14
 * @Author lifei
 */
public class ParameterFilter implements Filter {
    @Override
    public Object preFilter(RpcRequest rpcRequest) {
        Map<String, String> contextParameters = RpcContent.contextParameters.get();
        if (!contextParameters.isEmpty()) {
            rpcRequest.getParameters().putAll(contextParameters);
        }
        return null;
    }

    @Override
    public Object postFilter(RpcRequest rpcRequest, RpcResponse rpcResponse, Object result) {
        return result;
    }
}
