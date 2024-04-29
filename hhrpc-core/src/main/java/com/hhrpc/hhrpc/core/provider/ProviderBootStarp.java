package com.hhrpc.hhrpc.core.provider;

import com.hhrpc.hhrpc.core.annotation.HhRpcProvider;
import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.conf.AppParametersConf;
import com.hhrpc.hhrpc.core.conf.ProviderBusConf;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.meta.ProviderMeta;
import com.hhrpc.hhrpc.core.meta.ServiceMeta;
import com.hhrpc.hhrpc.core.utils.HhrpcMethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Map;

/**
 * @Date 2024/3/9
 * @Author lifei
 */
public class ProviderBootStarp implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private MultiValueMap<String, ProviderMeta> serviceMap = new LinkedMultiValueMap<>();

    private InstanceMeta instanceMeta;
    private RegisterCenter registerCenter;

    private final Integer port;
    private final ProviderBusConf providerBusConf;
    private final AppParametersConf appParametersConf;

    public ProviderBootStarp(Integer port, ProviderBusConf providerBusConf, AppParametersConf appParametersConf) {
        this.port = port;
        this.providerBusConf = providerBusConf;
        this.appParametersConf = appParametersConf;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // 对象创建完，还没有初始化的时候，获取到所有的服务提供者
    @PostConstruct
    public void init() {
        try {
            instanceMeta = createInstanceMeta();
            Map<String, Object> beans = applicationContext.getBeansWithAnnotation(HhRpcProvider.class);
            for (Map.Entry<String, Object> entry : beans.entrySet()) {
                Class<?>[] interfaces = entry.getValue().getClass().getInterfaces();
                for (Class<?> anInterface : interfaces) {
                    Method[] methods = anInterface.getMethods();
                    for (Method method : methods) {
                        if (HhrpcMethodUtils.checkLocalMethod(method)) {
                            continue;
                        }
                        ProviderMeta providerMeta = new ProviderMeta();
                        providerMeta.setMethod(method);
                        providerMeta.setMethodSign(HhrpcMethodUtils.createeMethodSign(method));
                        providerMeta.setServiceImpl(entry.getValue());
                        serviceMap.add(anInterface.getCanonicalName(), providerMeta);
                    }
                }
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private InstanceMeta createInstanceMeta() {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            InstanceMeta result = InstanceMeta.builder()
                    .schema("http")
                    .host(hostAddress)
                    .port(port)
                    .context("hhrpc")
                    .build();
            result.getParameters().putAll(providerBusConf.getMetas());
            return result;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        // 启动zk
        registerCenter = applicationContext.getBean(RegisterCenter.class);
//        registerCenter.start();
        // 注册
        register();
    }

    @PreDestroy
    public void stop() {
        unregister();
    }


    private void register() {
        serviceMap.keySet().forEach(service->{
            registerCenter.register(createServiceMeta(service), instanceMeta);
        });
    }

    private ServiceMeta createServiceMeta(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .namespace(appParametersConf.getNamespace())
                .app(appParametersConf.getId())
                .env(appParametersConf.getEnv())
                .name(service)
                .build();
        return serviceMeta;
    }

    private void unregister() {
        RegisterCenter registerCenter = applicationContext.getBean(RegisterCenter.class);
        serviceMap.keySet().forEach(service->{
            registerCenter.unregister(createServiceMeta(service), instanceMeta);
        });
    }

    public MultiValueMap<String, ProviderMeta> getServiceMap() {
        return serviceMap;
    }

    public ProviderBusConf getProviderBusConf() {
        return providerBusConf;
    }
}
