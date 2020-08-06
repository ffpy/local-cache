package com.tom.common.localcache.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.tom.common.localcache.action.RefreshByUpdateTimeAction;
import com.tom.common.localcache.cache.TimeValue;
import com.tom.common.localcache.config.LocalCacheManagerConfig;
import com.tom.common.localcache.constant.ConfigPrefix;
import com.tom.common.localcache.helper.NacosConfigHelper;
import com.tom.common.localcache.manager.LocalCacheManager;
import com.tom.common.localcache.util.MyStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Enumeration;
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
    private NacosConfigHelper nacosConfigHelper;

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

        nacosConfigHelper.addChangedListener((oldProp, prop) -> {
            Enumeration<Object> names = prop.keys();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                if (name.startsWith(ConfigPrefix.GROUP)) {
                    String str = name.substring(ConfigPrefix.GROUP.length());
                    String[] split = StringUtils.split(str, '.');
                    if (split == null || split.length != 2) {
                        continue;
                    }
                    String group = split[0];
                    String key = MyStringUtils.kebabCaseToCamelCase(split[1]);
                    if ("refreshByUpdateTimeInterval".equals(key)) {
                        try {
                            Object intervalValue = prop.get(name);
                            if (intervalValue == null) {
                                continue;
                            }
                            TimeValue interval = TimeValue.parse(String.valueOf(intervalValue));
                            if (interval.getValue() > 0) {
                                TimeValue oldInterval = refreshIntervalMap.get(group);
                                if (!Objects.equals(oldInterval, interval)) {
                                    log.info("update {}={}", name, interval);
                                    refreshIntervalMap.put(group, interval);
                                    startGroupRefresh(group);
                                }
                            } else {
                                log.error("属性{}必须大于0", name);
                            }
                        } catch (NumberFormatException e) {
                            log.error("属性" + name + "格式不正确", e);
                        }
                    }
                }
            }
        });
    }

    private void startGroupRefresh(String group) {
        ScheduledExecutorService executor = getNewExecutor(group);
        RefreshByUpdateTimeAction<?, ?> action = Objects.requireNonNull(actionMap.get(group));
        TimeValue interval = refreshIntervalMap.get(group);

        int intervalValue = Math.max(interval.getValue(), 1);
        executor.scheduleAtFixedRate(() -> {
            log.info("{} refresh by update time start", group);
            // +1用于防止因定时任务的延时而导致有数据没有扫描到
            Date timeBound = Date.from(LocalDateTime.now().minusMinutes(
                    interval.getUnit().toMinutes(interval.getValue()) + 1)
                    .atZone(ZONE_ID).toInstant());
            Map<?, ?> data = action.load(timeBound);
            Cache<Object, Object> cache = cacheManager.getCaffeineCache(group);
            data.forEach((k, v) -> {
                if (v == null) {
                    cache.invalidate(k);
                } else {
                    cache.put(k, v);
                }
            });
            log.info("{} refresh by update time end, size: {}", group, data.size());
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
