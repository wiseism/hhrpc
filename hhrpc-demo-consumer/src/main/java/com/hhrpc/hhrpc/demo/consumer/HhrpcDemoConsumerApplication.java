package com.hhrpc.hhrpc.demo.consumer;

import com.hhrpc.hhrpc.core.annotation.HhRpcConsumer;
import com.hhrpc.hhrpc.core.conf.ConsumerConf;
import com.hhrpc.hhrpc.demo.api.Order;
import com.hhrpc.hhrpc.demo.api.OrderService;
import com.hhrpc.hhrpc.demo.api.User;
import com.hhrpc.hhrpc.demo.api.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ConsumerConf.class)
public class HhrpcDemoConsumerApplication {

    @HhRpcConsumer
    private UserService userService;

    @HhRpcConsumer
    private OrderService orderService;

    public static void main(String[] args) {
        SpringApplication.run(HhrpcDemoConsumerApplication.class, args);
    }


    @Bean
    public ApplicationRunner runServiceMethods() {
        return (x) -> {
            User user = this.userService.findById(1120);
            System.out.println(user);

            Integer uid = this.userService.findIdNum(2510);
            System.out.println(uid);

            Order order = orderService.findById(100000);
            System.out.println(order);

        };
    }
}
