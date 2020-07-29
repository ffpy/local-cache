package com.tom.common.localcache.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.tom.common.localcache.cache.CacheFactory;
import com.tom.common.localcache.cache.EmptyCache;
import com.tom.common.localcache.cache.EmptyLoadingCache;
import com.tom.common.localcache.constant.ConfigPrefix;
import com.tom.common.localcache.manager.LocalCacheManager;
import com.tom.common.localcache.manager.LocalCacheManagerImpl;
import com.tom.common.localcache.properties.LocalCacheGlobalGroupProperties;
import com.tom.common.localcache.properties.LocalCacheGroupProperties;
import com.tom.common.localcache.properties.LocalCacheManagerProperties;
import com.tom.common.localcache.properties.LocalCacheProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地缓存管理器配置
 */
@Slf4j
@Configuration
@EnableCaching
@EnableConfigurationProperties({
        LocalCacheProperties.class,
        LocalCacheGlobalGroupProperties.class,
        LocalCacheManagerProperties.class,
})
public class LocalCacheManagerConfig {

    private static final ConfigurationPropertyName CACHE_GROUP = ConfigurationPropertyName.of(ConfigPrefix.GROUP);

    private static final Bindable<Map<String, String>> STRING_MAP = Bindable.mapOf(String.class,
            String.class);

    /** 缓存分组名到Caffeine缓存的映射 */
    public final Map<String, com.github.benmanes.caffeine.cache.Cache<Object, Object>> caffeineCacheMap = new HashMap<>();

    @Autowired
    private LocalCacheProperties localCacheProperties;

    @Autowired
    private LocalCacheGlobalGroupProperties globalGroupProperties;

    @Bean
    public LocalCacheManager cacheManager(ConfigurableEnvironment environment, ApplicationContext applicationContext) {
        Map<String, LocalCacheGroupProperties> groups = getGroups(environment);

        LocalCacheManagerImpl cacheManager = new LocalCacheManagerImpl();
        List<org.springframework.cache.Cache> caches = new ArrayList<>();
        for (Map.Entry<String, LocalCacheGroupProperties> entry : groups.entrySet()) {
            String groupName = entry.getKey();
            Cache<Object, Object> cache = createCache(applicationContext, entry.getValue());
            caffeineCacheMap.put(groupName, cache);
            caches.add(new CaffeineCache(groupName, cache));
        }

        cacheManager.setCaches(caches);
        return cacheManager;
    }

    /**
     * 获取指定名称的缓存
     *
     * @param name 缓存分组名
     * @return Caffeine缓存
     */
    public com.github.benmanes.caffeine.cache.Cache<Object, Object> getCache(String name) {
        return caffeineCacheMap.get(name);
    }

    /**
     * 创建缓存
     *
     * @param applicationContext ApplicationContext
     * @param properties         缓存配置
     * @return 缓存
     */
    private Cache<Object, Object> createCache(ApplicationContext applicationContext, LocalCacheGroupProperties properties) {
        Cache<Object, Object> cache = CacheFactory.createCaffeineCache(properties, applicationContext);
        if (isGroupDisable(properties)) {
            if (cache instanceof LoadingCache) {
                cache = new EmptyLoadingCache((LoadingCache<Object, Object>) cache);
            } else {
                cache = new EmptyCache(cache);
            }
        }
        return cache;
    }

    /**
     * 判断指定分组是否不启用缓存
     *
     * @param properties 分组属性
     * @return true为不启用，false为启用
     */
    private boolean isGroupDisable(LocalCacheGroupProperties properties) {
        return !localCacheProperties.isEnable() || !properties.isEnable();
    }

    /**
     * 获取缓存分组及对应的属性
     *
     * @param environment {@link ConfigurableEnvironment}
     * @return 分组名 -> 分组属性
     */
    private Map<String, LocalCacheGroupProperties> getGroups(ConfigurableEnvironment environment) {
        // 获取所有以local-cache.group开头的配置项
        Binder binder = Binder.get(environment);
        Map<String, String> groupMap = binder.bind(CACHE_GROUP, STRING_MAP).orElseGet(Collections::emptyMap);

        // 获取分组及其配置
        Map<String, LocalCacheGroupProperties> groups = new HashMap<>();
        for (Map.Entry<String, String> entry : groupMap.entrySet()) {
            String[] split = StringUtils.split(entry.getKey(), '.');
            if (split == null || split.length != 2) {
                throw new IllegalArgumentException("Invalid key: " + entry.getKey());
            }
            String groupName = split[0];
            String propertyName = split[1];

            LocalCacheGroupProperties itemProperties = groups.computeIfAbsent(
                    groupName, k -> new LocalCacheGroupProperties());
            try {
                BeanUtils.copyProperty(itemProperties, propertyName, entry.getValue());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException("Invalid property: " + entry.getKey() +
                        " => " + entry.getValue(), e);
            }
        }

        // 处理全局配置
        groups.forEach((key, value) -> copyPropertyFromGlobal(value));

        return groups;
    }

    /**
     * 如果分组中的字段值为空，则设置为全局分组中的字段值
     *
     * @param properties 分组属性
     */
    private void copyPropertyFromGlobal(LocalCacheGroupProperties properties) {
        for (Field field : LocalCacheGroupProperties.class.getDeclaredFields()) {
            try {
                if (PropertyUtils.getSimpleProperty(properties, field.getName()) == null) {
                    Object valueInGlobal = PropertyUtils.getSimpleProperty(globalGroupProperties, field.getName());
                    if (valueInGlobal != null) {
                        PropertyUtils.setSimpleProperty(properties, field.getName(), valueInGlobal);
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
