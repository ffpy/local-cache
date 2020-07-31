package com.tom.common.localcache.bean;

import lombok.Data;

import java.time.LocalDateTime;

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
}
