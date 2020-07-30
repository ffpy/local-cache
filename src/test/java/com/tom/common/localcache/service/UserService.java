package com.tom.common.localcache.service;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.tom.common.localcache.bean.BigUser;
import com.tom.common.localcache.bean.User1;
import com.tom.common.localcache.bean.User2;
import com.tom.common.localcache.manager.LocalCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 测试用户服务
 *
 * @author 温龙盛
 * @date 2020/7/29 18:25
 */
@Service
public class UserService {

    @Autowired
    private LocalCacheManager cacheManager;

    private final Map<String, Integer> loadUser1Counter = new HashMap<>();

    public Map<String, Integer> getLoadUser1Counter() {
        return loadUser1Counter;
    }

    @Cacheable("user1")
    public User1 loadUser1(String username) {
        loadUser1Counter.compute(username, (key, value) -> {
            if (value == null) {
                return 1;
            }
            return value + 1;
        });
        if ("null".equals(username)) {
            return null;
        }
        return new User1(username);
    }

    public User2 loadUser2(String username) {
        LoadingCache<String, User2> cache = cacheManager.getLoadingCache("user2");
        return cache.get(username);
    }

    @Cacheable("big-user")
    public BigUser loadBigUser(String username) {
        return new BigUser(username);
    }
}
