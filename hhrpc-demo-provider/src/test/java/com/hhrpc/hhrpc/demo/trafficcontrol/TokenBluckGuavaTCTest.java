package com.hhrpc.hhrpc.demo.trafficcontrol;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;

/**
 * @Date 2024/4/30
 * @Author lifei
 */
public class TokenBluckGuavaTCTest {

    @Test
    public void testTC() {
        try {
            // 每秒10个令牌
            RateLimiter rateLimiter = RateLimiter.create(10);
            Thread.sleep(1000 * 15);
            for (int j = 0; j < 5; j++) {
                Thread.sleep(1000);
                for (int i = 0; i < 20; i++) {
                    if (rateLimiter.tryAcquire()) {
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
