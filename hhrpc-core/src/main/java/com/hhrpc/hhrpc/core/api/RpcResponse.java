package com.hhrpc.hhrpc.core.api;

import com.google.common.base.MoreObjects;

/**
 * @Date 2024/3/9
 * @Author lifei
 */
public class RpcResponse<T> {

    private Boolean status;
    private T data;
    private String errorCode;

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

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(RpcResponse.class)
                .add("status", status)
                .add("data", data)
                .add("errorCode", errorCode)
                .toString();
    }
}
