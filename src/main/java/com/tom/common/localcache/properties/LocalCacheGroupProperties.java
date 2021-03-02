package com.tom.common.localcache.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 缓存分组属性配置
 * <p>
 * 注意，如果修改字段，要看要不要修改{@link LocalCacheGlobalGroupProperties}中的字段
 */
@Getter
@Setter
@ToString
public class LocalCacheGroupProperties {

    /** 是否启用缓存 */
    private boolean enable = true;

    /** 是否开启调试模式，设为true则每次都会请求数据，不会存在缓存中 */
    private boolean debug;

    /** 缓存组描述，用于显示在缓存管理页面 */
    private String desc;

    /** 是否缓存空值，防止缓存穿透，默认开启 */
    private Boolean cacheNullValue;

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

    /** 写入刷新时间，如果配置了此项，则必须也要配置cacheLoader属性 */
    private String refreshAfterWrite;

    /** 缓存容量初始值 */
    private Integer initialCapacity;

    /** 缓存容量最大值 */
    private Integer maximumSize;

    /** {@link com.github.benmanes.caffeine.cache.CacheLoader}的Bean名称 */
    private String cacheLoader;

    /** {@link com.tom.common.localcache.action.ReloadAction}的Bean名称 */
    private String reloadAction;

    /** 重新加载所有缓存的Cron表达式，必须同时配置{@link #reloadAction} */
    private String reloadCron;

    /** 是否在启动的时候加载数据 */
    private Boolean reloadOnStart;

    /** {@link com.tom.common.localcache.action.RefreshByUpdateTimeAction}的Bean名称 */
    private String refreshByUpdateTimeAction;

    /** 增量更新时间间隔 */
    private String refreshByUpdateTimeInterval;
}
