package com.hhrpc.hhrpc.demo.consumer;

import com.hhrpc.hhrpc.core.annotation.HhRpcConsumer;
import com.hhrpc.hhrpc.core.api.Router;
import com.hhrpc.hhrpc.core.api.RpcContent;
import com.hhrpc.hhrpc.core.cluster.GrayRouter;
import com.hhrpc.hhrpc.core.conf.ConsumerConf;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.demo.api.Order;
import com.hhrpc.hhrpc.demo.api.OrderService;
import com.hhrpc.hhrpc.demo.api.User;
import com.hhrpc.hhrpc.demo.api.UserService;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@Import(ConsumerConf.class)
@RestController
public class HhrpcDemoConsumerApplication {

    @HhRpcConsumer
    private UserService userService;

    @HhRpcConsumer
    private OrderService orderService;

    @RequestMapping(value = "/")
    public User findUserById(@RequestParam("id") int id) {
        return this.userService.findById(id);
    }

    @RequestMapping(value = "/findUserTimeout")
    public User findUserTimeout(@RequestParam("timeout") long timeout) {
        return this.userService.findTimeout(timeout);
    }

    @Resource
    private Router<InstanceMeta> router;

    @RequestMapping(value = "/updateGrayRatio")
    public Integer updateGrayRatio(@RequestParam("grayRatio") Integer grayRatio) {
        ((GrayRouter)router).setGrayRatio(grayRatio);
        return grayRatio;
    }

    public static void main(String[] args) {
        SpringApplication.run(HhrpcDemoConsumerApplication.class, args);
    }


    @Bean
    public ApplicationRunner runServiceMethods() {
        return (x) -> {
            testAll();
        };
    }

    private void testAll() {
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
        long[]  longArray= orderService.findLongArray(new long[]{1l, 2l, 3l, 4l, 5l});
        System.out.println(Arrays.toString(longArray));
        List<Order> longOrderList = orderService.findLongOrder(Arrays.asList(new Order(1, "aa"), new Order(2, "bb2")));
        System.out.println(longOrderList);
        Map<String, Order> map = new HashMap<>();
        map.put("001", new Order(111, "map01"));
        map.put("002", new Order(121, "map02"));
        Map<String, Order> mapOrder = orderService.findMapOrder(map);
        System.out.println(mapOrder);
        try {
            User timeoutUser = userService.findTimeout(1100);
            System.out.println(timeoutUser);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("===> 测试跨客户端和服务端的参数：");
        RpcContent.putContextParameter("tokenId", "001");
        RpcContent.putContextParameter("username", "aaa-002");
        String tokenId = userService.echoContextParameter("tokenId");
        System.out.println(tokenId);
        // 为了避免线程复用导致RpcContext参数串了，需要使用后清理
        RpcContent.contextParameters.get().clear();

    }
}
