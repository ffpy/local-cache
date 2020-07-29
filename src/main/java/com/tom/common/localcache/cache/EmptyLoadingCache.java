package com.tom.common.localcache.cache;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Policy;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
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

    @Nullable
    @Override
    public Object get(@NonNull Object key) {
        Object value = cache.get(key);
        // 清掉cacheLoader加载的缓存
        cache.invalidate(key);
        return value;
    }

    @Override
    public @NonNull Map<Object, Object> getAll(@NonNull Iterable<?> keys) {
        Map<Object, Object> value = cache.getAll(keys);
        // 清掉cacheLoader加载的缓存
        cache.invalidateAll(keys);
        return value;
    }

    @Override
    public void refresh(@NonNull Object key) {
        cache.refresh(key);
    }

    @Nullable
    @Override
    public Object getIfPresent(@NonNull Object key) {
        return cache.getIfPresent(key);
    }

    @Nullable
    @Override
    public Object get(@NonNull Object key, @NonNull Function<? super Object, ?> mappingFunction) {
        return cache.get(key, mappingFunction);
    }

    @Override
    public @NonNull Map<Object, Object> getAllPresent(@NonNull Iterable<?> keys) {
        return cache.getAllPresent(keys);
    }

    @Override
    public void put(@NonNull Object key, @NonNull Object value) {
        // 不存入缓存
    }

    @Override
    public void putAll(@NonNull Map<?, ?> map) {
        // 不存入缓存
    }

    @Override
    public void invalidate(@NonNull Object key) {
        cache.invalidate(key);
    }

    @Override
    public void invalidateAll(@NonNull Iterable<?> keys) {
        cache.invalidateAll(keys);
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }

    @Override
    public @NonNegative long estimatedSize() {
        return cache.estimatedSize();
    }

    @Override
    public @NonNull CacheStats stats() {
        return cache.stats();
    }

    @Override
    public @NonNull ConcurrentMap<Object, Object> asMap() {
        return cache.asMap();
    }

    @Override
    public void cleanUp() {
        cache.cleanUp();
    }

    @Override
    public @NonNull Policy<Object, Object> policy() {
        return cache.policy();
    }
}
