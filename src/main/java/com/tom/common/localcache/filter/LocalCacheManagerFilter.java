package com.tom.common.localcache.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.tom.common.localcache.config.LocalCacheManagerConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 本地缓存管理接口过滤器，用于提供管理接口
 *
 * @author 温龙盛
 * @date 2020-07-29 13:57
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "local-cache.manager.enable", matchIfMissing = true)
public class LocalCacheManagerFilter implements Filter {

    /** 管理接口访问路径 */
    public static final String PATH = "/local-cache-manager";

    /** 是否格式化打印参数名 */
    private static final String PARAM_PRETTY = "pretty";

    private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json; charset=utf-8";

    private ObjectMapper objectMapper;

    @Autowired
    private LocalCacheManagerConfig localCacheManagerConfig;

    @Override
    public void init(FilterConfig filterConfig) {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getServletPath();
        if (path.startsWith(PATH)) {
            GroupAndAction groupAndAction = getGroupAndActionFromPath(path);
            if (groupAndAction == null) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            processAction(request, response, groupAndAction.getGroup(), groupAndAction.getAction());
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    /**
     * 获取路径中的缓存分组名和动作名，如果获取失败则返回null
     *
     * @param path 请求路径
     * @return 缓存分组名和动作名
     */
    private GroupAndAction getGroupAndActionFromPath(String path) {
        if (path != null && path.length() > PATH.length()) {
            path = path.substring(PATH.length());
            String[] split = StringUtils.split(path, '/');
            if (split != null && split.length == 2) {
                return new GroupAndAction(split[0], split[1]);
            }
        }

        return null;
    }

    /**
     * 执行缓存管理动作
     *
     * @param request    request
     * @param response   response
     * @param groupName  缓存分组名
     * @param actionName 动作名
     * @throws IOException IO错误
     */
    private void processAction(HttpServletRequest request, HttpServletResponse response,
                               String groupName, String actionName) throws IOException {
        Cache<Object, Object> cache = localCacheManagerConfig.getCache(groupName);
        boolean prettyPrint = request.getParameter(PARAM_PRETTY) != null;
        if (cache == null) {
            write(response, HttpStatus.BAD_REQUEST, "缓存组不存在: " + groupName, prettyPrint);
            return;
        }
        CacheAction action = CacheAction.of(actionName).orElse(null);
        if (action == null) {
            write(response, HttpStatus.BAD_REQUEST, "不支持的动作: " + actionName, prettyPrint);
            return;
        }
        CacheAction.Result result = action.execute(cache, request);
        write(response, result.getStatus(), result.getData(), prettyPrint);
    }

    /**
     * 以JSON格式输出响应信息到{@link HttpServletResponse}并关闭流
     *
     * @param response    要输出的Response
     * @param status      响应码
     * @param obj         响应数据，会转换为JSON格式输出
     * @param prettyPrint 是否格式化输出
     * @throws IOException IO错误
     */
    private void write(HttpServletResponse response, HttpStatus status, Object obj, boolean prettyPrint) throws IOException {
        response.setStatus(status.value());
        response.setContentType(CONTENT_TYPE_APPLICATION_JSON);
        PrintWriter writer = response.getWriter();
        if (prettyPrint) {
            writer.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
        } else {
            writer.println(objectMapper.writeValueAsString(obj));
        }
        writer.flush();
        writer.close();
    }

    @Getter
    @ToString
    @RequiredArgsConstructor
    private static class GroupAndAction {

        /** 缓存分组名 */
        private final String group;

        /** 动作名 */
        private final String action;
    }
}
