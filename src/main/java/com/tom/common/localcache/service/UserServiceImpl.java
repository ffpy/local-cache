package com.tom.common.localcache.service;

import com.tom.common.localcache.config.LocalCacheManager;
import com.tom.common.localcache.config.LocalCacheManagerImpl;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private LocalCacheManager localCacheManager;

//    @Cacheable("user")
//    @Override
//    public String loadUser(String username) {
//        log.info("loadUser: " + username);
//        return "user: " + username;
//    }


    @Override
    public String loadUser(String username) {
        LoadingCache<String, String> cache = localCacheManager.getLoadingCache("user");
        return cache.get(username);
    }
}
