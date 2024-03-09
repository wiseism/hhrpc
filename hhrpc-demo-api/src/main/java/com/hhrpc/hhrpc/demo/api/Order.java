package com.hhrpc.hhrpc.demo.api;

/**
 * @Date 2024/3/9
 * @Author lifei
 */
public class Order {
    private Integer oid;
    private String name;

    public Order(Integer oid, String name) {
        this.oid = oid;
        this.name = name;
    }

    public Order() {
    }

    public Integer getOid() {
        return oid;
    }

    public void setOid(Integer oid) {
        this.oid = oid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Order{" +
                "oid=" + oid +
                ", name='" + name + '\'' +
                '}';
    }
}
