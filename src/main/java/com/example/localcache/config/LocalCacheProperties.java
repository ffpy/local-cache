package com.example.localcache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties("local-cache")
public class LocalCacheProperties {

    private Map<String, String> group;

    private Map<String, String> global;
}
