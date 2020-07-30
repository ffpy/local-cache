package com.tom.common.localcache;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.SpringApplication;

class LocalCacheApplicationTests {

    public static void main(String[] args) {
        args = ArrayUtils.add(args, "--spring.profiles.active=test");
        SpringApplication.run(LocalCacheApplication.class, args);
    }
}
