package com.tom.common.localcache.cache;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Policy;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.cache.support.NullValue;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * 缓存null值的缓存
 *
 * @author 温龙盛
 * @date 2020/7/29 16:30
 */
class CacheNullValueLoadingCache implements LoadingCache<Object, Object> {

    private final LoadingCache<Object, Object> cache;

    public CacheNullValueLoadingCache(LoadingCache<Object, Object> cache) {
        this.cache = cache;
    }

    @Nullable
    @Override
    public Object get(@NonNull Object key) {
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
    public @NonNull Map<Object, Object> getAll(@NonNull Iterable<?> keys) {
        return convertNullValueInMap(cache.getAll(keys));
    }

    @Override
    public void refresh(@NonNull Object key) {
        cache.refresh(key);
    }

    @Nullable
    @Override
    public Object getIfPresent(@NonNull Object key) {
        Object value = cache.getIfPresent(key);
        if (value == NullValue.INSTANCE) {
            return null;
        }
        return value;
    }

    @Nullable
    @Override
    public Object get(@NonNull Object key, @NonNull Function<? super Object, ?> mappingFunction) {
        Object value = cache.get(key, mappingFunction);
        if (value == NullValue.INSTANCE) {
            return null;
        }
        return value;
    }

    @Override
    public @NonNull Map<Object, Object> getAllPresent(@NonNull Iterable<?> keys) {
        return convertNullValueInMap(cache.getAllPresent(keys));
    }

    @Override
    public void put(@NonNull Object key, @NonNull Object value) {
        cache.put(key, value);
    }

    @Override
    public void putAll(@NonNull Map<?, ?> map) {
        cache.putAll(map);
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

    private Map<Object, Object> convertNullValueInMap(Map<Object, Object> map) {
        map.entrySet().removeIf(entry -> entry.getValue() == NullValue.INSTANCE);
        return map;
    }
}
