package com.tom.common.localcache.properties;

import com.tom.common.localcache.constant.ConfigPrefix;
import com.tom.common.localcache.constant.PathConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 本地缓存管理接口配置
 *
 * @author 温龙盛
 * @date 2020/7/29 17:36
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(ConfigPrefix.MANAGER)
public class LocalCacheManagerProperties {

    /** 是否启用管理接口 */
    private boolean enable = true;

    /** 管理接口路径 */
    private String path = PathConstant.DEFAULT_PATH;

    public void setPath(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        this.path = path;
    }
}
