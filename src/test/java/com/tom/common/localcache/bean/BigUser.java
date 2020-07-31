package com.tom.common.localcache.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用于测试OOM的用户类
 *
 * @author 温龙盛
 * @date 2020/7/29 18:26
 */
@Data
public class BigUser {
    private String username;
    private final byte[] bytes = new byte[100 * 1024 * 1024];
    private String time = LocalDateTime.now().toString();

    public BigUser(String username) {
        this.username = username;
    }
}
