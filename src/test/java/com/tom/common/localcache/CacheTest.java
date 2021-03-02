package com.tom.common.localcache;

import com.tom.common.localcache.bean.User1;
import com.tom.common.localcache.bean.User2;
import com.tom.common.localcache.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试缓存功能是否有效
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "local-cache.group.user1.enable=true",
        "local-cache.group.user2.cacheLoader=user2CacheLoader",
})
public class CacheTest {

    @Autowired
    private UserService userService;

    @Test
    public void loadUser1Test() {
        User1 user1_1 = userService.loadUser1("user1");
        Assertions.assertThat(user1_1).isEqualTo(new User1("user1"));
        User1 user1_2 = userService.loadUser1("user1");
        Assertions.assertThat(user1_1 == user1_2).isTrue();

        User2 user2_1 = userService.loadUser2("user2");
        Assertions.assertThat(user2_1).isEqualTo(new User2("user2"));
        User2 user2_2 = userService.loadUser2("user2");
        Assertions.assertThat(user2_1 == user2_2).isTrue();

        Assertions.assertThat(user1_1).isNotEqualTo(user2_1);
    }
}
