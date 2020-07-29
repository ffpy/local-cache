package com.tom.common.localcache.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Cacheable("user")
    @Override
    public String loadUser(String username) {
        log.info("loadUser: " + username);
        return "user: " + username;
    }
}
