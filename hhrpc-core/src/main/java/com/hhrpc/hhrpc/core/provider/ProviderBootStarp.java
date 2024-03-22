package com.hhrpc.hhrpc.core.provider;

import com.google.common.base.Strings;
import com.hhrpc.hhrpc.core.annotation.HhRpcProvider;
import com.hhrpc.hhrpc.core.api.RegisterCenter;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.utils.HhrpcMethodUtils;
import com.hhrpc.hhrpc.core.utils.TypeUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Date 2024/3/9
 * @Author lifei
 */
public class ProviderBootStarp implements ApplicationContextAware, EnvironmentAware {

    private ApplicationContext applicationContext;
    private Environment environment;
    private MultiValueMap<String, ProviderMeta> serviceMap = new LinkedMultiValueMap<>();

    private String instance;



    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // 对象创建完，还没有初始化的时候，获取到所有的服务提供者
    @PostConstruct
    public void buildProviders() {
        try {
            String port = environment.getProperty("server.port");
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            this.instance = Strings.lenientFormat("%s_%s", hostAddress, port);
            Map<String, Object> beans = applicationContext.getBeansWithAnnotation(HhRpcProvider.class);
            for (Map.Entry<String, Object> entry : beans.entrySet()) {
                Class<?>[] interfaces = entry.getValue().getClass().getInterfaces();
                for (Class<?> anInterface : interfaces) {
                    Method[] methods = anInterface.getMethods();
                    for (Method method : methods) {
                        if (HhrpcMethodUtils.checkLocalMethod(method)) {
                            continue;
                        }
                        ProviderMeta providerMeta = new ProviderMeta();
                        providerMeta.setMethod(method);
                        providerMeta.setMethodSign(HhrpcMethodUtils.createeMethodSign(method));
                        providerMeta.setServiceImpl(entry.getValue());
                        serviceMap.add(anInterface.getCanonicalName(), providerMeta);
                    }
                }
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        // 启动zk
        RegisterCenter registerCenter = applicationContext.getBean(RegisterCenter.class);
        registerCenter.start();
        // 注册
        register();
    }

    @PreDestroy
    public void stop() {
        unregister();
    }


    private void register() {
        RegisterCenter registerCenter = applicationContext.getBean(RegisterCenter.class);
        serviceMap.keySet().forEach(service->{
            registerCenter.register(service, instance);
        });
    }

    private void unregister() {
        RegisterCenter registerCenter = applicationContext.getBean(RegisterCenter.class);
        serviceMap.keySet().forEach(service->{
            registerCenter.unregister(service, instance);
        });
    }





    public RpcResponse invokeMethod(RpcRequest rpcRequest) {
        RpcResponse result = new RpcResponse();
        try {
            ProviderMeta providerMeta = fetchProviderMeta(rpcRequest);
            Method method = providerMeta.getMethod();
            Object[] args = TypeUtils.processArgs(rpcRequest.getArgs(), method.getParameterTypes(), method.getGenericParameterTypes());
            Object data = method.invoke(providerMeta.getServiceImpl(), args);
            result.setStatus(true);
            result.setData(data);
            return result;
        }catch (Exception e) {
            e.printStackTrace();
            result.setStatus(false);
            result.setData(null);
            return result;
        }
    }

    private ProviderMeta fetchProviderMeta(RpcRequest rpcRequest) {
        return serviceMap.get(rpcRequest.getServiceName()).stream()
                .filter(item->item.getMethodSign().equals(rpcRequest.getMethodSign()))
                .findAny()
                .orElse(null);
    }

    private Method findMethod(Object providerObj, String methodName) {
        Method[] methods = providerObj.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
