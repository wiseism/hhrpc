package com.hhrpc.hhrpc.core.api;

import java.util.List;

/**
 * @Date 2024/3/22
 * @Author lifei
 */
public class RpcContent {
    private List<Filter> filterList;
    private Router router;
    private LoadBalance loadBalance;

    public RpcContent() {
    }

    public RpcContent(Router router, LoadBalance loadBalance) {
        this.router = router;
        this.loadBalance = loadBalance;
    }

    public List<Filter> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<Filter> filterList) {
        this.filterList = filterList;
    }

    public Router getRouter() {
        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }

    public LoadBalance getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    @Override
    public String toString() {
        return "RpcContent{" +
                "filterList=" + filterList +
                ", router=" + router +
                ", loadBalance=" + loadBalance +
                '}';
    }
}
