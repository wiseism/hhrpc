package com.hhrpc.hhrpc.core.api;

/**
 * @Date 2024/3/22
 * @Author lifei
 */
public interface Filter {

    /**
     * 前置过滤器
     * @param rpcRequest
     * @return
     */
    Object preFilter(RpcRequest rpcRequest);

    Object postFilter(RpcRequest rpcRequest, RpcResponse rpcResponse, Object result);

    Filter DEFAULT = new Filter() {
        @Override
        public Object preFilter(RpcRequest rpcRequest) {
            return null;
        }

        @Override
        public Object postFilter(RpcRequest rpcRequest, RpcResponse rpcResponse, Object result) {
            return result;
        }
    };
}
