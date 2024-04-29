package com.hhrpc.hhrpc.demo.trafficcontrol;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeMap;

/**
 * @Date 2024/4/30
 * @Author lifei
 */
public class SlingLogTCTest {
    // 流量大小
    private static long trafficControl = 10;
    // 时间窗口
    private static long timeWinow = 20;

    private static Long lastTime = System.currentTimeMillis();
    private static TreeMap<Long, Integer> map = Maps.newTreeMap();

    public static synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis()/1000;
        // 清理
        if (!map.isEmpty() && map.firstKey() + timeWinow < now) {
            Set<Long> keySet = map.subMap(0L, now - timeWinow).keySet();
            for (Long key : keySet) {
                map.remove(key);
            }
        }


        // 比较
        Set<Long> keySets = map.subMap(now - timeWinow, now).keySet();
        int sum = 0;
        for (Long key : keySets) {
            sum += map.get(key);
        }

        if (sum >= trafficControl) {
            return false;
        }
        // 记录

        if (map.containsKey(now)) {
            map.put(now, map.get(now) + 1);
        } else {
            map.put(now, 1);
        }
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
