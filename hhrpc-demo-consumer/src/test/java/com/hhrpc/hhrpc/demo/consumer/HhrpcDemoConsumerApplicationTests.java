package com.hhrpc.hhrpc.demo.consumer;

import com.hhrpc.hhrpc.demo.api.User;
import com.hhrpc.hhrpc.demo.provider.HhrpcDemoProviderApplication;
import org.apache.curator.test.TestingServer;
import org.junit.*;
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

    private static TestingServer testingServer;
    private static ApplicationContext providerApplicationContext;

    @BeforeClass
    public static void beforeClassTest() {
        try {
            testingServer = new TestingServer(2182);
            providerApplicationContext = SpringApplication.run(HhrpcDemoProviderApplication.class, new String[]{
                    "--server.port=8085", "--hhrpc.zkservers=localhost:2182"
            });
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    @Before
//    public void start() {
//        providerApplicationContext = SpringApplication.run(HhrpcDemoProviderApplication.class, new String[]{
//                "--server.port=8085"
//        });
//    }

    @AfterClass
    public static void afterClassTest() {
        try {
            testingServer.close();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
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
