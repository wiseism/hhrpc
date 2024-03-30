package com.hhrpc.hhrpc.demo.consumer;

import com.hhrpc.hhrpc.demo.api.User;
import com.hhrpc.hhrpc.demo.provider.HhrpcDemoProviderApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HhrpcDemoConsumerApplicationTests {

    @Autowired
    private HhrpcDemoConsumerApplication hhrpcDemoConsumerApplication;

    private ApplicationContext providerApplicationContext;
    @Before
    public void start() {
        providerApplicationContext = SpringApplication.run(HhrpcDemoProviderApplication.class, new String[]{
                "--server.port=8085"
        });
    }

    @Test
    public void contextLoads() {
        User user = hhrpcDemoConsumerApplication.findUserById(80);
        System.out.println(user);
    }

    @After
    public void stop() {
        SpringApplication.exit(providerApplicationContext, ()->1);
    }

}
