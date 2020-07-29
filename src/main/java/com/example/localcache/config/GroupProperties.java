package com.example.localcache.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 缓存分组属性配置
 *
 * @author 温龙盛
 * @date 2020/7/29 9:28
 */
@Getter
@Setter
@ToString
public class GroupProperties {

    /** 是否启用此缓存组 */
    private boolean enable;

    /** 写入过期时间 */
    private String expireAfterWrite;

    /** 访问过期时间 */
    private String expireAfterAccess;
}
