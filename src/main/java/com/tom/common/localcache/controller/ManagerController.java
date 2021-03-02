package com.tom.common.localcache.controller;

import com.tom.common.localcache.constant.PathConstant;
import com.tom.common.localcache.properties.LocalCacheManagerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("${local-cache.manager.path:" + PathConstant.DEFAULT_PATH + "}")
public class ManagerController {

    @Autowired
    private LocalCacheManagerProperties localCacheManagerProperties;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("path", contextPath + localCacheManagerProperties.getPath());
        model.addAttribute("name", localCacheManagerProperties.getName());
        return "local-cache/index";
    }
}
