package com.tom.common.localcache.service;

import com.tom.common.localcache.action.ReloadAction;
import com.tom.common.localcache.bean.User1;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class User1ReloadAction implements ReloadAction<String, User1> {

    @Override
    public Map<String, User1> reload() {
        Map<String, User1> map = new HashMap<>();
        for (int i = 1; i <= 28; i++) {
            map.put("reload_user" + i, new User1("user" + i));
        }
        return map;
    }
}
