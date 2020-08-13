package com.tom.common.localcache.properties;

import com.tom.common.localcache.constant.ConfigPrefix;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 本地缓存配置
 */
@Data
@ConfigurationProperties(ConfigPrefix.ROOT)
public class LocalCacheProperties {

    /** 全局配置是否启用缓存 */
    private boolean enable = true;

    /** 全局配置是否开启调试模式，设为true则每次都会请求数据，不会存在缓存中 */
    private boolean debug;

    /** 定时任务线程池大小，如果值为0则不创建线程池 */
    private int schedulePoolSize = 4;

    /** 缓存分组配置 */
    private Map<String, String> group;

}
