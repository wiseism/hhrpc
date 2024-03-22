package com.hhrpc.hhrpc.core.api;

import java.util.List;

/**
 * @Date 2024/3/22
 * @Author lifei
 */
public interface Router<T> {

    List<T> rout(List<T> urls);

    Router DEFAULT = (urls)->urls;
}
