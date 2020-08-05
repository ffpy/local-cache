package com.tom.common.localcache.service;

import com.tom.common.localcache.action.RefreshByUpdateTimeAction;
import com.tom.common.localcache.bean.User1;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class User1RefreshByUpdateTimeAction implements RefreshByUpdateTimeAction<String, User1> {

    @Override
    public Map<String, Value<User1>> load(Date timeBound) {
        System.out.println("timeBound: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timeBound));
        HashMap<String, Value<User1>> map = new HashMap<>();
        map.put("reload_user1", new Value<>(new User1("reload_user1"), true));
        map.put("reload_user2", new Value<>(new User1("reload_user2"), false));
        return map;
    }
}
