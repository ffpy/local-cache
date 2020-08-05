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
     * <p>
     * 注意：因为缓存实例可能会被替换，所以不要存起来，
     * 每次都通过此方法来获取最新的实例
     *
     * @param group 缓存分组名
     * @param <K>   缓存键
     * @param <V>   缓存值
     * @return LoadingCache
     * @throws IllegalArgumentException 缓存不存在
     */
    <K, V> LoadingCache<K, V> getLoadingCache(String group);

    /**
     * 获取Caffeine缓存实例
     * <p>
     * 注意：因为缓存实例可能会被替换，所以不要存起来，
     * 每次都通过此方法来获取最新的实例
     *
     * @param group 缓存分组名
     * @return Caffeine缓存
     */
    <K, V> com.github.benmanes.caffeine.cache.Cache<K, V> getCaffeineCache(String group);

    /**
     * 获取Caffeine缓存Map
     * <p>
     * 注意：因为缓存实例可能会被替换，所以不要存起来，
     * 每次都通过此方法来获取最新的实例
     *
     * @return Caffeine缓存Map
     */
    Map<String, Cache<Object, Object>> getCaffeineCacheMap();

    /**
     * 更新指定分组的所有数据，数据来源为reload-action的bean
     *
     * @param group 缓存分组名
     * @throws IllegalArgumentException 找不到分组
     */
    void reloadAll(String group);

    /**
     * 更新指定分组的所有数据为传入的数据
     *
     * @param group 缓存分组名
     * @param data  新数据
     * @throws IllegalArgumentException 找不到分组
     */
    void reloadAll(String group, Map<?, ?> data);
}
