package com.hhrpc.hhrpc.core.conf;

import com.google.common.base.MoreObjects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Date 2024/4/14
 * @Author lifei
 */
@Configuration
@ConfigurationProperties("hhrpc.consumer")
public class ConsumerBusConf {
    private Integer retries = 2;
    private Integer grayRatio = 10;
    private Integer faultLimit = 10;
    private Long halfOpenInitDelay = 10_000L;
    private Long halfOpenDelay = 60_000L;
    private Long timeout = 1000L;

    public Integer getRetries() {
        return retries;
    }

    public Integer getGrayRatio() {
        return grayRatio;
    }

    public Integer getFaultLimit() {
        return faultLimit;
    }

    public Long getHalfOpenInitDelay() {
        return halfOpenInitDelay;
    }

    public Long getHalfOpenDelay() {
        return halfOpenDelay;
    }

    public Long getTimeout() {
        return timeout;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(ConsumerConf.class)
                .add("retries", retries)
                .add("grayRatio", grayRatio)
                .add("faultLimit", faultLimit)
                .add("halfOpenInitDelay", halfOpenInitDelay)
                .add("halfOpenDelay", halfOpenDelay)
                .add("timeout", timeout)
                .toString();
    }
}
