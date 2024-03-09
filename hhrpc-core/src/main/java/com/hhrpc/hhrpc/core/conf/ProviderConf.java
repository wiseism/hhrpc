package com.hhrpc.hhrpc.core.conf;

import com.hhrpc.hhrpc.core.provider.ProviderBootStarp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Date 2024/3/9
 * @Author lifei
 */
@Configuration
public class ProviderConf {

    @Bean
    public ProviderBootStarp providerBootStarp() {
        return new ProviderBootStarp();
    }
}
