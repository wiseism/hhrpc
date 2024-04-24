package com.hhrpc.hhrpc.core.annotation;

import com.hhrpc.hhrpc.core.conf.ConsumerConf;
import com.hhrpc.hhrpc.core.conf.ProviderConf;
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
@Import({ProviderConf.class, ConsumerConf.class})
public @interface EnableHHrpc {
}
