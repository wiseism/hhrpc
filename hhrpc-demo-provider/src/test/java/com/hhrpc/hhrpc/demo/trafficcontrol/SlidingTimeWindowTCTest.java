package com.hhrpc.hhrpc.demo.trafficcontrol;

import com.google.common.base.Strings;
import com.hhrpc.hhrpc.core.governance.SlidingTimeWindow;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Date 2024/4/30
 * @Author lifei
 */
public class SlidingTimeWindowTCTest {

    // 流量大小
    private static long trafficControl = 10;
    // 时间窗口
    private static int timeWinow = 20;
    private static SlidingTimeWindow slidingTimeWindow = new SlidingTimeWindow(timeWinow);
    public static synchronized boolean tryAcquire() {
        if (slidingTimeWindow.calcSum() > trafficControl) {
            return false;
        }
        slidingTimeWindow.record(System.currentTimeMillis());
        return true;
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
