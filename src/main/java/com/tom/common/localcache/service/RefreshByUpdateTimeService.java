package com.tom.common.localcache.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.tom.common.localcache.action.RefreshByUpdateTimeAction;
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
import java.util.concurrent.TimeUnit;

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

    private final Map<String, ScheduledExecutorService> executorMap = new HashMap<>();

    private final Map<String, Integer> refreshIntervalMap = new HashMap<>();

    private final Map<String, RefreshByUpdateTimeAction<?, ?>> actionMap = new HashMap<>();

    @PostConstruct
    public void init() {
        cacheManagerConfig.getGroupPropertiesMap().forEach((group, prop) -> {
            if (StringUtils.isNotBlank(prop.getRefreshByUpdateTimeAction())) {
                if (prop.getRefreshByUpdateTimeInterval() <= 0) {
                    throw new IllegalArgumentException("refreshByUpdateTimeInterval必须大于0");
                }
                refreshIntervalMap.put(group, prop.getRefreshByUpdateTimeInterval());

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
                            int interval = Integer.parseInt(String.valueOf(prop.get(name)));
                            if (interval > 0) {
                                Integer oldInterval = refreshIntervalMap.get(group);
                                if (!Objects.equals(oldInterval, interval)) {
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
        ScheduledExecutorService oldExecutor = executorMap.get(group);
        if (oldExecutor != null) {
            oldExecutor.shutdownNow();
        }
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executorMap.put(group, executor);

        RefreshByUpdateTimeAction<?, ?> action = Objects.requireNonNull(actionMap.get(group));

        Integer interval = refreshIntervalMap.get(group);
        executor.scheduleAtFixedRate(() -> {
            log.info("{} refresh by update time start", group);
            Date timeBound = Date.from(LocalDateTime.now().minusMinutes(interval + 1)
                    .atZone(ZONE_ID).toInstant());
            Map<?, ? extends RefreshByUpdateTimeAction.Value<?>> data = action.load(timeBound);
            Cache<Object, Object> cache = cacheManager.getCaffeineCache(group);
            data.forEach((k, v) -> {
                if (!v.isStatus()) {
                    cache.invalidate(k);
                } else {
                    cache.put(k, v.getValue());
                }
            });
            log.info("{} refresh by update time end, size: {}", group, data.size());
            // TODO
//        }, interval, interval, TimeUnit.MINUTES);
        }, interval, interval, TimeUnit.SECONDS);
    }
}
