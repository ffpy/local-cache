package com.tom.common.localcache.config;

import com.tom.common.localcache.helper.NacosConfigHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalCacheNacosConfig {

    @Bean
    @ConditionalOnMissingBean
    public NacosConfigHelper nacosConfigHelper(@Value("${nacos.dataId}") String dataId,
                                               @Value("${nacos.group:DEFAULT_GROUP}") String group) {
        // TODO
        return new NacosConfigHelper(dataId, group);
    }
}
