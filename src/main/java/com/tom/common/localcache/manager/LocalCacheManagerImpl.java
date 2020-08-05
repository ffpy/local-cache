package com.tom.common.localcache.manager;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.tom.common.localcache.ReloadAction;
import com.tom.common.localcache.cache.CacheFactory;
import com.tom.common.localcache.config.LocalCacheManagerConfig;
import com.tom.common.localcache.properties.LocalCacheGroupProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.ApplicationContext;

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

    @Autowired
    private LocalCacheManagerConfig cacheManagerConfig;

    @Autowired
    private ApplicationContext context;

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
    public <K, V> LoadingCache<K, V> getLoadingCache(String group) {
        Object cache = Optional.ofNullable(getCache(group))
                .map(Cache::getNativeCache)
                .orElseThrow(() -> new IllegalArgumentException("没有名为" + group + "的缓存"));
        if (!(cache instanceof LoadingCache)) {
            throw new RuntimeException("缓存类型不正确，请配置" + group + "的cache-loader属性");
        }
        return (LoadingCache<K, V>) cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheManager.getCacheNames();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> com.github.benmanes.caffeine.cache.Cache<K, V> getCaffeineCache(String group) {
        return (com.github.benmanes.caffeine.cache.Cache<K, V>) cacheMap.get(group);
    }

    @Override
    public Map<String, com.github.benmanes.caffeine.cache.Cache<Object, Object>> getCaffeineCacheMap() {
        return Collections.unmodifiableMap(cacheMap);
    }

    @Override
    public void reloadAll(String group) {
        LocalCacheGroupProperties prop = cacheManagerConfig.getGroupProperties(group);
        if (prop == null) {
            throw new IllegalArgumentException("找不到分组: " + group);
        }
        if (StringUtils.isBlank(prop.getReloadAction())) {
            throw new IllegalArgumentException("缓存分组" + group + "的reload-action属性不能为空");
        }
        ReloadAction<?, ?> reloadAction = context.getBean(prop.getReloadAction(), ReloadAction.class);
        reloadAll(group, reloadAction.reload());
    }

    @Override
    public void reloadAll(String group, Map<?, ?> data) {
        Objects.requireNonNull(data);
        LocalCacheGroupProperties prop = cacheManagerConfig.getGroupProperties(group);
        if (prop == null) {
            throw new IllegalArgumentException("找不到分组: " + group);
        }
        com.github.benmanes.caffeine.cache.Cache<Object, Object> newCache =
                CacheFactory.createCaffeineCache(prop, context);
        newCache.putAll(data);
        // 替换缓存器并清空旧缓存器的数据
        replaceCache(group, newCache).invalidateAll();
    }

    /**
     * 替换同名缓存器
     *
     * @param group    缓存名
     * @param newCache 新缓存实例
     * @return 旧缓存器
     * @throws IllegalArgumentException 找不到同名缓存器
     */
    @SuppressWarnings("unchecked")
    private <K, V> com.github.benmanes.caffeine.cache.Cache<K, V> replaceCache(
            String group, com.github.benmanes.caffeine.cache.Cache<K, V> newCache) {
        Objects.requireNonNull(group);
        Objects.requireNonNull(newCache);

        // 查找同名缓存
        com.github.benmanes.caffeine.cache.Cache<Object, Object> oldCache = cacheMap.get(group);
        if (oldCache == null) {
            throw new IllegalArgumentException("找不到同名缓存: " + group);
        }

        // TODO 要不要加上同步，防止观察到不一致的状态
        cacheMap.put(group, (com.github.benmanes.caffeine.cache.Cache<Object, Object>) newCache);
        cacheManager.setCaches(getCacheList());
        cacheManager.initializeCaches();

        return (com.github.benmanes.caffeine.cache.Cache<K, V>) oldCache;
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
