package com.demo.dao;

import com.demo.domain.ProductEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDao {

    ProductEntity selectById(@Param("id") int id);

    List<ProductEntity> selectByIds(@Param("ids") List<String> ids);
}
