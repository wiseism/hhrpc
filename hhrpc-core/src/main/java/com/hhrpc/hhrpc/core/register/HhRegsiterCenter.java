package com.hhrpc.hhrpc.core.register;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hhrpc.hhrpc.core.api.Event;
import com.hhrpc.hhrpc.core.api.EventListener;
import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.consumer.HttpInvoker;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.meta.ServiceMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Date 2024/5/25
 * @Author lifei
 */
public class HhRegsiterCenter implements RegisterCenter {

    private static final Logger log = LoggerFactory.getLogger(HhRegsiterCenter.class);
    private static final String RENEWS_PATH = "renews";
    private static final String REGISTER_PATH = "register";
    private static final String UNREGISTER_PATH = "unregister";
    private static final String FIND_ALL_PATH = "findAllInstances";
    private static final String VERSION_PATH = "version";

    @Value("${hhregister.servers}")
    private String registerServer;

    private final HhHealthChecker healthChecker = new HhHealthChecker();
    private final Map<String, Long> VERSIONS = Maps.newHashMap();
    private final MultiValueMap<InstanceMeta, ServiceMeta> INSTANCES = new LinkedMultiValueMap<>();

    @Override
    public void start() {
        log.debug("===> [hhregister] start...");
        healthChecker.start();
        providerHealthCheck();
    }

    private void providerHealthCheck() {
        healthChecker.providerHealthCheck(()->{
            log.debug("===> [hhregister] renew provider status....");
            INSTANCES.keySet().parallelStream().forEach(instance->{
                Long timestamp = HttpInvoker.httpPost(renewsPath(INSTANCES.get(instance)), new Gson().toJson(instance), Long.class);
                log.debug("===> [hhregister] timestamp {}, instance{}", timestamp, instance);
            });
        });
    }

    @Override
    public void stop() {
        log.debug("===> [hhregister] stop...");
        healthChecker.stop();
    }

    @Override
    public void register(ServiceMeta serviceMeta, InstanceMeta instanceMeta) {
        log.debug("===> [hhregister] register service {}, instance {}", serviceMeta, instanceMeta);
        String url = registerPath(serviceMeta);
        InstanceMeta instance = HttpInvoker.httpPost(url, new Gson().toJson(instanceMeta), InstanceMeta.class);
        if (Objects.nonNull(instance)) {
            INSTANCES.add(instance, serviceMeta);
        }
        log.debug("===> [hhregister] success register instance {}, service: {}, url: {}", instance, serviceMeta.toPath(), url);
    }

    @Override
    public void unregister(ServiceMeta serviceMeta, InstanceMeta instanceMeta) {
        log.debug("===> [hhregister] unregister service {}, instance {}", serviceMeta, instanceMeta);
        String url = unregisterPath(serviceMeta);
        InstanceMeta instance = HttpInvoker.httpPost(url, new Gson().toJson(instanceMeta), InstanceMeta.class);
        if (Objects.nonNull(instance)) {
            List<ServiceMeta> serverList = INSTANCES.get(instance);
            serverList.remove(serviceMeta);
        }

        log.debug("===> [hhregister] success unregister instance {}, service: {}, url: {}", instance, serviceMeta.toPath(), url);
    }

    @Override
    public List<InstanceMeta> findAll(ServiceMeta serviceMeta) {
        log.debug("===> [hhregister] findAll service: {}", serviceMeta);
        TypeToken<List<InstanceMeta>> typeToken = new TypeToken<>() {};
        List<InstanceMeta> instanceList = HttpInvoker.httpGet(findAllPath(serviceMeta), typeToken);
        log.debug("===> [hhregister] findAll service {}, instanceList: {}", serviceMeta, instanceList);
        return instanceList;
    }

    @Override
    public void subscribe(ServiceMeta serviceMeta, EventListener eventListener) {
        consumerHealthCheck(serviceMeta, eventListener);
    }

    private void consumerHealthCheck(ServiceMeta serviceMeta, EventListener eventListener) {
        healthChecker.consumerHealthCheck(()->{
            // 获取旧的版本号
            Long oldVersion = VERSIONS.getOrDefault(serviceMeta.toPath(), -1L);
            // 获取新的版本号
            Long newVersion = HttpInvoker.httpGet(versionPath(serviceMeta), Long.class);
            log.debug("===> [hhregister] subscribe oldVersion: {}, newVersion: {}", oldVersion, newVersion);
            if (Optional.ofNullable(newVersion).orElse(-1L) > oldVersion) {
                List<InstanceMeta> instanceList = findAll(serviceMeta);
                log.debug("===> [hhregister] subscribe *****refresh***** instanceList: {}", instanceList);
                eventListener.fire(new Event(instanceList));
                VERSIONS.put(serviceMeta.toPath(), newVersion);
            }
        });
    }

    private String renewsPath(List<ServiceMeta> serviceMetaList) {
        return path(RENEWS_PATH, serviceMetaList);
    }

    private String registerPath(ServiceMeta serviceMeta) {
        return path(REGISTER_PATH, serviceMeta);
    }

    private String unregisterPath(ServiceMeta serviceMeta) {
        return path(UNREGISTER_PATH, serviceMeta);
    }

    private String findAllPath(ServiceMeta serviceMeta) {
        return path(FIND_ALL_PATH, serviceMeta);
    }

    private String versionPath(ServiceMeta serviceMeta) {
        return path(VERSION_PATH, serviceMeta);
    }

    private String path(String context, ServiceMeta serviceMeta) {
        return Strings.lenientFormat("%s/hhregistryController/%s?server=%s", registerServer, context, serviceMeta.toPath());
    }

    private String path(String context, List<ServiceMeta> serviceMetaList) {
        String servers = serviceMetaList.stream().map(ServiceMeta::toPath).collect(Collectors.joining(","));
        return Strings.lenientFormat("%s/hhregistryController/%s?servers=%s", registerServer, context, servers);
    }
}
