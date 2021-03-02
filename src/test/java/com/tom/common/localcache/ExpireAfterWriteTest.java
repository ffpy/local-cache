package com.tom.common.localcache;

import com.tom.common.localcache.bean.User1;
import com.tom.common.localcache.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * 测试设置写过期时间是否生效
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "local-cache.group.user1.enable=true",
        "local-cache.group.user1.expireAfterWrite=3s",
})
public class ExpireAfterWriteTest {

    @Autowired
    private UserService userService;

    @Test
    public void test() throws InterruptedException {
        User1 user1 = userService.loadUser1("user1");
        User1 user2 = userService.loadUser1("user1");
        Assertions.assertThat(user1 == user2).isTrue();
        TimeUnit.SECONDS.sleep(3);
        User1 user3 = userService.loadUser1("user1");
        Assertions.assertThat(user1 == user3).isFalse();
    }
}
