package com.tom.common.localcache;

import com.tom.common.localcache.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试不设置缓存值软引用是否会引发OOM
 *
 * @author 温龙盛
 * @date 2020-07-30 10:11
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "local-cache.group.big-user.enable=true",
})
public class SoftValuesOOMTest {

    @Autowired
    private UserService userService;

    @Test(expected = OutOfMemoryError.class)
    public void test() {
        for (int i = 0; i < 100; i++) {
            userService.loadBigUser("user_" + i);
        }
    }
}
