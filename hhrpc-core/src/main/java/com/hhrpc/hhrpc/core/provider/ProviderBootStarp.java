package com.hhrpc.hhrpc.core.provider;

import com.hhrpc.hhrpc.core.annotation.HhRpcProvider;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Date 2024/3/9
 * @Author lifei
 */
public class ProviderBootStarp implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private Map<String, Object> serviceMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // 对象创建完，还没有初始化的时候，获取到所有的服务提供者
    @PostConstruct
    public void buildProviders() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(HhRpcProvider.class);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Class<?>[] interfaces = entry.getValue().getClass().getInterfaces();
            for (Class<?> anInterface : interfaces) {
                serviceMap.put(anInterface.getName(), entry.getValue());
            }
        }
    }





    public RpcResponse invokeMethod(RpcRequest rpcRequest) {
        RpcResponse result = new RpcResponse();
        try {
            String serviceName = rpcRequest.getServiceName();
            Object providerObj = serviceMap.get(serviceName);
            Method method = findMethod(providerObj, rpcRequest.getMethodName());
            Object data = method.invoke(providerObj, rpcRequest.getArgs());
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

    private Method findMethod(Object providerObj, String methodName) {
        Method[] methods = providerObj.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

}
