package com.tom.common.localcache;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LocalCacheApplication {

    public static void main(String[] args) {
        args = ArrayUtils.add(args, "--spring.profiles.active=test");
        SpringApplication.run(LocalCacheApplication.class, args);
    }
}
