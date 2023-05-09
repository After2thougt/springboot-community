package com.bee.community.service;


import com.bee.community.entity.BBSPostCategory;
import java.util.List;

public interface BBSPostCategoryService {
    /**
     * 获取分类列表
     *
     * @return
     */
    List<BBSPostCategory> getBBSPostCategories();

    BBSPostCategory selectById(Integer categoryId);
}
