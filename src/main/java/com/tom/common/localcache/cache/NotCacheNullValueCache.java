package com.tom.common.localcache.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Policy;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.cache.support.NullValue;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * 不缓存null值的缓存
 *
 * @author 温龙盛
 * @date 2020/7/30 11:48
 */
class NotCacheNullValueCache implements Cache<Object, Object> {

    private final Cache<Object, Object> cache;

    public NotCacheNullValueCache(Cache<Object, Object> cache) {
        this.cache = cache;
    }

    @Override
    public Object getIfPresent(Object key) {
        return cache.getIfPresent(key);
    }

    @Override
    public Object get(Object key, Function<? super Object, ? extends Object> mappingFunction) {
        return cache.get(key, mappingFunction);
    }

    @Override
    public Map<Object, Object> getAllPresent(Iterable<?> keys) {
        return cache.getAllPresent(keys);
    }

    @Override
    public void put(Object key, Object value) {
        if (value != NullValue.INSTANCE) {
            cache.put(key, value);
        }
    }

    @Override
    public void putAll(Map<? extends Object, ? extends Object> map) {
        cache.putAll(map);
    }

    @Override
    public void invalidate(Object key) {
        cache.invalidate(key);
    }

    @Override
    public void invalidateAll(Iterable<?> keys) {
        cache.invalidateAll();
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
