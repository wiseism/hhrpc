package com.hhrpc.hhrpc.demo.consumer;

import com.hhrpc.hhrpc.core.utils.MockUtils;
import com.hhrpc.hhrpc.demo.api.User;
import org.junit.Test;

/**
 * @Date 2024/3/30
 * @Author lifei
 */
public class BeanTest {

    @Test
    public void mockUserTest() {
        Object user = MockUtils.mock(User.class, null);
        System.out.println(user);
    }
}
