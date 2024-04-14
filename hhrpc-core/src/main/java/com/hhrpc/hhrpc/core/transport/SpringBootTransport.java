package com.hhrpc.hhrpc.core.transport;

import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.provider.ProviderInvoker;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Date 2024/4/14
 * @Author lifei
 */
@RestController
public class SpringBootTransport {

    @Resource
    private ProviderInvoker providerInvoker;

    // HTTP + JSON 的方式，提供通信
    // RpcRquest 请求参数
    // RpcResponse 响应参数
    @RequestMapping(value = "/hhrpc", method = RequestMethod.POST)
    public RpcResponse invoke(@RequestBody RpcRequest rpcRequest) {
        return providerInvoker.invokeMethod(rpcRequest);
    }

}
