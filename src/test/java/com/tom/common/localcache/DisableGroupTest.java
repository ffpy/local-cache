package com.tom.common.localcache;

import com.tom.common.localcache.bean.User1;
import com.tom.common.localcache.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试禁用缓存功能是否有效
 *
 * @author 温龙盛
 * @date 2020/7/29 18:54
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "local-cache.group.user1.debug=true",
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
