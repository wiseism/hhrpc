package com.hhrpc.hhrpc.core.consumer;

import com.hhrpc.hhrpc.core.annotation.HhRpcConsumer;
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
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public void screenConsumerServiceFields() throws IllegalAccessException {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            // 获取这个bean中，所有带有 @HhRpcConsumer 注解的字段
            List<Field> fieldList = findServiceFields(bean.getClass());
            if (Objects.isNull(fieldList) || fieldList.size()==0) {
                continue;
            }
            for (Field field : fieldList) {
                String serviceName = field.getType().getCanonicalName();
                Object targetObj = null;
                if (proxyMap.containsKey(serviceName)) {
                    targetObj = proxyMap.get(serviceName);
                } else {
                    targetObj = createProxyObject(field.getType());
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
    private Object createProxyObject(Class<?> clazz) {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz},
                new HhRpcConsumerInvocationHandler(clazz.getCanonicalName()));
    }

    /**
     * 获取所有带有  @HhRpcConsumer 注解的字段
     * @param clazz
     * @return
     */
    private List<Field> findServiceFields(Class<?> clazz) {
        List<Field> result = new ArrayList<>();
        while (Objects.nonNull(clazz)) {
            List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(HhRpcConsumer.class))
                    .toList();
            if (Objects.nonNull(fields) && fields.size() > 0) {
                result.addAll(fields);
            }
            clazz = clazz.getSuperclass();
        }
        return result;
    }
}
