package com.hhrpc.hhrpc.demo.provider;

import com.hhrpc.hhrpc.core.conf.ProviderGrayConf;
import jakarta.annotation.Resource;
import org.apache.curator.test.TestingServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @Date 2024/4/13
 * @Author lifei
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProviderGrayConfTest {

    private static TestingServer testingServer;

    @Resource
    private ProviderGrayConf providerGrayConf;

    @BeforeClass
    public static void init() {
        try {
            testingServer = new TestingServer(2182);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test01() {
        System.out.println(providerGrayConf.getMetas());
    }

    @AfterClass
    public static void stop() {
        try {
            testingServer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
