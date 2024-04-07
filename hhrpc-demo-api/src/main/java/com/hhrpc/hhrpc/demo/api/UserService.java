package com.hhrpc.hhrpc.demo.api;

public interface UserService {

    User findById(Integer uid);
    User findById(Integer uid, String name);

    Integer findIdNum(Integer uid);

    long findLongId(long uid);

    User findUser(User user);

    User findTimeout(long timeout);

    void updateTimeoutPorts(String ports);

}
