package com.tom.common.localcache.config;

import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleCacheManager;

import java.util.Collection;
import java.util.Optional;

/**
 * 本地缓存管理器实现
 *
 * @author 温龙盛
 * @date 2020/7/29 15:00
 */
public class LocalCacheManagerImpl implements LocalCacheManager, InitializingBean {

    private final SimpleCacheManager cacheManager = new SimpleCacheManager();

    public void setCaches(Collection<? extends Cache> caches) {
        cacheManager.setCaches(caches);
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
        return (LoadingCache<K, V>) Optional.ofNullable(getCache(name))
                .map(Cache::getNativeCache)
                .orElseThrow(() -> new IllegalArgumentException("没有名为" + name + "的缓存"));
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheManager.getCacheNames();
    }
}
