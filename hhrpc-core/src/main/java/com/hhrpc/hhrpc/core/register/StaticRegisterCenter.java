package com.hhrpc.hhrpc.core.register;

import com.hhrpc.hhrpc.core.api.EventListener;
import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;

import java.util.List;

/**
 * @Date 2024/3/22
 * @Author lifei
 */
public class StaticRegisterCenter implements RegisterCenter {

    private List<InstanceMeta> provider;

    public StaticRegisterCenter(List<InstanceMeta> provider) {
        this.provider = provider;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void register(String service, InstanceMeta instanceMeta) {

    }

    @Override
    public void unregister(String service, InstanceMeta instanceMeta) {

    }

    @Override
    public List<InstanceMeta> findAll(String service) {
        return provider;
    }

    @Override
    public void subscribe(String service, EventListener eventListener) {

    }
}
