package com.hhrpc.hhrpc.demo.provider;

import org.apache.curator.test.TestingServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HhrpcDemoProviderApplicationTests {

    private static TestingServer testingServer;

    @BeforeClass
    public static void beforeClassTest() {
        try {
            testingServer = new TestingServer(2182);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void contextLoads() {
    }

    @AfterClass
    public static void afterClassTest() {
        try {
            testingServer.close();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
