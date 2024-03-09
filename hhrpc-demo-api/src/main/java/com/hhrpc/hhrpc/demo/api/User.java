package com.hhrpc.hhrpc.demo.api;

/**
 * @Date 2024/3/9
 * @Author lifei
 */
public class User {

    private Integer uid;
    private String name;

    public User() {
    }

    public User(Integer uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                '}';
    }
}
