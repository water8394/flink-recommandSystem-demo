package com.demo.dao;

import com.demo.domain.ContactEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContactDao {

    ContactDao selectById(@Param("id") int id);

    List<ContactEntity> selectByIds(@Param("ids") List<String> ids);
}
