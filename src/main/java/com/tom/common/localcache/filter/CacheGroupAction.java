package com.tom.common.localcache.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.tom.common.localcache.ReloadAction;
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

import javax.servlet.http.HttpServletRequest;
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

            if (StringUtils.isBlank(query)) {
                return Response.success(toList(cache.asMap()));
            } else {
                Map<Object, Object> map = new HashMap<>(cache.asMap());
                if (regex) {
                    Predicate<String> predicate = Pattern.compile(query).asPredicate();
                    map.entrySet().removeIf(entry -> !predicate.test(String.valueOf(entry.getKey())));
                } else {
                    map.entrySet().removeIf(entry -> !String.valueOf(entry.getKey()).contains(query));
                }
                return Response.success(toList(map));
            }
        }

        private List<Item> toList(Map<?, ?> map) {
            return map.entrySet().stream()
                    .map(entry -> new Item(entry.getKey(), entry.getValue()))
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
    private static final String PARAM_KEY = "key";

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

    @Data
    @AllArgsConstructor
    private static class Item {
        private Object key;
        private Object value;
    }
}
