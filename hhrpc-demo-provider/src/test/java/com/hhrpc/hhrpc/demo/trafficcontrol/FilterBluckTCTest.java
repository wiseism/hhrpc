package com.hhrpc.hhrpc.demo.trafficcontrol;

import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Date 2024/4/30
 * @Author lifei
 */
public class FilterBluckTCTest {

    // 桶的容量
    private static final int capcity = 10;
    // 漏水的速度
    private static final int rate = 20;

    private static final AtomicInteger requestCount = new AtomicInteger(0);

    private static Long lastRequestTime = System.currentTimeMillis();

    public static synchronized boolean tryAcquire() {
        // 判断桶里是不是空的
        if (requestCount.get()==0) {
            requestCount.incrementAndGet();
            lastRequestTime = System.currentTimeMillis();
            return true;
        }

        // 上次请求的时间到 本次请求的时间 之间，漏了多少水
        int timeOff = (int)((System.currentTimeMillis() - lastRequestTime)/1000);
        int useNum = timeOff * rate;
        // 桶中剩余的水
        int leftNum = Math.max(0, requestCount.get() - useNum);
        requestCount.set(leftNum);
        lastRequestTime = System.currentTimeMillis();
        // 判断
        if (requestCount.incrementAndGet() > capcity) {
            return false;
        }
        return true;
    }

    @Test
    public void testTC() {
        try {
            Thread.sleep(1000 * 15);
            for (int j = 0; j < 5; j++) {
                Thread.sleep(1000);
                for (int i = 0; i < 20; i++) {
                    if (tryAcquire()) {
                        System.out.println(Strings.lenientFormat("第%s次，请求成功", i+1));
                    } else {
                        System.out.println(Strings.lenientFormat("第%s次，被限流了!", i+1));
                    }
                }
            }

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
