package com.demo.dao;

import com.demo.entity.ProductEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public interface ProductDao {

    public ProductEntity selectById(int id);
}
