package com.hhrpc.hhrpc.core.provider;

import com.hhrpc.hhrpc.core.annotation.HhRpcProvider;
import com.hhrpc.hhrpc.core.api.RpcRequest;
import com.hhrpc.hhrpc.core.api.RpcResponse;
import com.hhrpc.hhrpc.core.utils.HhrpcMethodUtils;
import com.hhrpc.hhrpc.core.utils.TypeUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Date 2024/3/9
 * @Author lifei
 */
public class ProviderBootStarp implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private MultiValueMap<String, ProviderMeta> serviceMap = new LinkedMultiValueMap<>();

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

}
