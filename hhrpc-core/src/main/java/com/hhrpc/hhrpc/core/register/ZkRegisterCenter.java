package com.hhrpc.hhrpc.core.register;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Date 2024/3/22
 * @Author lifei
 */
public class ZkRegisterCenter implements RegisterCenter {

    private static final Logger log = LoggerFactory.getLogger(ZkRegisterCenter.class);

    private CuratorFramework client;
    private TreeCache treeCache;

    private final String zkServers;
    private final String zkRoot;

    public ZkRegisterCenter(String zkServers, String zkRoot) {
        this.zkServers = zkServers;
        this.zkRoot = zkRoot;
    }


    @Override
    public void start() {
        final RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder()
                .retryPolicy(retryPolicy)
                .connectString(zkServers)
                .namespace(zkRoot)
                .build();
        log.info("===> start zk...");
        this.client.start();
    }

    @Override
    public void stop() {
        log.info("===> stop zk");
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
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, serviceMeta.toMetas().getBytes());
            }
            String instancePath = Strings.lenientFormat("%s/%s", servicePath, instanceMeta.toPath());
            if (Objects.isNull(client.checkExists().forPath(instancePath))) {
                client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, instanceMeta.toMetas().getBytes());
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
            List<InstanceMeta> providers = mapToInstanceMeta(nodes, servicePath);
            log.debug("===> findAll from zk: ");
            providers.forEach(System.out::println);
            return providers;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<InstanceMeta> mapToInstanceMeta(List<String> nodes, String servicePath) {
        return nodes.stream()
                .map(node -> {
                    InstanceMeta instanceMeta = InstanceMeta.builder()
                            .schema("http")
                            .host(node.split("_")[0])
                            .port(Integer.valueOf(node.split("_")[1]))
                            .context(node.split("_")[2])
                            .build();
                    String path = Strings.lenientFormat("%s/%s", servicePath, node);
                    byte[] bytes;
                    try {
                        bytes = client.getData().forPath(path);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    TypeToken<?> mapToken = TypeToken.getParameterized(Map.class, String.class, String.class);
                    Map<String, String> parameters = new Gson().fromJson(new String(bytes), mapToken.getType());
                    instanceMeta.setParameters(parameters);
                    return instanceMeta;
                }).collect(Collectors.toList());
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
                    log.debug("===> subscribe :" + serviceMeta);
                    List<InstanceMeta> nodes = findAll(serviceMeta);
                    eventListener.fire(new Event(nodes));
                });
            treeCache.start();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
