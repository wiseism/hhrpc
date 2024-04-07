package com.hhrpc.hhrpc.demo.provider;

import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.conf.ProviderConf;
import com.hhrpc.hhrpc.core.provider.ProviderInvoker;
import com.hhrpc.hhrpc.demo.api.UserService;
import jakarta.annotation.Resource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;

@RestController
@SpringBootApplication
@Import(ProviderConf.class)
public class HhrpcDemoProviderApplication {

    @Resource
    private ProviderInvoker providerInvoker;

    @Resource
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(HhrpcDemoProviderApplication.class, args);
    }

    // HTTP + JSON 的方式，提供通信
    // RpcRquest 请求参数
    // RpcResponse 响应参数
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public RpcResponse invoke(@RequestBody RpcRequest rpcRequest) {
        return providerInvoker.invokeMethod(rpcRequest);
    }

    @RequestMapping(value = "/updateTimeoutPorts")
    public RpcResponse<String> updateTimeoutPorts(@RequestParam("timeoutPorts") String timeoutPorts) {
        userService.updateTimeoutPorts(timeoutPorts);
        RpcResponse<String> result = new RpcResponse<>();
        result.setStatus(true);
        result.setData("success");
        return result;
    }






}
