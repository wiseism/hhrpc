package com.hhrpc.hhrpc.core.api;

import com.hhrpc.hhrpc.core.meta.InstanceMeta;

import java.util.List;

/**
 * @Date 2024/3/23
 * @Author lifei
 */
public class Event {
    List<InstanceMeta> data;

    public Event(){}

    public Event(List<InstanceMeta> data) {
        this.data = data;
    }

    public List<InstanceMeta> getData() {
        return data;
    }

    public void setData(List<InstanceMeta> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Event{" +
                "data=" + data +
                '}';
    }
}
