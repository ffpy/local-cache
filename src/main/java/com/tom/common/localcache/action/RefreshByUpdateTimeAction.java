package com.tom.common.localcache.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
     * @return 更新的值
     */
    Map<K, Value<V>> load(Date timeBound);

    /**
     * 更新项的值
     *
     * @param <V> 值类型
     */
    @AllArgsConstructor
    @Getter
    class Value<V> {

        /** 刷新值 */
        private final V value;

        /** 状态，true为正常，false为删除 */
        private final boolean status;
    }
}
