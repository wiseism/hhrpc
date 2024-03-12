package com.hhrpc.hhrpc.core.conf;

import com.hhrpc.hhrpc.core.consumer.ConsumerBootStrap;
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
}
