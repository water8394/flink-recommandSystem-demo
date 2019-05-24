package com.demo.dao;

import com.demo.entity.ProductEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDao {

    public ProductEntity selectById(int id);
}
