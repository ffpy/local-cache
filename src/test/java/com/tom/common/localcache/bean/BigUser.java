package com.tom.common.localcache.bean;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用于测试OOM的用户类
 */
@Data
public class BigUser {
    private String username;
    private final byte[] bytes = new byte[100 * 1024 * 1024];
    private String time = LocalDateTime.now().toString();

    public BigUser(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigUser bigUser = (BigUser) o;
        return Objects.equals(username, bigUser.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
