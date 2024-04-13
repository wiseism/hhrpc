package com.hhrpc.hhrpc.core.conf;

import com.google.common.collect.Maps;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @Date 2024/4/13
 * @Author lifei
 */
@Configuration
@ConfigurationProperties(prefix = "app")
public class ProviderGrayConf {

    private Map<String, String> metas = Maps.newHashMap();

    public Map<String, String> getMetas() {
        return metas;
    }
}
