package com.hhrpc.hhrpc.core.api;

/**
 * @Date 2024/3/9
 * @Author lifei
 */
public class RpcResponse<T> {

    private Boolean status;
    private T data;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "status=" + status +
                ", data=" + data +
                '}';
    }
}
