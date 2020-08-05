package com.tom.common.localcache.config;

import com.tom.common.localcache.Holder;
import com.tom.common.localcache.action.ReloadAction;
import com.tom.common.localcache.manager.LocalCacheManager;
import com.tom.common.localcache.properties.LocalCacheProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

/**
 * 定时任务配置
 *
 * @author 温龙盛
 * @date 2020/7/30 16:54
 */
@Slf4j
@Configuration
public class LocalCacheScheduleConfig implements SchedulingConfigurer {

    @Autowired
    private LocalCacheManagerConfig cacheManagerConfig;

    @Autowired
    private LocalCacheManager cacheManager;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private LocalCacheProperties localCacheProperties;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        Holder<Boolean> hasTask = new Holder<>(false);
        cacheManagerConfig.getGroupPropertiesMap().forEach((group, prop) -> {
            String cron = prop.getReloadCron();
            if (StringUtils.isNotBlank(cron)) {
                if (StringUtils.isBlank(prop.getReloadAction())) {
                    throw new IllegalArgumentException("缓存分组" + group + "的reload-action属性不能为空");
                }
                ReloadAction<?, ?> reloadAction = context.getBean(prop.getReloadAction(), ReloadAction.class);
                taskRegistrar.addCronTask(new ReloadAllRunnable(cacheManager, group, reloadAction), cron);
                log.info("cache {} add reload task, cron: {}", group, cron);
                hasTask.value = true;
            }
        });

        if (hasTask.value) {
            int poolSize = localCacheProperties.getSchedulePoolSize();
            if (poolSize > 0) {
                taskRegistrar.setScheduler(Executors.newScheduledThreadPool(poolSize));
            }
        }
    }
}
