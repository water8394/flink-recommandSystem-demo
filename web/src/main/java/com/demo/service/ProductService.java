package com.demo.service;

import com.demo.entity.ProductEntity;

public interface ProductService {

    /**
     * 根据产品id转换为产品类
     * @param id
     * @return
     */
    public ProductEntity selectById(String id);
}
