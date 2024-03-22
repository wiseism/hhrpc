package com.hhrpc.hhrpc.core.register;

import com.google.common.base.Strings;
import com.hhrpc.hhrpc.core.api.Event;
import com.hhrpc.hhrpc.core.api.EventListener;
import com.hhrpc.hhrpc.core.api.RegisterCenter;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Objects;

/**
 * @Date 2024/3/22
 * @Author lifei
 */
public class ZkRegisterCenter implements RegisterCenter {

    private CuratorFramework client;


    @Override
    public void start() {
        final RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3, 1000);
        this.client = CuratorFrameworkFactory.builder()
                .retryPolicy(retryPolicy)
                .connectString("localhost:2181")
                .namespace("hhrpc")
                .build();
        this.client.start();
    }

    @Override
    public void stop() {
        this.client.close();
    }

    @Override
    public void register(String service, String instance) {
        try {
            String servicePath = Strings.lenientFormat("/%s", service);
            if (Objects.isNull(client.checkExists().forPath(servicePath))) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath);
            }
            String instancePath = Strings.lenientFormat("%s/%s", servicePath, instance);
            if (Objects.isNull(client.checkExists().forPath(instancePath))) {
                client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath);
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(String service, String instance) {
        try {
            String servicePath = Strings.lenientFormat("/%s", service);
            if (Objects.isNull(client.checkExists().forPath(servicePath))) {
                return;
            }
            String instancePath = Strings.lenientFormat("%s/%s", servicePath, instance);
            if (Objects.nonNull(client.checkExists().forPath(instancePath))) {
                client.delete().quietly().forPath(instancePath);
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> findAll(String service) {
        try {
            String servicePath = Strings.lenientFormat("/%s", service);
            List<String> nodes = client.getChildren().forPath(servicePath);
            return nodes;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(String service, EventListener eventListener) {
        try {
            String servicePath = Strings.lenientFormat("/%s", service);
            final TreeCache treeCache = TreeCache.newBuilder(client, servicePath)
                    .setCacheData(true)
                    .setMaxDepth(2)
                    .build();
            treeCache.getListenable().addListener(new TreeCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                    System.out.println("===> subscribe.");
                    List<String> nodes = findAll(service);
                    eventListener.fire(new Event(nodes));
                }
            });
            treeCache.start();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
