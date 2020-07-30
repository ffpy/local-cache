package com.tom.common.localcache.cache;

import com.github.benmanes.caffeine.cache.Cache;
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

    @Nullable
    @Override
    public Object getIfPresent(@NonNull Object key) {
        return cache.getIfPresent(key);
    }

    @Nullable
    @Override
    public Object get(@NonNull Object key, @NonNull Function<? super Object, ? extends Object> mappingFunction) {
        return cache.get(key, mappingFunction);
    }

    @Override
    public @NonNull Map<Object, Object> getAllPresent(@NonNull Iterable<?> keys) {
        return cache.getAllPresent(keys);
    }

    @Override
    public void put(@NonNull Object key, @NonNull Object value) {
        if (value != NullValue.INSTANCE) {
            cache.put(key, value);
        }
    }

    @Override
    public void putAll(@NonNull Map<? extends Object, ? extends Object> map) {
        cache.putAll(map);
    }

    @Override
    public void invalidate(@NonNull Object key) {
        cache.invalidate(key);
    }

    @Override
    public void invalidateAll(@NonNull Iterable<?> keys) {
        cache.invalidateAll();
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
