package com.hhrpc.hhrpc.core.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Date 2024/4/14
 * @Author lifei
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({HhRpcProvider.class, HhRpcConsumer.class})
public @interface EnableHHrpc {
}
