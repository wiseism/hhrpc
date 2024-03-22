package com.hhrpc.hhrpc.core.api;

import java.util.List;

/**
 * @Date 2024/3/23
 * @Author lifei
 */
public class Event {
    List<String> data;

    public Event(){}

    public Event(List<String> data) {
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Event{" +
                "data=" + data +
                '}';
    }
}
