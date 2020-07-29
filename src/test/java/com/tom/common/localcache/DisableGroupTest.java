package com.tom.common.localcache;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 温龙盛
 * @date 2020/7/29 18:54
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "local-cache.group.user1.enable=false",
})
public class DisableGroupTest {

    @Autowired
    private UserService userService;

    @Test
    public void test() {
        User1 user1 = userService.loadUser1("user1");
        User1 user2 = userService.loadUser1("user1");
        Assertions.assertThat(user1).isEqualTo(user2);
        Assertions.assertThat(user1 == user2).isFalse();
    }
}
