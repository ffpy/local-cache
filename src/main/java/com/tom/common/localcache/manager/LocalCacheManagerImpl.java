package com.tom.common.localcache.manager;

import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 本地缓存管理器实现
 *
 * @author 温龙盛
 * @date 2020/7/29 15:00
 */
public class LocalCacheManagerImpl implements LocalCacheManager, InitializingBean {

    /** 委托的缓存管理器 */
    private final SimpleCacheManager cacheManager = new SimpleCacheManager();

    /** 缓存分组名到Caffeine缓存的映射 */
    private Map<String, com.github.benmanes.caffeine.cache.Cache<Object, Object>> cacheMap = Collections.emptyMap();

    /**
     * 设置缓存
     *
     * @param cacheMap Caffeine缓存map
     */
    public void setCaches(Map<String, com.github.benmanes.caffeine.cache.Cache<Object, Object>> cacheMap) {
        this.cacheMap = Objects.requireNonNull(cacheMap);
        cacheManager.setCaches(getCacheList());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        cacheManager.afterPropertiesSet();
    }

    @Override
    public Cache getCache(String name) {
        return cacheManager.getCache(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> LoadingCache<K, V> getLoadingCache(String name) {
        Object cache = Optional.ofNullable(getCache(name))
                .map(Cache::getNativeCache)
                .orElseThrow(() -> new IllegalArgumentException("没有名为" + name + "的缓存"));
        if (!(cache instanceof LoadingCache)) {
            throw new RuntimeException("缓存类型不正确，请配置" + name + "的cache-loader属性");
        }
        return (LoadingCache<K, V>) cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheManager.getCacheNames();
    }

    @Override
    public com.github.benmanes.caffeine.cache.Cache<Object, Object> getCaffeineCache(String name) {
        return cacheMap.get(name);
    }

    @Override
    public Map<String, com.github.benmanes.caffeine.cache.Cache<Object, Object>> getCaffeineCacheMap() {
        return Collections.unmodifiableMap(cacheMap);
    }

    @Override
    public com.github.benmanes.caffeine.cache.Cache<Object, Object> replaceCache(
            String name, com.github.benmanes.caffeine.cache.Cache<Object, Object> newCache) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(newCache);

        // 查找同名缓存
        com.github.benmanes.caffeine.cache.Cache<Object, Object> oldCache = cacheMap.get(name);
        if (oldCache == null) {
            throw new IllegalArgumentException("找不到同名缓存: " + name);
        }

        // TODO 要不要加上同步，防止观察到不一致的状态
        cacheMap.put(name, newCache);
        cacheManager.setCaches(getCacheList());
        cacheManager.initializeCaches();

        return oldCache;
    }

    /**
     * {@link #cacheMap}转为List
     *
     * @return caches
     */
    private List<Cache> getCacheList() {
        return cacheMap.entrySet().stream()
                .map(it -> new CaffeineCache(it.getKey(), it.getValue()))
                .collect(Collectors.toList());
    }
}
