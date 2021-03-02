package com.tom.common.localcache;

import com.tom.common.localcache.bean.User2;
import com.tom.common.localcache.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * 测试设置写入刷新时间是否生效
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "local-cache.group.user2.refreshAfterWrite=3s",
        "local-cache.group.user2.cacheLoader=user2CacheLoader",
})
public class RefreshAfterWriteTest {

    @Autowired
    private UserService userService;

    @Test
    public void test() throws InterruptedException {
        User2 user1 = userService.loadUser2("user1");
        User2 user2 = userService.loadUser2("user1");
        Assertions.assertThat(user1 == user2).isTrue();

        TimeUnit.SECONDS.sleep(1);

        User2 user3 = userService.loadUser2("user1");
        Assertions.assertThat(user1 == user3).isTrue();

        TimeUnit.SECONDS.sleep(2);

        User2 user4 = userService.loadUser2("user1");
        Assertions.assertThat(user1 == user4).isTrue();

        User2 user5 = userService.loadUser2("user1");
        Assertions.assertThat(user1 == user5).isFalse();
    }
}
