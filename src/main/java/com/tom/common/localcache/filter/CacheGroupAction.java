package com.tom.common.localcache.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.tom.common.localcache.action.ReloadAction;
import com.tom.common.localcache.config.LocalCacheManagerConfig;
import com.tom.common.localcache.manager.LocalCacheManager;
import com.tom.common.localcache.properties.LocalCacheGroupProperties;
import com.tom.common.localcache.util.SpringContextUtils;
import com.tom.common.localcache.vo.LocalCacheStats;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 缓存分组接口动作
 *
 * @author 温龙盛
 * @date 2020/7/29 13:06
 */
@Getter
@RequiredArgsConstructor
@Slf4j
public enum CacheGroupAction {

    /** 获取指定缓存的值 */
    GET("get") {
        @Override
        public Response execute(String group, Cache<Object, Object> cache, HttpServletRequest request) {
            String key = request.getParameter(PARAM_KEY);

            if (StringUtils.isBlank(key)) {
                return Response.error("缺少key参数");
            }
            return Response.success(cache.getIfPresent(key));
        }
    },

    /** 列出所有缓存和值 */
    LIST("list") {
        @Override
        public Response execute(String group, Cache<Object, Object> cache, HttpServletRequest request) {
            String query = request.getParameter(PARAM_QUERY);
            boolean regex = Boolean.parseBoolean(request.getParameter(PARAM_REGEX));
            PageRequest pageable = getPageRequest(request);

            Map<Object, Object> map;
            if (StringUtils.isBlank(query)) {
                map = cache.asMap();
            } else {
                map = new HashMap<>(cache.asMap());
                if (regex) {
                    Predicate<String> predicate = Pattern.compile(query).asPredicate();
                    map.entrySet().removeIf(entry -> !predicate.test(String.valueOf(entry.getKey())));
                } else {
                    map.entrySet().removeIf(entry -> !String.valueOf(entry.getKey()).contains(query));
                }
            }

            List<Item> result = toList(map, pageable);
            return Response.success(new PageImpl<>(result, pageable, map.size()));
        }

        private PageRequest getPageRequest(HttpServletRequest request) {
            int page = getIntParam(request.getParameter(PARAM_PAGE))
                    // 最小值
                    .map(it -> Math.max(it - 1, 0))
                    .orElse(0);
            int size = getIntParam(request.getParameter(PARAM_SIZE))
                    // 最小值
                    .map(it -> Math.max(it, 1))
                    // 最大值
                    .map(it -> Math.min(it, MAX_PAGE_SIZE))
                    .orElse(DEFAULT_PAGE_SIZE);
            return PageRequest.of(page, size);
        }

        private List<Item> toList(Map<?, ?> map, PageRequest pageable) {
            ObjectMapper objectMapper = SpringContextUtils.getBean(ObjectMapper.class);
            return map.entrySet().stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .map(entry -> new Item(entry.getKey(), formatMaxValue(objectMapper, entry.getValue())))
                    .collect(Collectors.toList());
        }
    },

    /** 删除指定缓存的值 */
    DELETE("delete") {
        @Override
        public Response execute(String group, Cache<Object, Object> cache, HttpServletRequest request) {
            cache.invalidate(request.getParameter(PARAM_KEY));
            return Response.success();
        }
    },

    /** 清空缓存 */
    CLEAR("clear") {
        @Override
        public Response execute(String group, Cache<Object, Object> cache, HttpServletRequest request) {
            cache.invalidateAll();
            return Response.success();
        }
    },

    /** 刷新指定缓存的值 */
    REFRESH("refresh") {
        @Override
        public Response execute(String group, Cache<Object, Object> cache, HttpServletRequest request) {
            String key = request.getParameter(PARAM_KEY);
            if (StringUtils.isBlank(key)) {
                return Response.error("缺少key参数");
            }

            if (!(cache instanceof LoadingCache)) {
                return Response.error("当前分组不支持此操作");
            }

            LoadingCache<Object, Object> loadingCache = (LoadingCache<Object, Object>) cache;
            loadingCache.refresh(key);

            return Response.success();
        }
    },

    /** 重新加载所有数据 */
    RELOAD_ALL("reload-all") {
        @Override
        public Response execute(String group, Cache<Object, Object> cache, HttpServletRequest request) {
            LocalCacheGroupProperties properties = SpringContextUtils.getBean(
                    LocalCacheManagerConfig.class).getGroupProperties(group);
            if (properties == null) {
                return Response.error("找不到分组: " + group);
            }

            LocalCacheManager cacheManager = SpringContextUtils.getBean(LocalCacheManager.class);
            String reloadActionBeanName = properties.getReloadAction();
            ReloadAction<?, ?> reloadAction = Optional.ofNullable(reloadActionBeanName)
                    .filter(StringUtils::isNotBlank)
                    .map(beanName -> {
                        try {
                            return SpringContextUtils.getBean(beanName, ReloadAction.class);
                        } catch (BeansException e) {
                            log.error(e.getMessage(), e);
                            return null;
                        }
                    }).orElse(null);
            if (reloadAction == null) {
                return Response.error("当前分组不支持此操作");
            }
            try {
                cacheManager.reloadAll(group, reloadAction.reload());
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage(), e);
                return Response.error(e.getMessage());
            }

            return Response.success();
        }
    },

    /** 获取缓存统计信息 */
    STATS("stats") {
        @Override
        public Response execute(String group, Cache<Object, Object> cache, HttpServletRequest request) {
            return Response.success(new LocalCacheStats(cache.estimatedSize(), cache.stats()));
        }
    },

    ;
    private static final String PARAM_REGEX = "regex";
    private static final String PARAM_QUERY = "query";
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_SIZE = "size";
    private static final String PARAM_KEY = "key";

    /** 默认分页大小 */
    private static final int DEFAULT_PAGE_SIZE = 50;

    /** 最大分页大小 */
    private static final int MAX_PAGE_SIZE = 1000;

    /** {@link #LIST}返回内容的最大长度 */
    private static final int MAX_VALUE_LENGTH = 170;

    /** {@link #LIST}超出最大长度后的后缀 */
    private static final String MAX_VALUE_END = "...";

    /** 动作名到动作的映射 */
    private static Map<String, CacheGroupAction> actionMap;

    /** 动作名 */
    private final String name;

    /**
     * 获取动作名对应的动作
     *
     * @param name 动作名
     * @return 动作
     */
    public static Optional<CacheGroupAction> of(String name) {
        if (actionMap == null) {
            synchronized (CacheGroupAction.class) {
                if (actionMap == null) {
                    Map<String, CacheGroupAction> map = new HashMap<>(16);
                    for (CacheGroupAction action : values()) {
                        map.put(action.name, action);
                    }
                    actionMap = map;
                }
            }
        }
        return Optional.ofNullable(actionMap.get(name));
    }

    /**
     * 执行动作并返回响应信息
     *
     * @param group   分组名
     * @param cache   要执行动作的缓存
     * @param request Http请求
     * @return 动作响应信息
     */
    public abstract Response execute(String group, Cache<Object, Object> cache, HttpServletRequest request);

    /**
     * 格式化内容的最大长度
     *
     * @param objectMapper ObjectMapper
     * @param value        要格式化的值
     * @return 格式化后的值，如果格式化失败返回原值
     */
    private static Object formatMaxValue(ObjectMapper objectMapper, Object value) {
        if (value == null) {
            return null;
        }
        try {
            String valueStr = objectMapper.writeValueAsString(value);
            if (valueStr.length() + MAX_VALUE_END.length() > MAX_VALUE_LENGTH) {
                valueStr = valueStr.substring(0, MAX_VALUE_LENGTH - MAX_VALUE_END.length()) + MAX_VALUE_END;
            }
            return valueStr;
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return value;
        }
    }

    /**
     * 字符串转整型
     *
     * @param value 字符串
     * @return 数字，字符串为空或格式不正确返回{@link Optional#empty()}
     */
    private static Optional<Integer> getIntParam(String value) {
        return Optional.ofNullable(value)
                .filter(StringUtils::isNotBlank)
                .map(it -> {
                    try {
                        return Integer.parseInt(it);
                    } catch (Exception ignored) {
                        // 转换失败返回null
                    }
                    return null;
                });
    }

    @Data
    @AllArgsConstructor
    private static class Item {
        private Object key;
        private Object value;
    }
}
