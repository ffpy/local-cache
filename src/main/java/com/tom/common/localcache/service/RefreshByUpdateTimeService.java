package com.tom.common.localcache.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.tom.common.localcache.action.RefreshByUpdateTimeAction;
import com.tom.common.localcache.cache.TimeValue;
import com.tom.common.localcache.cache.UnmodifiableCache;
import com.tom.common.localcache.config.LocalCacheManagerConfig;
import com.tom.common.localcache.manager.LocalCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@Service
public class RefreshByUpdateTimeService {

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    @Autowired
    private ApplicationContext context;

    @Autowired
    private LocalCacheManager cacheManager;

    @Autowired
    private LocalCacheManagerConfig cacheManagerConfig;

    /** 分组名 -> 调度服务 */
    private final Map<String, ScheduledExecutorService> executorMap = new HashMap<>();

    /** 分组名 -> 刷新时间 */
    private final Map<String, TimeValue> refreshIntervalMap = new HashMap<>();

    /** 分组名 -> 刷新动作 */
    private final Map<String, RefreshByUpdateTimeAction<?, ?>> actionMap = new HashMap<>();

    @PostConstruct
    public void init() {
        cacheManagerConfig.getGroupPropertiesMap().forEach((group, prop) -> {
            if (StringUtils.isNotBlank(prop.getRefreshByUpdateTimeAction())) {
                refreshIntervalMap.put(group, TimeValue.parse(prop.getRefreshByUpdateTimeInterval()));

                RefreshByUpdateTimeAction<?, ?> action = context.getBean(prop.getRefreshByUpdateTimeAction(),
                        RefreshByUpdateTimeAction.class);
                actionMap.put(group, action);

                startGroupRefresh(group);
            }
        });
    }

    @PreDestroy
    public void destroy() {
        executorMap.forEach((k, v) -> v.shutdownNow());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void startGroupRefresh(String group) {
        ScheduledExecutorService executor = getNewExecutor(group);
        RefreshByUpdateTimeAction<?, ?> action = Objects.requireNonNull(actionMap.get(group));
        TimeValue interval = refreshIntervalMap.get(group);

        int intervalValue = Math.max(interval.getValue(), 1);
        executor.scheduleAtFixedRate(() -> {
            try {
                log.info("{} refresh by update time start", group);
                // +5用于防止因定时任务的延时而导致有数据没有扫描到
                Date timeBound = Date.from(LocalDateTime.now().minusMinutes(
                        interval.getUnit().toMinutes(interval.getValue()) + 5)
                        .atZone(ZONE_ID).toInstant());
                Cache<Object, Object> cache = cacheManager.getCaffeineCache(group);
                Map<?, ?> data = action.load(timeBound, new UnmodifiableCache(cache));
                data.forEach((k, v) -> {
                    if (v == null) {
                        cache.invalidate(k);
                    } else {
                        cache.put(k, v);
                    }
                    log.info("{} refresh: {} => {}", group, k, v);
                });
                log.info("{} refresh by update time end, size: {}", group, data.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }, intervalValue, intervalValue, interval.getUnit());
    }

    private ScheduledExecutorService getNewExecutor(String group) {
        ScheduledExecutorService oldExecutor = executorMap.get(group);
        if (oldExecutor != null) {
            oldExecutor.shutdownNow();
        }
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executorMap.put(group, executor);
        return executor;
    }
}
