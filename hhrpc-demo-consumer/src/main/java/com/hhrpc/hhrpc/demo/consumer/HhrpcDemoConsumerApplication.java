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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
            System.out.println(orderService.toString());
            System.out.println(userService.findById(1120, "aaa"));

            System.out.println(userService.findLongId(123L));
            System.out.println(userService.findUser(new User(1, "aa")));
            System.out.println(Arrays.toString(orderService.findLongArray(new long[]{1l, 2l, 3l, 4l, 5l})));
            System.out.println(orderService.findLongOrder(Arrays.asList(new Order(1, "aa"), new Order(2, "bb2"))));
            Map<String, Order> map = new HashMap<>();
            map.put("001", new Order(111, "map01"));
            map.put("002", new Order(121, "map02"));
            System.out.println(orderService.findMapOrder(map));
        };
    }
}
