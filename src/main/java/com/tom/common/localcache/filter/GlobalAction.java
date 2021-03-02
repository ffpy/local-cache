package com.tom.common.localcache.filter;

import com.tom.common.localcache.config.LocalCacheManagerConfig;
import com.tom.common.localcache.util.SpringContextUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 全局接口动作
 */
@Getter
@RequiredArgsConstructor
public enum GlobalAction {

    /** 列出所有分组 */
    GROUPS("groups") {
        @Override
        public Response execute(HttpServletRequest request) {
            return Response.success(SpringContextUtils.getBean(LocalCacheManagerConfig.class)
                    .getGroupPropertiesMap());
        }
    },

    ;

    /** 动作名到动作的映射 */
    private static Map<String, GlobalAction> actionMap;

    /** 动作名 */
    private final String name;

    /**
     * 获取动作名对应的动作
     *
     * @param name 动作名
     * @return 动作
     */
    public static Optional<GlobalAction> of(String name) {
        if (actionMap == null) {
            synchronized (GlobalAction.class) {
                if (actionMap == null) {
                    Map<String, GlobalAction> map = new HashMap<>(16);
                    for (GlobalAction action : values()) {
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
     * @param request Http请求
     * @return 动作响应信息
     */
    public abstract Response execute(HttpServletRequest request);

}
