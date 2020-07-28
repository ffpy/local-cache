package com.example.localcache.config;

import com.example.localcache.filter.CacheManagerFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class CacheManagerFilterConfig {

    @Autowired
    private CacheManagerFilter cacheManagerFilter;

    @Bean
    public FilterRegistrationBean<?> registerFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(cacheManagerFilter);
        registration.addUrlPatterns("/cache-manager");
        registration.setName("cacheManagerFilter");
        registration.setOrder(1);
        return registration;
    }
}
