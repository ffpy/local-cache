package com.tom.common.localcache.cache;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Policy;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.cache.support.NullValue;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * 缓存null值的缓存
 */
class CacheNullValueLoadingCache implements LoadingCache<Object, Object> {

    private final LoadingCache<Object, Object> cache;

    public CacheNullValueLoadingCache(LoadingCache<Object, Object> cache) {
        this.cache = cache;
    }


    @Override
    public Object get(Object key) {
        Object value = cache.get(key);
        if (value == null) {
            cache.put(key, NullValue.INSTANCE);
        }
        if (value == NullValue.INSTANCE) {
            return null;
        }
        return value;
    }

    @Override
    public Map<Object, Object> getAll(Iterable<?> keys) {
        return convertNullValueInMap(cache.getAll(keys));
    }

    @Override
    public void refresh(Object key) {
        cache.refresh(key);
    }


    @Override
    public Object getIfPresent(Object key) {
        Object value = cache.getIfPresent(key);
        if (value == NullValue.INSTANCE) {
            return null;
        }
        return value;
    }


    @Override
    public Object get(Object key, Function<? super Object, ?> mappingFunction) {
        Object value = cache.get(key, mappingFunction);
        if (value == NullValue.INSTANCE) {
            return null;
        }
        return value;
    }

    @Override
    public Map<Object, Object> getAllPresent(Iterable<?> keys) {
        return convertNullValueInMap(cache.getAllPresent(keys));
    }

    @Override
    public void put(Object key, Object value) {
        cache.put(key, value);
    }

    @Override
    public void putAll(Map<?, ?> map) {
        cache.putAll(map);
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

    private Map<Object, Object> convertNullValueInMap(Map<Object, Object> map) {
        map.entrySet().removeIf(entry -> entry.getValue() == NullValue.INSTANCE);
        return map;
    }
}
