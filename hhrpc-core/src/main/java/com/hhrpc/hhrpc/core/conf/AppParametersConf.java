package com.hhrpc.hhrpc.core.conf;

import com.google.common.base.MoreObjects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Date 2024/4/14
 * @Author lifei
 */
@Configuration
@ConfigurationProperties(prefix = "hhrpc.app")
public class AppParametersConf {
    private String id = "hhrpc";
    private String namespace = "public";
    private String env = "dev";

    public String getId() {
        return id;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getEnv() {
        return env;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(AppParametersConf.class)
                .add("id", id)
                .add("namespace", namespace)
                .add("env", env)
                .toString();
    }
}
