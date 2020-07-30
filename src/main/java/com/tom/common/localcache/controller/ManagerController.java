package com.tom.common.localcache.controller;

import com.tom.common.localcache.constant.PathConstant;
import com.tom.common.localcache.properties.LocalCacheManagerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 温龙盛
 * @date 2020/7/30 14:35
 */
@Controller
@RequestMapping("${local-cache.manager.path:" + PathConstant.DEFAULT_PATH + "}")
public class ManagerController {

    @Autowired
    private LocalCacheManagerProperties localCacheManagerProperties;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("path", localCacheManagerProperties.getPath());
        return "local-cache/index";
    }
}
