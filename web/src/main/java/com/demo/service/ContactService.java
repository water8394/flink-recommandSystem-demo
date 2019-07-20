package com.demo.service;

import com.demo.domain.ContactEntity;

import java.util.List;

public interface ContactService {

    /**
     * 根据id列表筛选产品
     * @param ids
     * @return
     */
    List<ContactEntity> selectByIds(List<String> ids);

    /**
     * 根据id 筛选产品
     * @param id
     * @return
     */
    ContactEntity selectById(String id);
}
