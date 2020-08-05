package com.tom.common.localcache.action;

import java.util.Map;

/**
 * 重新加载所有缓存数据的动作接口
 *
 * @author 温龙盛
 * @date 2020/7/31 11:25
 */
public interface ReloadAction<K, V> {

    /**
     * 返回重新加载后的缓存数据，如果返回null则不会执行刷新
     *
     * @return 缓存数据（缓存键 -> 缓存值）
     */
    Map<K, V> reload();
}
