package com.tom.common.localcache.config;

import com.tom.common.localcache.filter.LocalCacheManagerFilter;
import com.tom.common.localcache.properties.LocalCacheManagerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * 本地缓存管理接口拦截器配置
 */
@Configuration
@ConditionalOnProperty(value = "local-cache.manager.enable", matchIfMissing = true)
public class LocalCacheManagerFilterConfig {

    @Autowired
    private LocalCacheManagerFilter localCacheManagerFilter;

    @Autowired
    private LocalCacheManagerProperties localCacheManagerProperties;

    @Bean
    public FilterRegistrationBean<?> registerFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(localCacheManagerFilter);
        registration.addUrlPatterns(localCacheManagerProperties.getPath() + "/*");
        registration.setName("localCacheManagerFilter");
        registration.setOrder(1);
        return registration;
    }
}
