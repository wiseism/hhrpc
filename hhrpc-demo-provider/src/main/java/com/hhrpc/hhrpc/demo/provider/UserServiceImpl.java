package com.hhrpc.hhrpc.demo.provider;

import com.hhrpc.hhrpc.core.annotation.HhRpcProvider;
import com.hhrpc.hhrpc.demo.api.User;
import com.hhrpc.hhrpc.demo.api.UserService;
import jakarta.annotation.Resource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

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
}
