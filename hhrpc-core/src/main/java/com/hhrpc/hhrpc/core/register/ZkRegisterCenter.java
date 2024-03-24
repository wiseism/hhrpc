package com.hhrpc.hhrpc.core.register;

import com.google.common.base.Strings;
import com.hhrpc.hhrpc.core.api.Event;
import com.hhrpc.hhrpc.core.api.EventListener;
import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.meta.ServiceMeta;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Date 2024/3/22
 * @Author lifei
 */
public class ZkRegisterCenter implements RegisterCenter {

    private CuratorFramework client;
    private TreeCache treeCache;


    @Override
    public void start() {
        final RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder()
                .retryPolicy(retryPolicy)
                .connectString("localhost:2181")
                .namespace("hhrpc")
                .build();
        this.client.start();
    }

    @Override
    public void stop() {
        System.out.println("===> stop zk");
        if (Objects.nonNull(treeCache)) {
            treeCache.close();
        }
        this.client.close();
    }

    @Override
    public void register(ServiceMeta serviceMeta, InstanceMeta instanceMeta) {
        try {
            String servicePath = Strings.lenientFormat("/%s", serviceMeta.toPath());
            if (Objects.isNull(client.checkExists().forPath(servicePath))) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath);
            }
            String instancePath = Strings.lenientFormat("%s/%s", servicePath, instanceMeta.toPath());
            if (Objects.isNull(client.checkExists().forPath(instancePath))) {
                client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath);
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(ServiceMeta serviceMeta, InstanceMeta instanceMeta) {
        try {
            String servicePath = Strings.lenientFormat("/%s", serviceMeta.toPath());
            if (Objects.isNull(client.checkExists().forPath(servicePath))) {
                return;
            }
            String instancePath = Strings.lenientFormat("%s/%s", servicePath, instanceMeta.toPath());
            if (Objects.nonNull(client.checkExists().forPath(instancePath))) {
                client.delete().quietly().forPath(instancePath);
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<InstanceMeta> findAll(ServiceMeta serviceMeta) {
        try {
            String servicePath = Strings.lenientFormat("/%s", serviceMeta.toPath());
            List<String> nodes = client.getChildren().forPath(servicePath);
            List<InstanceMeta> result = nodes.stream()
                    .map(node -> InstanceMeta.builder()
                            .schema("http")
                            .host(node.split("_")[0])
                            .port(Integer.valueOf(node.split("_")[1]))
                            .build())
                    .collect(Collectors.toList());
            return result;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(ServiceMeta serviceMeta, EventListener eventListener) {
        try {
            String servicePath = Strings.lenientFormat("/%s", serviceMeta.toPath());
            treeCache = TreeCache.newBuilder(client, servicePath)
                    .setCacheData(true)
                    .setMaxDepth(2)
                    .build();
            treeCache.getListenable().addListener((CuratorFramework cf, TreeCacheEvent event) -> {
                    System.out.println("===> subscribe :" + serviceMeta);
                    List<InstanceMeta> nodes = findAll(serviceMeta);
                    nodes.forEach(System.out::println);
                    eventListener.fire(new Event(nodes));
                });
            treeCache.start();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
