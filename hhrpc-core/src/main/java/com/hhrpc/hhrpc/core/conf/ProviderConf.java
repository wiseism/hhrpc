package com.hhrpc.hhrpc.core.conf;

import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.provider.ProviderBootStarp;
import com.hhrpc.hhrpc.core.provider.ProviderInvoker;
import com.hhrpc.hhrpc.core.register.ZkRegisterCenter;
import com.hhrpc.hhrpc.core.transport.SpringBootTransport;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

/**
 * @Date 2024/3/9
 * @Author lifei
 */
@Configuration
@Import({ProviderGrayConf.class, SpringBootTransport.class, AppParametersConf.class})
public class ProviderConf {

    @Value("${server.port:8081}")
    private Integer port;

    @Value("${hhrpc.zk.servers:localhost:2181}")
    private String zkServers;

    @Value("${hhrpc.zk.root:hhrpc}")
    private String zkRoot;

    @Resource
    private ProviderGrayConf providerGrayConf;

    @Resource
    private AppParametersConf appParametersConf;

    @Bean
    public ProviderBootStarp providerBootStarp() {
        return new ProviderBootStarp(port, providerGrayConf, appParametersConf);
    }

    @Bean(initMethod = "start")
    @ConditionalOnMissingBean
    public RegisterCenter registerCenter() {
        return new ZkRegisterCenter(zkServers, zkRoot);
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner runStart(ProviderBootStarp providerBootStarp) {
        return x->{
            providerBootStarp.start();
        };
    }

    @Bean
    public ProviderInvoker providerInvoker(ProviderBootStarp providerBootStarp) {
        return new ProviderInvoker(providerBootStarp);
    }
}
