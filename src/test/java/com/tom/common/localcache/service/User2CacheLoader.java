package com.tom.common.localcache.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.tom.common.localcache.bean.User2;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于测试的{@link CacheLoader}
 *
 * @author 温龙盛
 * @date 2020/7/29 18:28
 */
@Component
public class User2CacheLoader implements CacheLoader<String, User2> {

    private final Map<String, Integer> loadCounter = new HashMap<>();

    public Map<String, Integer> getLoadCounter() {
        return loadCounter;
    }

    @Nullable
    @Override
    public User2 load(@NonNull String username) throws Exception {
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
