package com.example.localcache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 用于IDE编写时提示自动补全
 */
@SuppressWarnings("ConfigurationProperties")
@Data
@ConfigurationProperties("local-cache")
public class LocalCacheProperties {

    private Map<String, String> group;

    private Map<String, String> global;
}
