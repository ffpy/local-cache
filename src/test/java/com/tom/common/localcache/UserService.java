package com.tom.common.localcache;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.tom.common.localcache.manager.LocalCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author 温龙盛
 * @date 2020/7/29 18:25
 */
@Service
public class UserService {

    @Autowired
    private LocalCacheManager cacheManager;

    @Cacheable("user1")
    public User1 loadUser1(String username) {
        return new User1(username);
    }

    public User2 loadUser2(String username) {
        LoadingCache<String, User2> cache = cacheManager.getLoadingCache("user2");
        return cache.get(username);
    }
}
