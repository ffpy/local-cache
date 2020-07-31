package com.tom.common.localcache.cache;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Policy;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * 不存入缓存的缓存实现，主要是用于测试
 *
 * @author 温龙盛
 * @date 2020/7/29 16:30
 */
public class EmptyLoadingCache implements LoadingCache<Object, Object> {

    private final LoadingCache<Object, Object> cache;

    public EmptyLoadingCache(LoadingCache<Object, Object> cache) {
        this.cache = cache;
    }

    @Override
    public Object get(Object key) {
        Object value = cache.get(key);
        // 清掉cacheLoader加载的缓存
        cache.invalidate(key);
        return value;
    }

    @Override
    public Map<Object, Object> getAll(Iterable<?> keys) {
        Map<Object, Object> value = cache.getAll(keys);
        // 清掉cacheLoader加载的缓存
        cache.invalidateAll(keys);
        return value;
    }

    @Override
    public void refresh(Object key) {
        cache.refresh(key);
    }


    @Override
    public Object getIfPresent(Object key) {
        return cache.getIfPresent(key);
    }


    @Override
    public Object get(Object key, Function<? super Object, ?> mappingFunction) {
        return cache.get(key, mappingFunction);
    }

    @Override
    public Map<Object, Object> getAllPresent(Iterable<?> keys) {
        return cache.getAllPresent(keys);
    }

    @Override
    public void put(Object key, Object value) {
        // 不存入缓存
    }

    @Override
    public void putAll(Map<?, ?> map) {
        // 不存入缓存
    }

    @Override
    public void invalidate(Object key) {
        cache.invalidate(key);
    }

    @Override
    public void invalidateAll(Iterable<?> keys) {
        cache.invalidateAll(keys);
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }

    @Override
    public long estimatedSize() {
        return cache.estimatedSize();
    }

    @Override
    public CacheStats stats() {
        return cache.stats();
    }

    @Override
    public ConcurrentMap<Object, Object> asMap() {
        return cache.asMap();
    }

    @Override
    public void cleanUp() {
        cache.cleanUp();
    }

    @Override
    public Policy<Object, Object> policy() {
        return cache.policy();
    }
}
