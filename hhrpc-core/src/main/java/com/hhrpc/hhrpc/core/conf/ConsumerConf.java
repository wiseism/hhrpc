package com.hhrpc.hhrpc.core.conf;

import com.hhrpc.hhrpc.core.api.LoadBalance;
import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.api.Router;
import com.hhrpc.hhrpc.core.cluster.RoundBionLoadBalance;
import com.hhrpc.hhrpc.core.consumer.ConsumerBootStrap;
import com.hhrpc.hhrpc.core.register.ZkRegisterCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @Date 2024/3/12
 * @Author lifei
 */
@Configuration
public class ConsumerConf {

    @Value("${hhrpc.providers}")
    private String urls;

    @Bean
    public ConsumerBootStrap consumerBootStrap() {
        return new ConsumerBootStrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner runScreenConsumerServiceFields(ConsumerBootStrap consumerBootStrap) {
        return x -> {
            consumerBootStrap.screenConsumerServiceFields();
        };
    }

    @Bean
    public Router router() {
        return Router.DEFAULT;
    }

    @Bean
    public LoadBalance loadBalance() {
        return new RoundBionLoadBalance();
    }


    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegisterCenter registerCenter() {
       return new ZkRegisterCenter();
    }

}
