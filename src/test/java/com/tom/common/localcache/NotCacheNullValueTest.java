package com.tom.common.localcache;

import com.tom.common.localcache.service.User2CacheLoader;
import com.tom.common.localcache.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试设置缓存空值是否生效
 *
 * @author 温龙盛
 * @date 2020/7/30 10:10
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "local-cache.group.user1.enable=true",
        "local-cache.group.user1.cache-null-value=false",
        "local-cache.group.user2.enable=true",
        "local-cache.group.user2.cache-loader=user2CacheLoader",
        "local-cache.group.user2.cache-null-value=false",
})
public class NotCacheNullValueTest {

    @Autowired
    private UserService userService;

    @Autowired
    private User2CacheLoader user2CacheLoader;

    @Test
    public void cacheTest() {
        String key = "null";
        userService.loadUser1(key);
        Assertions.assertThat(userService.getLoadUser1Counter().get(key)).isEqualTo(1);
        userService.loadUser1(key);
        Assertions.assertThat(userService.getLoadUser1Counter().get(key)).isEqualTo(2);
    }

    @Test
    public void loadingCacheTest() {
        String key = "null";
        userService.loadUser2(key);
        Assertions.assertThat(user2CacheLoader.getLoadCounter().get(key)).isEqualTo(1);
        userService.loadUser2(key);
        Assertions.assertThat(user2CacheLoader.getLoadCounter().get(key)).isEqualTo(2);
    }
}
