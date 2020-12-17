package com.tom.common.localcache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LocalCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocalCacheApplication.class, args);
    }
}
