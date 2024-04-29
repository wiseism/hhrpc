package com.hhrpc.hhrpc.core.governance;

import java.util.Arrays;

/**
 * 环，作为滑动窗口的容器
 * @Date 2024/4/7
 * @Author lifei
 */
public class RingBuffer {
    private final int size;
    private final int[] ring;
    public RingBuffer(int size) {
        this.size = size;
        this.ring = new int[size];
    }

    public int sum() {
        return Arrays.stream(ring).sum();
    }

    public void inc(int index, int val) {
        ring[index%size] += val;
    }

    public void reset() {
        Arrays.fill(ring, 0);
    }

    /**
     * 包含 beginIndex， 不包含 endIndex
     * @param beginIndex
     * @param endIndex
     */
    public void reset(int beginIndex, int endIndex) {
        int fromIndex = beginIndex%size;
        int toIndex = endIndex%size;
        if (fromIndex < toIndex) {
            Arrays.fill(ring, fromIndex, toIndex, 0);
        } else if (fromIndex > toIndex) {
            Arrays.fill(ring, fromIndex, size, 0);
            Arrays.fill(ring, 0, toIndex, 0);
        }
    }
}
