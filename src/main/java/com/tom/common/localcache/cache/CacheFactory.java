package com.tom.common.localcache.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tom.common.localcache.properties.LocalCacheGroupProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

/**
 * 缓存工厂
 *
 * @author 温龙盛
 * @date 2020/7/29 17:23
 */
public class CacheFactory {

    /**
     * 根据配置属性创建Caffeine缓存
     *
     * @param properties         配置属性
     * @param applicationContext ApplicationContext
     * @return Caffeine缓存
     */
    public static Cache<Object, Object> createCaffeineCache(LocalCacheGroupProperties properties, ApplicationContext applicationContext) {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
        // 启动统计功能
        caffeine.recordStats();

        if (properties.getSoftValues() == Boolean.TRUE) {
            caffeine.softValues();
        }
        if (properties.getWeakKeys() == Boolean.TRUE) {
            caffeine.weakKeys();
        }
        if (properties.getWeakValues() == Boolean.TRUE) {
            caffeine.weakValues();
        }
        if (StringUtils.isNotBlank(properties.getExpireAfterWrite())) {
            TimeValue timeValue = TimeValue.parse(properties.getExpireAfterWrite());
            caffeine.expireAfterWrite(timeValue.getValue(), timeValue.getUnit());
        }
        if (StringUtils.isNotBlank(properties.getExpireAfterAccess())) {
            TimeValue timeValue = TimeValue.parse(properties.getExpireAfterAccess());
            caffeine.expireAfterAccess(timeValue.getValue(), timeValue.getUnit());
        }
        if (StringUtils.isNotBlank(properties.getRefreshAfterWrite())) {
            if (StringUtils.isBlank(properties.getCacheLoader())) {
                throw new IllegalArgumentException("配置了refreshAfterWrite则必须也配置cacheLoader");
            }
            TimeValue timeValue = TimeValue.parse(properties.getRefreshAfterWrite());
            caffeine.refreshAfterWrite(timeValue.getValue(), timeValue.getUnit());
        }
        if (properties.getInitialCapacity() != null) {
            if (properties.getInitialCapacity() < 0) {
                throw new IllegalArgumentException("initialCapacity必须大于或等于0");
            }
            caffeine.initialCapacity(properties.getInitialCapacity());
        }
        if (properties.getMaximumSize() != null) {
            if (properties.getMaximumSize() <= 0) {
                throw new IllegalArgumentException("maximumSize必须大于0");
            }
            caffeine.maximumSize(properties.getMaximumSize());
        }

        if (StringUtils.isNotBlank(properties.getCacheLoader())) {
            return caffeine.build(getCacheLoader(properties, applicationContext));
        } else {
            return caffeine.build();
        }
    }

    /**
     * 根据配置属性获取对应的CacheLoader
     *
     * @param properties         配置属性
     * @param applicationContext ApplicationContext
     * @return CacheLoader
     */
    @SuppressWarnings("unchecked")
    private static CacheLoader<Object, Object> getCacheLoader(LocalCacheGroupProperties properties, ApplicationContext applicationContext) {
        return applicationContext.getBean(properties.getCacheLoader(), CacheLoader.class);
    }
}
