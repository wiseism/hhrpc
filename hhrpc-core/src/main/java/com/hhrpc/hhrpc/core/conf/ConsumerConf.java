package com.hhrpc.hhrpc.core.conf;

import com.hhrpc.hhrpc.core.api.*;
import com.hhrpc.hhrpc.core.cluster.GrayRouter;
import com.hhrpc.hhrpc.core.cluster.RoundBionLoadBalance;
import com.hhrpc.hhrpc.core.consumer.ConsumerBootStrap;
import com.hhrpc.hhrpc.core.consumer.HttpInvoker;
import com.hhrpc.hhrpc.core.consumer.http.OkHttpInvoker;
import com.hhrpc.hhrpc.core.filter.ParameterFilter;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import com.hhrpc.hhrpc.core.register.HhRegsiterCenter;
import com.hhrpc.hhrpc.core.register.ZkRegisterCenter;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * @Date 2024/3/12
 * @Author lifei
 */
@Configuration
@Import({AppParametersConf.class, ConsumerBusConf.class})
public class ConsumerConf {

    @Value("${hhrpc.providers:http://localhost:8081,http://localhost:8082,http://localhost:8083}")
    private String urls;

    @Resource
    private ConsumerBusConf consumerBusConf;

    @Value("${hhrpc.zk.servers:localhost:2181}")
    private String zkServers;

    @Value("${hhrpc.zk.root:hhrpc}")
    private String zkRoot;

    @Bean
    public ConsumerBootStrap consumerBootStrap() {
        return new ConsumerBootStrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner runScreenConsumerServiceFields(ConsumerBootStrap consumerBootStrap) {
        return x -> {
            consumerBootStrap.start();
        };
    }

    @Bean
    public Router<InstanceMeta> router() {
        return new GrayRouter(consumerBusConf.getGrayRatio());
    }

    @Bean
    public LoadBalance<InstanceMeta> loadBalance() {
        return new RoundBionLoadBalance();
    }


    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean
    public RegisterCenter registerCenter() {
//       return new ZkRegisterCenter(zkServers, zkRoot);
        return new HhRegsiterCenter();
    }


    @Bean
    public HttpInvoker httpInvoker() {
        return new OkHttpInvoker(consumerBusConf.getTimeout());
    }

    @Bean
    public Filter filter01() {
        return Filter.DEFAULT;
    }

    @Bean
    public Filter parameterFilter() {
        return new ParameterFilter();
    }

//    @Bean
//    public Filter filter02() {
//        return new CacheFilter();
////        return new MockFilter();
//    }

    @Bean
    public RpcContext rpcContent(List<Filter> filterList,
                                 Router<InstanceMeta> router,
                                 LoadBalance<InstanceMeta> loadBalance,
                                 HttpInvoker httpInvoker, AppParametersConf appParametersConf) {
        RpcContext rpcContext = new RpcContext();
        rpcContext.setFilterList(filterList);
        rpcContext.setRouter(router);
        rpcContext.setLoadBalance(loadBalance);
        rpcContext.setHttpInvoker(httpInvoker);
        rpcContext.getParameters().put("consumer.retries", String.valueOf(consumerBusConf.getRetries()));
        rpcContext.getParameters().put("consumer.faultLimit", String.valueOf(consumerBusConf.getFaultLimit()));
        rpcContext.getParameters().put("consumer.halfOpenInitDelay", String.valueOf(consumerBusConf.getHalfOpenInitDelay()));
        rpcContext.getParameters().put("consumer.halfOpenDelay", String.valueOf(consumerBusConf.getHalfOpenDelay()));
        rpcContext.getParameters().put("consumer.timeout", String.valueOf(consumerBusConf.getTimeout()));
        rpcContext.getParameters().put("app.id", appParametersConf.getId());
        rpcContext.getParameters().put("app.namespace", appParametersConf.getNamespace());
        rpcContext.getParameters().put("app.env", appParametersConf.getEnv());
        return rpcContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public ApolloChangeListener apolloChangeListener() {
        return new ApolloChangeListener();
    }
}
