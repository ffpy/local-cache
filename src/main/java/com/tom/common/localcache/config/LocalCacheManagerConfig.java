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
import com.tom.common.localcache.util.MyStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
@ConditionalOnProperty(value = "local-cache.manager.enable", matchIfMissing = true)
public class LocalCacheManagerConfig {

    /** 本地缓存管理器Bean名称 */
    public static final String BEAN_NAME = "localCacheManager";

    private static final ConfigurationPropertyName CACHE_GROUP = ConfigurationPropertyName.of(ConfigPrefix.GROUP);

    private static final Bindable<Map<String, String>> STRING_MAP = Bindable.mapOf(String.class,
            String.class);

    @Autowired
    private LocalCacheProperties localCacheProperties;

    @Autowired
    private LocalCacheGlobalGroupProperties globalGroupProperties;

    /** 缓存分组配置信息 */
    private Map<String, LocalCacheGroupProperties> groupPropertiesMap;

    /**
     * 本地缓存管理器
     */
    @Bean(name = BEAN_NAME)
    public LocalCacheManager localCacheManager(ConfigurableEnvironment environment, ApplicationContext applicationContext) {
        Map<String, LocalCacheGroupProperties> groups = getGroupPropertiesMap(environment);
        groupPropertiesMap = groups;

        LocalCacheManagerImpl cacheManager = new LocalCacheManagerImpl();
        Map<String, Cache<Object, Object>> caffeineCacheMap = new HashMap<>();
        for (Map.Entry<String, LocalCacheGroupProperties> entry : groups.entrySet()) {
            String groupName = entry.getKey();
            LocalCacheGroupProperties prop = entry.getValue();
            Cache<Object, Object> cache = createCache(applicationContext, prop);
            caffeineCacheMap.put(groupName, cache);
        }

        cacheManager.setCaches(caffeineCacheMap);
        return cacheManager;
    }

    /**
     * 缓存分组及对应的属性（分组名 -> 分组属性）
     */
    private Map<String, LocalCacheGroupProperties> getGroupPropertiesMap(ConfigurableEnvironment environment) {
        // 获取所有以local-cache.group开头的配置项
        Binder binder = Binder.get(environment);
        Map<String, String> groupMap = binder.bind(CACHE_GROUP, STRING_MAP).orElseGet(Collections::emptyMap);

        // 获取分组及其配置
        Map<String, LocalCacheGroupProperties> groups = new HashMap<>();
        Set<String> validPropertiesNames = getValidPropertiesSet();
        Predicate<String> groupNamePredicate = Pattern.compile("^[\\w-]+$").asPredicate();
        for (Map.Entry<String, String> entry : groupMap.entrySet()) {
            String[] split = StringUtils.split(entry.getKey(), '.');
            if (split == null || split.length != 2) {
                throw new IllegalArgumentException("Invalid key: " + entry.getKey());
            }
            String groupName = split[0];
            String propertyName = split[1];

            // 检查分组名格式
            if (!groupNamePredicate.test(groupName)) {
                throw new IllegalArgumentException("分组名只能包含数字、字母、下划线和中划线: " + groupName);
            }

            // 属性名中划线转驼峰
            if (StringUtils.contains(propertyName, '-')) {
                propertyName = MyStringUtils.kebabCaseToCamelCase(propertyName);
            }

            if (!validPropertiesNames.contains(propertyName)) {
                throw new IllegalArgumentException("无效的配置: " + ConfigPrefix.GROUP + "." + entry.getKey());
            }

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

        // 去掉没启用的缓存
        groups.entrySet().removeIf(it -> !it.getValue().isEnable());

        return groups;
    }

    /**
     * 获取缓存分组配置信息Map
     *
     * @return 配置信息Map
     * @throws IllegalStateException 如果还没加载完成
     */
    public Map<String, LocalCacheGroupProperties> getGroupPropertiesMap() {
        if (groupPropertiesMap == null) {
            throw new IllegalStateException("还没加载完成");
        }
        return Collections.unmodifiableMap(groupPropertiesMap);
    }

    /**
     * 获取获取执行缓存分组的配置信息
     *
     * @param group 分组名称
     * @return 配置信息
     * @throws IllegalArgumentException 如果分组不存在
     * @throws IllegalStateException    如果还没加载完成
     */
    public LocalCacheGroupProperties getGroupProperties(String group) {
        LocalCacheGroupProperties properties = getGroupPropertiesMap().get(group);
        if (properties == null) {
            throw new IllegalArgumentException("分组不存在: " + group);
        }
        return properties;
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
        if (isGroupDebug(properties)) {
            if (cache instanceof LoadingCache) {
                cache = new EmptyLoadingCache((LoadingCache<Object, Object>) cache);
            } else {
                cache = new EmptyCache(cache);
            }
        }
        return cache;
    }

    /**
     * 获取有效的配置名称集合
     *
     * @return 有效的配置名称集合
     */
    private Set<String> getValidPropertiesSet() {
        return Arrays.stream(LocalCacheGroupProperties.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());
    }

    /**
     * 判断指定分组是否开启调试
     *
     * @param properties 分组属性
     * @return true为启用，false反之
     */
    private boolean isGroupDebug(LocalCacheGroupProperties properties) {
        return localCacheProperties.isDebug() || properties.isDebug();
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
                    try {
                        Object valueInGlobal = PropertyUtils.getSimpleProperty(globalGroupProperties, field.getName());
                        if (valueInGlobal != null) {
                            PropertyUtils.setSimpleProperty(properties, field.getName(), valueInGlobal);
                        }
                    } catch (NoSuchMethodException ignore) {
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
