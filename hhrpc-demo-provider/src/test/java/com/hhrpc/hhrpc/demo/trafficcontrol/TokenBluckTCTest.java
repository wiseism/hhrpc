package com.hhrpc.hhrpc.demo.trafficcontrol;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Date 2024/4/30
 * @Author lifei
 */
public class TokenBluckTCTest {

    // 流量大小
    private static int trafficControl = 10;
    // 时间窗口
    private static final int timeWinow = 20;

    private static final AtomicInteger requestCount = new AtomicInteger(0);

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    static {
        scheduledExecutorService.scheduleWithFixedDelay(()->{
            requestCount.set(trafficControl);
        }, 0, timeWinow, TimeUnit.SECONDS);
    }

    public static synchronized boolean tryAcquire() {
        return requestCount.decrementAndGet()>=0;
    }


    @Test
    public void testTC() {
        try {
            Thread.sleep(1000 * 15);
            for (int i = 0; i < 100; i++) {
                Thread.sleep(1000);
                if (tryAcquire()) {
                    System.out.println(Strings.lenientFormat("第%s次，请求成功", i+1));
                } else {
                    System.out.println(Strings.lenientFormat("第%s次，被限流了!", i+1));
                }
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
