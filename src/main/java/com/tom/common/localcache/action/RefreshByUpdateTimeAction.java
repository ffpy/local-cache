package com.tom.common.localcache.action;

import com.github.benmanes.caffeine.cache.Cache;

import java.util.Date;
import java.util.Map;

/**
 * 增量刷新数据动作接口
 * 通过定时检查更新时间来刷新更改的数据
 *
 * @author 温龙盛
 * @date 2020/8/5 18:15
 */
public interface RefreshByUpdateTimeAction<K, V> {

    /**
     * 返回需要刷新的数据
     *
     * @param timeBound 时间范围，查出更新时间大于等于此值的数据
     * @param cache     当前分组的缓存器
     * @return 更新的值，如果值为null，则会删除这个键的缓存
     */
    Map<K, V> load(Date timeBound, Cache<K, V> cache);
}
