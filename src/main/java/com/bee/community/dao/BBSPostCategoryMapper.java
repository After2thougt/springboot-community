package com.bee.community.dao;

import com.bee.community.entity.BBSPostCategory;

import java.util.List;

public interface BBSPostCategoryMapper {
    int deleteByPrimaryKey(Integer categoryId);

    int insert(BBSPostCategory record);

    int insertSelective(BBSPostCategory record);

    BBSPostCategory selectByPrimaryKey(Integer categoryId);

    int updateByPrimaryKeySelective(BBSPostCategory record);

    int updateByPrimaryKey(BBSPostCategory record);

    List<BBSPostCategory> getBBSPostCategories();
}