package com.tom.common.localcache;

import com.tom.common.localcache.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试设置缓存值软引用能否避免OOM
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "local-cache.group.big-user.softValues=true",
})
public class SoftValuesTest {

    @Autowired
    private UserService userService;

    @Test
    public void test() {
        for (int i = 0; i < 100; i++) {
            userService.loadBigUser("user_" + i);
        }
    }
}
