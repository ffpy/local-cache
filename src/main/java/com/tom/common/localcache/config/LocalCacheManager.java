package com.tom.common.localcache.config;

import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.cache.CacheManager;

/**
 * 本地缓存管理器
 *
 * @author 温龙盛
 * @date 2020/7/29 17:17
 */
public interface LocalCacheManager extends CacheManager {

    /**
     * 获取设置了CacheLoader的缓存
     *
     * @param name 缓存分组名
     * @param <K>  缓存键
     * @param <V>  缓存值
     * @return LoadingCache
     * @throws IllegalArgumentException 缓存不存在
     */
    <K, V> LoadingCache<K, V> getLoadingCache(String name);
}
