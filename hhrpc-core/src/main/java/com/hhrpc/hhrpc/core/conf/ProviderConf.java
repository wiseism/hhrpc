package com.hhrpc.hhrpc.core.conf;

import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.provider.ProviderBootStarp;
import com.hhrpc.hhrpc.core.provider.ProviderInvoker;
import com.hhrpc.hhrpc.core.register.ZkRegisterCenter;
import com.hhrpc.hhrpc.core.transport.SpringBootTransport;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

/**
 * @Date 2024/3/9
 * @Author lifei
 */
@Configuration
@Import({ProviderGrayConf.class, SpringBootTransport.class})
public class ProviderConf {

    @Bean
    public ProviderBootStarp providerBootStarp() {
        return new ProviderBootStarp();
    }

    @Bean//(initMethod = "start", destroyMethod = "stop")
    public RegisterCenter registerCenter() {
        return new ZkRegisterCenter();
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
