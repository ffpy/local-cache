package com.tom.common.localcache.config;

import com.tom.common.localcache.action.ReloadAction;
import com.tom.common.localcache.manager.LocalCacheManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author 温龙盛
 * @date 2020/8/5 16:49
 */
@Slf4j
class ReloadAllRunnable implements Runnable {
    private final LocalCacheManager cacheManager;
    private final String group;
    private final ReloadAction<?, ?> reloadAction;

    public ReloadAllRunnable(LocalCacheManager cacheManager, String group, ReloadAction<?, ?> reloadAction) {
        this.cacheManager = cacheManager;
        this.group = group;
        this.reloadAction = reloadAction;
    }

    @Override
    public void run() {
        log.info("{} reload all start", group);
        Map<?, ?> data = reloadAction.reload();
        if (data != null) {
            cacheManager.reloadAll(group, data);
            log.info("{} reload all end, size: {}", group, data.size());
        } else {
            log.info("{} reload all fail.", group);
        }
    }
}
