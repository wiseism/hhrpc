package com.hhrpc.hhrpc.demo.provider;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.hhrpc.hhrpc.core.annotation.HhRpcProvider;
import com.hhrpc.hhrpc.core.api.RpcContent;
import com.hhrpc.hhrpc.demo.api.User;
import com.hhrpc.hhrpc.demo.api.UserService;
import jakarta.annotation.Resource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @Date 2024/3/9
 * @Author lifei
 */
@HhRpcProvider
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private Environment environment;

    @Override
    public User findById(Integer uid) {
        String port = environment.getProperty("server.port");
        return new User(uid, "hh-" +port+"-"+ System.currentTimeMillis());
    }

    @Override
    public User findById(Integer uid, String name) {
        return new User(uid, name);
    }

    @Override
    public Integer findIdNum(Integer uid) {
        return uid;
    }

    @Override
    public long findLongId(long uid) {
        return uid;
    }

    @Override
    public User findUser(User user) {
        return user;
    }

    private String timeoutPorts = "8081";

    @Override
    public User findTimeout(long timeout) {
        String port = environment.getProperty("server.port");
        Set<String> timeoutPortSet = Sets.newHashSet(Splitter.on(",").trimResults().omitEmptyStrings().split(timeoutPorts));
        if (timeoutPortSet.contains(port)) {
            try {
                Thread.sleep(timeout);
            }catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return new User(201, "timeout-"+port+"-"+System.currentTimeMillis());
    }

    @Override
    public void updateTimeoutPorts(String ports) {
        this.timeoutPorts = Optional.ofNullable(ports).orElse("");
    }

    @Override
    public String echoContextParameter(String key) {
        System.out.println("===> RpcContext parameters: ");
        RpcContent.contextParameters.get().forEach((k, v) -> System.out.println(k + "=" + v));
        return RpcContent.getContextParameter(key);
    }
}
