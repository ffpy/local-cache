package com.example.localcache;

import com.example.localcache.service.ProductService;
import com.example.localcache.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LocalCacheApplication implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    public static void main(String[] args) {
        SpringApplication.run(LocalCacheApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        userService.loadUser("user1");
        userService.loadUser("user1");
        userService.loadUser("user2");
        userService.loadUser("user2");

        productService.getProduct(1L);
        productService.getProduct(1L);
        productService.getProduct(2L);
        productService.getProduct(2L);
    }
}
