package com.tom.common.localcache.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.tom.common.localcache.vo.LocalCacheStats;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

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
public enum CacheGroupAction implements Action {

    /** 获取指定缓存的值 */
    GET("get") {
        @Override
        public Result execute(Cache<Object, Object> cache, HttpServletRequest request) {
            String key = request.getParameter(PARAM_KEY);
            if (StringUtils.isBlank(key)) {
                return error("缺少key参数");
            }
            return success(cache.getIfPresent(key));
        }
    },

    /** 列出所有缓存和值 */
    LIST("list") {
        @Override
        public Result execute(Cache<Object, Object> cache, HttpServletRequest request) {
            String query = request.getParameter(PARAM_QUERY);
            boolean regex = Boolean.parseBoolean(request.getParameter(PARAM_REGEX));

            if (StringUtils.isBlank(query)) {
                return success(toList(cache.asMap()));
            } else {
                Map<Object, Object> map = new HashMap<>(cache.asMap());
                if (regex) {
                    Predicate<String> predicate = Pattern.compile(query).asPredicate();
                    map.entrySet().removeIf(entry -> !predicate.test(String.valueOf(entry.getKey())));
                } else {
                    map.entrySet().removeIf(entry -> !String.valueOf(entry.getKey()).contains(query));
                }
                return success(toList(map));
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
        public Result execute(Cache<Object, Object> cache, HttpServletRequest request) {
            cache.invalidate(request.getParameter(PARAM_KEY));
            return success();
        }
    },

    /** 清空缓存 */
    CLEAR("clear") {
        @Override
        public Result execute(Cache<Object, Object> cache, HttpServletRequest request) {
            cache.invalidateAll();
            return success();
        }
    },

    /** 获取缓存统计信息 */
    STATS("stats") {
        @Override
        public Result execute(Cache<Object, Object> cache, HttpServletRequest request) {
            return success(new LocalCacheStats(cache.estimatedSize(), cache.stats()));
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
     * @param cache   要执行动作的缓存
     * @param request Http请求
     * @return 动作响应信息
     */
    public abstract Result execute(Cache<Object, Object> cache, HttpServletRequest request);

    @Data
    @AllArgsConstructor
    private static class Item {
        private Object key;
        private Object value;
    }
}
