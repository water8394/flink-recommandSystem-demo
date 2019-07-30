package com.demo.service;

import com.demo.domain.ProductEntity;

import java.util.List;

public interface ProductService {

    /**
     * 根据产品id转换为产品类
     * @param id
     * @return
     */
    ProductEntity selectById(String id);

    /**
     * 根据id列表筛选产品
     * @param ids
     * @return
     */
    List<ProductEntity> selectByIds(List<String> ids);

	List<String> selectInitPro(int topSize);
}
