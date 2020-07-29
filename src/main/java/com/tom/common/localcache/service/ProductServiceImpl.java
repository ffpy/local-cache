package com.tom.common.localcache.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Cacheable("product")
    @Override
    public String getProduct(Long id) {
        log.info("getProduct: " + id);
        return "product: " + id;
    }
}
