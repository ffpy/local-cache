package com.tom.common.localcache.service;

import com.tom.common.localcache.ReloadAction;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 温龙盛
 * @date 2020/7/31 13:23
 */
@Component
public class User1ReloadAction implements ReloadAction<String, String> {

    @Override
    public Map<String, String> reload() {
        Map<String, String> map = new HashMap<>();
        map.put("reload_user1", "abc");
        map.put("reload_user2", "def");
        map.put("reload_user3", "123");
        return map;
    }
}
