package com.tom.common.localcache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author 温龙盛
 * @date 2020/7/29 18:28
 */
@Component
public class User2CacheLoader implements CacheLoader<String, User2> {

    @Nullable
    @Override
    public User2 load(@NonNull String username) throws Exception {
        return new User2(username);
    }
}
