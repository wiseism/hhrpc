package com.hhrpc.hhrpc.core.api;

import com.google.common.base.MoreObjects;
import com.hhrpc.hhrpc.core.consumer.HttpInvoker;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Date 2024/3/22
 * @Author lifei
 */
public class RpcContent {
    private List<Filter> filterList;
    private Router<InstanceMeta> router;
    private LoadBalance<InstanceMeta> loadBalance;
    private Map<String, String> parameters = new HashMap<>();

    private HttpInvoker httpInvoker;

    public RpcContent() {
    }

    public RpcContent(Router<InstanceMeta> router, LoadBalance<InstanceMeta> loadBalance, HttpInvoker httpInvoker) {
        this.router = router;
        this.loadBalance = loadBalance;
        this.httpInvoker = httpInvoker;
    }

    public List<Filter> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<Filter> filterList) {
        this.filterList = filterList;
    }

    public Router<InstanceMeta> getRouter() {
        return router;
    }

    public void setRouter(Router<InstanceMeta> router) {
        this.router = router;
    }

    public LoadBalance<InstanceMeta> getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(LoadBalance<InstanceMeta> loadBalance) {
        this.loadBalance = loadBalance;
    }

    public HttpInvoker getHttpInvoker() {
        return httpInvoker;
    }

    public void setHttpInvoker(HttpInvoker httpInvoker) {
        this.httpInvoker = httpInvoker;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(RpcContent.class)
                .add("filterList", filterList)
                .add("router", router)
                .add("loadBalance", loadBalance)
                .add("httpInvoker", httpInvoker)
                .add("parameters", parameters)
                .toString();
    }
}
