package com.tom.common.localcache.config;

import com.tom.common.localcache.filter.LocalCacheManagerFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * 本地缓存管理接口拦截器配置
 */
@Configuration
public class LocalCacheManagerFilterConfig {

    @Autowired
    private LocalCacheManagerFilter localCacheManagerFilter;

    @Bean
    public FilterRegistrationBean<?> registerFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(localCacheManagerFilter);
        registration.addUrlPatterns(LocalCacheManagerFilter.PATH + "/*");
        registration.setName("localCacheManagerFilter");
        registration.setOrder(1);
        return registration;
    }
}
