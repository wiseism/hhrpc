package com.hhrpc.hhrpc.core.api;

import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.meta.ServiceMeta;

import java.util.List;

/**
 * @Date 2024/3/22
 * @Author lifei
 */
public interface RegisterCenter {

    void start();
    void stop();

    void register(ServiceMeta serviceMeta, InstanceMeta instanceMeta);
    void unregister(ServiceMeta serviceMeta, InstanceMeta instanceMeta);

    List<InstanceMeta> findAll(ServiceMeta serviceMeta);

    void subscribe(ServiceMeta serviceMeta, EventListener eventListener);

}
