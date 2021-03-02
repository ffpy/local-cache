package com.tom.common.localcache.service;

import com.tom.common.localcache.action.ReloadAction;
import com.tom.common.localcache.config.LocalCacheManagerConfig;
import com.tom.common.localcache.config.ReloadAllRunnable;
import com.tom.common.localcache.manager.LocalCacheManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class ReloadService implements ApplicationContextAware {

    @Autowired
    private LocalCacheManagerConfig cacheManagerConfig;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        LocalCacheManager cacheManager = context.getBean(LocalCacheManager.class);
        cacheManagerConfig.getGroupPropertiesMap().forEach((group, prop) -> {
            // reload-on-start
            if (prop.getReloadOnStart() == Boolean.TRUE) {
                if (StringUtils.isBlank(prop.getReloadAction())) {
                    throw new IllegalArgumentException("缓存分组" + group + "的reload-action属性不能为空");
                }

                ReloadAction<?, ?> reloadAction = context.getBean(prop.getReloadAction(), ReloadAction.class);
                new ReloadAllRunnable(cacheManager, group, reloadAction).run();
            }
        });
    }
}
