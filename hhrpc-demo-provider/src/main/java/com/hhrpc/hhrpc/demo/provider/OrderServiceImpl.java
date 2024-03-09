package com.hhrpc.hhrpc.demo.provider;

import com.hhrpc.hhrpc.core.annotation.HhRpcProvider;
import com.hhrpc.hhrpc.demo.api.Order;
import com.hhrpc.hhrpc.demo.api.OrderService;
import org.springframework.stereotype.Service;

/**
 * @Date 2024/3/9
 * @Author lifei
 */
@HhRpcProvider
@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public Order findById(Integer oid) {
        return new Order(oid, "hh-order-" + System.currentTimeMillis());
    }
}
