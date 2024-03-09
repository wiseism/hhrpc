package com.hhrpc.hhrpc.demo.provider;

import com.hhrpc.hhrpc.core.annotation.HhRpcProvider;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.conf.ProviderConf;
import com.hhrpc.hhrpc.core.provider.ProviderBootStarp;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@RestController
@SpringBootApplication
@Import(ProviderConf.class)
public class HhrpcDemoProviderApplication {

    @Resource
    private ProviderBootStarp providerBootStarp;

    public static void main(String[] args) {
        SpringApplication.run(HhrpcDemoProviderApplication.class, args);
    }

    // HTTP + JSON 的方式，提供通信
    // RpcRquest 请求参数
    // RpcResponse 响应参数
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public RpcResponse invoke(@RequestBody RpcRequest rpcRequest) {
        return providerBootStarp.invokeMethod(rpcRequest);
    }






}
