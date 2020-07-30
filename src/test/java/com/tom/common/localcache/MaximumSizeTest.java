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
 * 测试设置最大缓存数是否生效
 *
 * @author 温龙盛
 * @date 2020-07-30 10:11
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "local-cache.group.user1.maximumSize=2",
})
public class MaximumSizeTest {

    @Autowired
    private UserService userService;

    @Test
    public void test() throws InterruptedException {
        User1 user1 = userService.loadUser1("user1");
        User1 user2 = userService.loadUser1("user1");
        Assertions.assertThat(user1 == user2).isTrue();

        userService.loadUser1("user2");
        userService.loadUser1("user2");

        TimeUnit.SECONDS.sleep(1);
        User1 user3 = userService.loadUser1("user1");
        Assertions.assertThat(user1 == user3).isTrue();

        userService.loadUser1("user3");
        userService.loadUser1("user3");

        TimeUnit.SECONDS.sleep(1);
        User1 user4 = userService.loadUser1("user1");
        Assertions.assertThat(user1 == user4).isTrue();
    }
}
