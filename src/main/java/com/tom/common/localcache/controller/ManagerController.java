package com.tom.common.localcache.controller;

import com.tom.common.localcache.filter.LocalCacheManagerFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 温龙盛
 * @date 2020/7/30 14:35
 */
@Controller
@RequestMapping(LocalCacheManagerFilter.PATH)
public class ManagerController {

    @GetMapping
    public String index() {
        return "local-cache/index";
    }
}
