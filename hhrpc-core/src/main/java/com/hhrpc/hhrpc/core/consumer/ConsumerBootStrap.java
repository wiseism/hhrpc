package com.hhrpc.hhrpc.core.consumer;

import com.hhrpc.hhrpc.core.annotation.HhRpcConsumer;
import com.hhrpc.hhrpc.core.api.*;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.meta.ServiceMeta;
import com.hhrpc.hhrpc.core.utils.HhrpcMethodUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 *
 * @Date 2024/3/12
 * @Author lifei
 */
public class ConsumerBootStrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private Map<String, Object> proxyMap = new HashMap<>();

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public void start() throws IllegalAccessException {
        RegisterCenter registerCenter = applicationContext.getBean(RegisterCenter.class);
        RpcContext rpcContext = applicationContext.getBean(RpcContext.class);
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            // 获取这个bean中，所有带有 @HhRpcConsumer 注解的字段
            List<Field> fieldList = HhrpcMethodUtils.findAnnotationFields(bean.getClass(), HhRpcConsumer.class);
            if (Objects.isNull(fieldList) || fieldList.size()==0) {
                continue;
            }
            for (Field field : fieldList) {
                String serviceName = field.getType().getCanonicalName();
                Object targetObj = null;
                if (proxyMap.containsKey(serviceName)) {
                    targetObj = proxyMap.get(serviceName);
                } else {
                    targetObj = createProxyObject(field.getType(), rpcContext, registerCenter);
                    proxyMap.put(serviceName, targetObj);
                }
                field.setAccessible(true);
                field.set(bean, targetObj);
            }
        }
    }

    /**
     * 创建代理对象
     * @return
     */
    private Object createProxyObject(Class<?> clazz, RpcContext rpcContext, RegisterCenter registerCenter) {
        String serviceName = clazz.getCanonicalName();
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(serviceName)
                .env(rpcContext.param("app.env"))
                .namespace(rpcContext.param("app.namespace"))
                .app(rpcContext.param("app.id")).build();
        List<InstanceMeta> instanceMetaList = registerCenter.findAll(serviceMeta);
        registerCenter.subscribe(serviceMeta, (event)->{
            instanceMetaList.clear();
            instanceMetaList.addAll(event.getData());
        });
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz},
                new HhRpcConsumerInvocationHandler(clazz.getCanonicalName(), rpcContext, instanceMetaList));
    }

}
