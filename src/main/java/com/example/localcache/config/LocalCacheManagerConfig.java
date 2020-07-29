package com.example.localcache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地缓存管理器配置
 */
@Configuration
@EnableCaching
public class LocalCacheManagerConfig {

    /** 分组属性名前缀 */
    private static final String GROUP_PREFIX = "local-cache.group";

    private static final ConfigurationPropertyName CACHE_GROUP = ConfigurationPropertyName.of(GROUP_PREFIX);

    private static final Bindable<Map<String, String>> STRING_MAP = Bindable.mapOf(String.class,
            String.class);

    // TODO 有没有更好的设计，不要通过静态字段导出？
    public static final Map<String, com.github.benmanes.caffeine.cache.Cache<Object, Object>> cacheMap = new HashMap();

    @Bean
    public CacheManager cacheManager(ConfigurableEnvironment environment) {
        Map<String, GroupProperties> groups = getGroups(environment);

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<Cache> caches = new ArrayList<>();
        for (Map.Entry<String, GroupProperties> entry : groups.entrySet()) {
            String groupName = entry.getKey();
            GroupProperties properties = entry.getValue();
            if (!properties.isEnable()) {
                continue;
            }

            Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
            caffeine.recordStats();
            // 写入过期时间
            if (StringUtils.isNotBlank(properties.getExpireAfterWrite())) {
                ConfigTimeValue timeValue = ConfigTimeValue.parse(properties.getExpireAfterWrite());
                caffeine.expireAfterWrite(timeValue.getValue(), timeValue.getUnit());
            }
            // 访问过期时间
            if (StringUtils.isNotEmpty(properties.getExpireAfterAccess())) {
                ConfigTimeValue timeValue = ConfigTimeValue.parse(properties.getExpireAfterAccess());
                caffeine.expireAfterAccess(timeValue.getValue(), timeValue.getUnit());
            }

            com.github.benmanes.caffeine.cache.Cache<Object, Object> cache = caffeine.build();
            cacheMap.put(groupName, cache);
            caches.add(new CaffeineCache(groupName, cache));
        }

        // TODO global分组属性解析

        cacheManager.setCaches(caches);
        return cacheManager;
    }

    /**
     * 获取缓存分组及对应的属性
     *
     * @param environment {@link ConfigurableEnvironment}
     * @return 分组名 -> 分组属性
     */
    private Map<String, GroupProperties> getGroups(ConfigurableEnvironment environment) {
        Binder binder = Binder.get(environment);
        Map<String, String> groupMap = binder.bind(CACHE_GROUP, STRING_MAP).orElseGet(Collections::emptyMap);

        Map<String, GroupProperties> groups = new HashMap<>();
        for (Map.Entry<String, String> entry : groupMap.entrySet()) {
            String[] split = StringUtils.split(entry.getKey(), '.');
            if (split == null || split.length != 2) {
                throw new IllegalArgumentException("Invalid key: " + entry.getKey());
            }
            String groupName = split[0];
            String propertyName = split[1];

            GroupProperties itemProperties = groups.computeIfAbsent(groupName, k -> new GroupProperties());
            try {
                BeanUtils.copyProperty(itemProperties, propertyName, entry.getValue());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException("Invalid property: " + entry.getKey() + " => " + entry.getValue(), e);
            }
        }

        return groups;
    }

}
