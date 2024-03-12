package com.hhrpc.hhrpc.demo.provider;

import com.hhrpc.hhrpc.core.annotation.HhRpcProvider;
import com.hhrpc.hhrpc.demo.api.User;
import com.hhrpc.hhrpc.demo.api.UserService;
import org.springframework.stereotype.Service;

/**
 * @Date 2024/3/9
 * @Author lifei
 */
@HhRpcProvider
@Service
public class UserServiceImpl implements UserService {

    @Override
    public User findById(Integer uid) {
        return new User(uid, "hh-" + System.currentTimeMillis());
    }

    @Override
    public Integer findIdNum(Integer uid) {
        return uid;
    }
}
