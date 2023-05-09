package com.bee.community.service.impl;

import com.bee.community.dao.BBSPostCategoryMapper;
import com.bee.community.dao.BBSPostMapper;
import com.bee.community.dao.BBSUserMapper;
import com.bee.community.entity.*;
import com.bee.community.service.BBSPostService;
import com.bee.community.util.BeanUtil;
import com.bee.community.util.PageQueryUtil;
import com.bee.community.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BBSPostServiceImpl implements BBSPostService {

    @Autowired
    private BBSPostCategoryMapper bbsPostCategoryMapper;
    @Autowired
    private BBSPostMapper bbsPostMapper;
    @Autowired
    private BBSUserMapper bbsUserMapper;

    @Override
    public int savePost(BBSPost bbsPost) {
        BBSUser bbsUser = bbsUserMapper.selectByPrimaryKey(bbsPost.getPublishUserId());
        if (bbsUser == null || bbsUser.getUserStatus().intValue() == 1) {
            //账号已被封禁
            return 0;
        }

        BBSPostCategory bbsPostCategory = bbsPostCategoryMapper.selectByPrimaryKey(bbsPost.getPostCategoryId());
        if (bbsPostCategory == null) {
            //分类数据错误
            return 0;
        }
        return bbsPostMapper.insertSelective(bbsPost);
    }

    @Override
    public BBSPost getBBSPostById(Long bbsPostId) {
        return bbsPostMapper.selectByPrimaryKey(bbsPostId);
    }

    @Override
    public BBSPost getBBSPostForDetail(Long bbsPostId) {
        BBSPost bbsPost = bbsPostMapper.selectByPrimaryKey(bbsPostId);
        if (bbsPost != null) {
            bbsPost.setPostViews(bbsPost.getPostViews() + 1);
            bbsPostMapper.updateByPrimaryKeySelective(bbsPost);
        }
        return bbsPost;
    }

    @Override
    public int updateBBSPost(BBSPost bbsPost) {
        BBSUser bbsUser = bbsUserMapper.selectByPrimaryKey(bbsPost.getPublishUserId());

        if (bbsUser == null || bbsUser.getUserStatus().intValue() == 1) {
            //账号已被封禁
            return 0;
        }

        BBSPostCategory bbsPostCategory = bbsPostCategoryMapper.selectByPrimaryKey(bbsPost.getPostCategoryId());
        if (bbsPostCategory == null) {
            //分类数据错误
            return 0;
        }

        return bbsPostMapper.updateByPrimaryKeySelective(bbsPost);
    }

    @Override
    public int delBBSPost(Long userId, Long postId) {
        BBSUser bbsUser = bbsUserMapper.selectByPrimaryKey(userId);

        if (bbsUser == null || bbsUser.getUserStatus().intValue() == 1) {
            //账号已被封禁
            return 0;
        }

        BBSPost bbsPost = bbsPostMapper.selectByPrimaryKey(postId);
        // 对象不为空且发帖人为当前登录用户才可以删除
        if (bbsPost != null && bbsPost.getPublishUserId().equals(userId)) {
            return bbsPostMapper.deleteByPrimaryKey(postId);
        }
        return 0;
    }

    /**
     * 首页展示
     * @param pageUtil
     * @return
     */
    @Override
    public PageResult getBBSPostPageForIndex(PageQueryUtil pageUtil) {
        //查询帖子数据
        int total = bbsPostMapper.getTotalBBSPosts(pageUtil);
        List<BBSPost> bbsPostList = bbsPostMapper.findBBSPostList(pageUtil);
        List<BBSPostListEntity> bbsPostListEntities = new ArrayList<>();
        //数据格式转换
        if (!CollectionUtils.isEmpty(bbsPostList)) {
            bbsPostListEntities = BeanUtil.copyList(bbsPostList, BBSPostListEntity.class);
            List<Long> userIds = bbsPostListEntities.stream().map(BBSPostListEntity::getPublishUserId).collect(Collectors.toList());
            //查询user数据
            List<BBSUser> bbsUsers = bbsUserMapper.selectByPrimaryKeys(userIds);
            if (!CollectionUtils.isEmpty(bbsUsers)) {
                //封装user数据
                Map<Long, BBSUser> bbsUserMap = bbsUsers.stream().collect(Collectors.toMap(BBSUser::getUserId, Function.identity(), (entity1, entity2) -> entity1));
                for (BBSPostListEntity bbsPostListEntity : bbsPostListEntities) {
                    if (bbsUserMap.containsKey(bbsPostListEntity.getPublishUserId())) {
                        //设置头像字段和用户名字段
                        BBSUser tempUser = bbsUserMap.get(bbsPostListEntity.getPublishUserId());
                        bbsPostListEntity.setHeadImgUrl(tempUser.getHeadImgUrl());
                        bbsPostListEntity.setNickName(tempUser.getNickName());
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(bbsPostListEntities, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public List getHotTopicBBSPostList() {
        List<BBSPost> bbsPostList = bbsPostMapper.getHotTopicBBSPostList();
        List<HotTopicBBSPostListEntity> hotTopicBBSPostList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bbsPostList)) {
            hotTopicBBSPostList = BeanUtil.copyList(bbsPostList, HotTopicBBSPostListEntity.class);
        }
        return hotTopicBBSPostList;
    }

    @Override
    public List getRecentBBSPostList() {
        List<BBSPost> bbsPostList = bbsPostMapper.getRecentBBSPostList();
        List<HotTopicBBSPostListEntity> recentBBSPostList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bbsPostList)) {
            recentBBSPostList = BeanUtil.copyList(bbsPostList, HotTopicBBSPostListEntity.class);
        }
        return recentBBSPostList;
    }

    @Override
    public List<BBSPost> getMyBBSPostList(Long userId) {
        return bbsPostMapper.getMyBBSPostList(userId, "all");
    }

    @Override
    public List<BBSPost> getRecentPostListByUserId(Long userId) {
        return bbsPostMapper.getMyBBSPostList(userId, "recent");
    }
}
