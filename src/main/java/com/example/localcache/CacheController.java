package com.example.localcache;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping("/cache-manager")
public class CacheController {

    @GetMapping
    public String index() {
        return "";
    }
}
