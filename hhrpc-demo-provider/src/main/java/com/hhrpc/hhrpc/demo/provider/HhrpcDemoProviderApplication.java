package com.hhrpc.hhrpc.demo.provider;

import com.google.common.base.Strings;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.conf.ProviderBusConf;
import com.hhrpc.hhrpc.core.conf.ProviderConf;
import com.hhrpc.hhrpc.core.provider.ProviderInvoker;
import com.hhrpc.hhrpc.demo.api.UserService;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;

@RestController
@SpringBootApplication
@Import(ProviderConf.class)
public class HhrpcDemoProviderApplication {

    @Resource
    private UserService userService;

    @Resource
    private ProviderInvoker providerInvoker;

    public static void main(String[] args) {
        SpringApplication.run(HhrpcDemoProviderApplication.class, args);
    }



    @RequestMapping(value = "/updateTimeoutPorts")
    public RpcResponse<String> updateTimeoutPorts(@RequestParam("timeoutPorts") String timeoutPorts) {
        userService.updateTimeoutPorts(timeoutPorts);
        RpcResponse<String> result = new RpcResponse<>();
        result.setStatus(true);
        result.setData("success");
        return result;
    }

    @Resource
    private ProviderBusConf providerBusConf;

    @RequestMapping(value = "/findCf")
    public String findCf() {
        return providerBusConf.getMetas().toString();
    }


    @Bean
    public ApplicationRunner runTestDemo() {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName("com.hhrpc.hhrpc.demo.api.UserService");
        rpcRequest.setMethodSign("findById#java.lang.Integer");
        rpcRequest.setArgs(new Object[]{1});
        return args -> {
//            for (int i = 0; i < 100; i++) {
//                Thread.sleep(1000);
//                RpcResponse<Object> result = providerInvoker.invokeMethod(rpcRequest);
//                if (result.getStatus()) {
//                    System.out.println(Strings.lenientFormat("第%s次，请求成功：%s", i+1, result.getData()));
//                } else {
//                    System.out.println(Strings.lenientFormat("第%s次，请求失败，错误代码：%s", i+1, result.getErrorCode()));
//                }
//            }
        };
    }






}
