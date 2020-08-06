package com.tom.common.localcache.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Policy;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * 不可修改的Cache
 *
 * @author 温龙盛
 * @date 2020/8/6 16:04
 */
@RequiredArgsConstructor
public class UnmodifiableCache<K, V> implements Cache<K, V> {

    @NonNull
    private final Cache<K, V> cache;

    @Nullable
    @Override
    public V getIfPresent(Object key) {
        return cache.getIfPresent(key);
    }

    @Nullable
    @Override
    public V get(K key, Function<? super K, ? extends V> mappingFunction) {
        return cache.get(key, mappingFunction);
    }

    @Override
    public Map<K, V> getAllPresent(Iterable<?> keys) {
        return cache.getAllPresent(keys);
    }

    @Override
    public void put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidate(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidateAll(Iterable<?> keys) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidateAll() {
        throw new UnsupportedOperationException();
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
    public ConcurrentMap<K, V> asMap() {
        return new ConcurrentHashMap<>(cache.asMap());
    }

    @Override
    public void cleanUp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Policy<K, V> policy() {
        return cache.policy();
    }
}
