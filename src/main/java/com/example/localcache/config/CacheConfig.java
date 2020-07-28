package com.example.localcache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
import java.util.*;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final String GROUP_PREFIX = "local-cache.group";

    private static final ConfigurationPropertyName CACHE_GROUP = ConfigurationPropertyName.of(GROUP_PREFIX);

    private static final Bindable<Map<String, String>> STRING_MAP = Bindable.mapOf(String.class,
            String.class);

    @Bean
    public CacheManager cacheManager(ConfigurableEnvironment environment) {
        Map<String, GroupItemProperties> groups = getGroups(environment);

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<Cache> caches = new ArrayList<>();
        for (Map.Entry<String, GroupItemProperties> entry : groups.entrySet()) {
            GroupItemProperties properties = entry.getValue();
            if (!properties.isEnable()) {
                continue;
            }

            Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
            if (StringUtils.isNotBlank(properties.getExpireAfterWrite())) {

            }

            caches.add(new CaffeineCache(entry.getKey(), caffeine.build()));
        }
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    private Map<String, GroupItemProperties> getGroups(ConfigurableEnvironment environment) {
        Binder binder = Binder.get(environment);
        Map<String, String> groupMap = binder.bind(CACHE_GROUP, STRING_MAP).orElseGet(Collections::emptyMap);

        Map<String, GroupItemProperties> groups = new HashMap<>();
        for (Map.Entry<String, String> entry : groupMap.entrySet()) {
            String[] split = StringUtils.split(entry.getKey(), '.');
            if (split.length != 2) {
                throw new IllegalArgumentException("Invalid key: " + entry.getKey());
            }
            String groupName = split[0];
            String propertyName = split[1];

            GroupItemProperties itemProperties = groups.computeIfAbsent(groupName, k -> new GroupItemProperties());
            try {
                BeanUtils.copyProperty(itemProperties, propertyName, entry.getValue());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException("Invalid property: " + entry.getKey() + " => " + entry.getValue(), e);
            }
        }

        return groups;
    }

    @Getter
    @Setter
    @ToString
    public static class GroupItemProperties {
        private boolean enable;
        private String expireAfterWrite;
    }

    @Getter
    @Setter
    @ToString
    public static class TimeValue {
        private static final Map<String, TimeUnit> UNIT_MAP = Map.of("s", TimeUnit.SECONDS);

//        private final int time;
//        private final TimeUnit unit;

        public TimeValue(String timeStr) {

        }
    }
}
