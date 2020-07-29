package com.example.localcache.filter;

import com.example.localcache.vo.LocalCacheStats;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 缓存管理接口动作
 *
 * @author 温龙盛
 * @date 2020/7/29 13:06
 */
@Getter
@RequiredArgsConstructor
public enum CacheAction {

    /** 清空缓存 */
    CLEAR("clear") {
        @Override
        public Result execute(Cache<Object, Object> cache, HttpServletRequest request) {
            cache.invalidateAll();
            return success();
        }
    },

    /** 获取指定缓存的值 */
    GET("get") {
        @Override
        public Result execute(Cache<Object, Object> cache, HttpServletRequest request) {
            String key = request.getParameter("key");
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
            return success(cache.asMap());
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
    /** 操作成功时的返回信息 */
    private static final String RESULT_SUCCESS = "success";
    /** 动作名到动作的映射 */
    private static Map<String, CacheAction> actionMap;

    /** 动作名 */
    private final String name;

    /**
     * 获取动作名对应的动作
     *
     * @param name 动作名
     * @return 动作
     */
    public static Optional<CacheAction> of(String name) {
        if (actionMap == null) {
            synchronized (CacheAction.class) {
                if (actionMap == null) {
                    Map<String, CacheAction> map = new HashMap<>(16);
                    for (CacheAction action : values()) {
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

    /**
     * 创建响应成功结果
     *
     * @return 结果
     */
    protected Result success() {
        return success(RESULT_SUCCESS);
    }

    /**
     * 创建响应成功结果
     *
     * @param data 响应数据
     * @return 结果
     */
    protected Result success(Object data) {
        return new Result(HttpStatus.OK, data);
    }

    /**
     * 创建响应失败结果
     *
     * @param msg 错误信息
     * @return 结果
     */
    protected Result error(String msg) {
        return new Result(HttpStatus.BAD_REQUEST, msg);
    }

    /**
     * 动作执行结果
     */
    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class Result {
        /** 响应码 */
        private final HttpStatus status;

        /** 响应数据 */
        private final Object data;
    }
}
