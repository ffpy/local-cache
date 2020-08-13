package com.tom.common.localcache.helper;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.tom.common.localcache.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * 用于读取和监听Nacos的配置
 *
 * @author 温龙盛
 * @date 2020/7/31 9:08
 */
@Component
@Slf4j
@ConditionalOnProperty("nacos-config-helper.dataId")
public class NacosConfigHelper implements InitializingBean {

    private final String dataId;

    private final String group;

    @NacosInjected
    private ConfigService configService;

    /** nacos上的配置 */
    private Properties properties;

    private final List<Listener> listeners = new LinkedList<>();

    public NacosConfigHelper(@Value("${nacos-config-helper.dataId}") String dataId,
                             @Value("${nacos-config-helper.group:DEFAULT_GROUP}") String group) {
        if (StringUtils.isBlank(dataId)) {
            throw new IllegalArgumentException("nacos-config-helper.dataId不能为空");
        }
        if (StringUtils.isBlank(group)) {
            throw new IllegalArgumentException("nacos-config-helper.group不能为空");
        }
        this.dataId = dataId;
        this.group = group;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        configService.addListener(dataId, group, new AbstractListener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                try {
                    Properties prop = new Properties();
                    prop.load(new StringReader(configInfo));
                    Properties oldProp = properties;
                    properties = prop;
                    synchronized (listeners) {
                        listeners.forEach(listener -> {
                            try {
                                listener.onChanged(oldProp, prop);
                            } catch (Exception e) {
                                // 捕获异常，防止影响到下一个监听器
                                log.error(e.getMessage(), e);
                            }
                        });
                    }
                } catch (IOException e) {
                    log.error("读取nacos配置失败", e);
                }
            }
        });
    }

    /**
     * 监听配置文件变更
     *
     * @param listener 监听器
     */
    public void addChangedListener(Listener listener) {
        synchronized (listeners) {
            listeners.add(Objects.requireNonNull(listener));
        }
        if (properties != null) {
            listener.onChanged(null, properties);
        }
    }

    /**
     * 监听指定配置的变更
     *
     * @param key      键
     * @param type     值类型Class
     * @param listener 监听器
     * @param <T>      值类型
     */
    public <T> void addChangedListener(String key, Class<T> type, Consumer<T> listener) {
        addChangedListener(((oldProp, prop) -> {
            String oldValue = getValue(oldProp, key);
            String newValue = getValue(prop, key);
            if (Objects.equals(oldValue, newValue)) {
                // 值没有变化
                return;
            }
            T value = ObjectUtils.parse(newValue, type);
            listener.accept(value);
        }));

        throw new UnsupportedOperationException();
    }

    /**
     * 获取Properties中指定键的值
     *
     * @param prop {@link Properties}
     * @param key  键名
     * @return 值，如果不存在则为null
     */
    private String getValue(Properties prop, String key) {
        return Optional.ofNullable(prop)
                .map(it -> it.getProperty(key))
                .map(String::trim)
                .orElse(null);
    }

    /**
     * 监听器接口
     */
    @FunctionalInterface
    public interface Listener {

        /**
         * 配置文件变更
         *
         * @param oldProp 旧的配置文件，可能为null
         * @param prop    新的配置文件
         */
        void onChanged(@Nullable Properties oldProp, Properties prop);
    }
}
