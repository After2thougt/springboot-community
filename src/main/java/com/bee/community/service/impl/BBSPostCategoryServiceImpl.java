package com.bee.community.service.impl;

import com.bee.community.dao.BBSPostCategoryMapper;
import com.bee.community.entity.BBSPostCategory;
import com.bee.community.service.BBSPostCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BBSPostCategoryServiceImpl implements BBSPostCategoryService {

    @Autowired
    private BBSPostCategoryMapper bbsPostCategoryMapper;

    @Override
    public List<BBSPostCategory> getBBSPostCategories() {
        return bbsPostCategoryMapper.getBBSPostCategories();
    }

    @Override
    public BBSPostCategory selectById(Integer categoryId) {
        return bbsPostCategoryMapper.selectByPrimaryKey(categoryId);
    }
}
