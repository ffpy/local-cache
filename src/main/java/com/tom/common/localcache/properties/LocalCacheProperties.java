package com.tom.common.localcache.properties;

import com.tom.common.localcache.constant.ConfigConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 本地缓存配置
 */
@Data
@ConfigurationProperties(ConfigConstant.PROPERTIES_PREFIX)
public class LocalCacheProperties {

    /** 全局配置是否启用缓存，设为false则每次都会请求数据，不会存在缓存中 */
    private boolean enable = true;

    /** 缓存分组配置 */
    private Map<String, String> group;

}
