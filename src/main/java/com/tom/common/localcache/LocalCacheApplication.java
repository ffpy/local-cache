package com.tom.common.localcache;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
// TODO 测试
@NacosPropertySource(dataId = "account", autoRefreshed = true)
@EnableScheduling
public class LocalCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocalCacheApplication.class, args);
    }
}
