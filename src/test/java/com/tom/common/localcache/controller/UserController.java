package com.tom.common.localcache.controller;

import com.tom.common.localcache.bean.User1;
import com.tom.common.localcache.bean.User2;
import com.tom.common.localcache.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user1/{username}")
    public User1 getUser1(@PathVariable String username) {
        return userService.loadUser1(username);
    }

    @GetMapping("/user2/{username}")
    public User2 getUser2(@PathVariable String username) {
        return userService.loadUser2(username);
    }
}
