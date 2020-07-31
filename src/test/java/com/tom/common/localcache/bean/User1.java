package com.tom.common.localcache.bean;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 测试用户类1
 *
 * @author 温龙盛
 * @date 2020/7/29 18:26
 */
@Data
public class User1 {
    private String username;
    private String time = LocalDateTime.now().toString();

    public User1(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User1 user1 = (User1) o;
        return Objects.equals(username, user1.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
