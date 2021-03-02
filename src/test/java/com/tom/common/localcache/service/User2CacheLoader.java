package com.tom.common.localcache.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.tom.common.localcache.bean.User2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于测试的{@link CacheLoader}
 */
@Component
public class User2CacheLoader implements CacheLoader<String, User2> {

    private final Map<String, Integer> loadCounter = new HashMap<>();

    public Map<String, Integer> getLoadCounter() {
        return loadCounter;
    }

    @Override
    public User2 load(String username) throws Exception {
        loadCounter.compute(username, (key, value) -> {
            if (value == null) {
                return 1;
            }
            return value + 1;
        });
        if ("null".equals(username)) {
            return null;
        }
        return new User2(username);
    }
}
