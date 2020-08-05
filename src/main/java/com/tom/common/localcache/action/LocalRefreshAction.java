package com.tom.common.localcache.action;

import java.util.Map;

/**
 * 局部刷新数据动作接口
 * 通过定时检查来刷新更改的数据
 *
 * @author 温龙盛
 * @date 2020/8/5 18:15
 */
public interface LocalRefreshAction<K, V> {

    Map<K, V> load();
}
