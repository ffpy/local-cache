package com.tom.common.localcache.properties;

import com.tom.common.localcache.constant.ConfigPrefix;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 全局分组属性配置
 *
 * @author 温龙盛
 * @date 2020/7/29 9:28
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(ConfigPrefix.GLOBAL_GROUP)
public class LocalCacheGlobalGroupProperties {

    /** 是否缓存空值，防止缓存穿透，默认开启 */
    private Boolean cacheNullValue = true;

    /** 开启缓存值软引用 */
    private Boolean softValues;

    /** 开启缓存键弱引用 */
    private Boolean weakKeys;

    /** 开启缓存值弱引用 */
    private Boolean weakValues;

    /** 写入过期时间 */
    private String expireAfterWrite;

    /** 访问过期时间 */
    private String expireAfterAccess;

    /** 写入刷新时间，如果配置了此项，则必须也要配置classLoader属性 */
    private String refreshAfterWrite;

    /** 缓存容量初始值 */
    private Integer initialCapacity;

    /** 缓存容量最大值 */
    private Integer maximumSize;

    /** 重新加载所有缓存的Cron表达式，必须同时配置reloadAction */
    private String reloadCron;

    /** 是否在启动的时候加载数据 */
    private Boolean reloadOnStart;
}
