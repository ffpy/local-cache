package com.tom.common.localcache.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.cache.CacheManager;

import java.util.Map;

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

    /**
     * 获取Caffeine缓存实例
     *
     * @param name 缓存分组名
     * @return Caffeine缓存
     */
    com.github.benmanes.caffeine.cache.Cache<Object, Object> getCaffeineCache(String name);

    /**
     * 获取Caffeine缓存Map
     *
     * @return Caffeine缓存Map
     */
    Map<String, Cache<Object, Object>> getCaffeineCacheMap();

    /**
     * 替换同名缓存
     *
     * @param name     缓存名
     * @param newCache 新缓存实例
     * @return 旧缓存
     * @throws IllegalArgumentException 找不到同名缓存
     */
    Cache<Object, Object> replaceCache(String name, Cache<Object, Object> newCache);
}
