package com.tom.common.localcache.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.tom.common.localcache.action.RefreshByUpdateTimeAction;
import com.tom.common.localcache.bean.User1;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class User1RefreshByUpdateTimeAction implements RefreshByUpdateTimeAction<String, User1> {

    @Override
    public Map<String, User1> load(Date timeBound, Cache<String, User1> cache) {
        HashMap<String, User1> map = new HashMap<>();
        map.put("reload_user1", new User1("reload_user1"));
        map.put("reload_user2", null);
        return map;
    }
}
