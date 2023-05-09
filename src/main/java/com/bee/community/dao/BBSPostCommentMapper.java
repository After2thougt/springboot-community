package com.bee.community.dao;

import com.bee.community.entity.BBSPostComment;
import com.bee.community.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BBSPostCommentMapper {
    int deleteByPrimaryKey(Long commentId);

    int insert(BBSPostComment record);

    int insertSelective(BBSPostComment record);

    BBSPostComment selectByPrimaryKey(Long commentId);

    int updateByPrimaryKeySelective(BBSPostComment record);

    int updateByPrimaryKey(BBSPostComment record);

    int getTotalComments(PageQueryUtil pageUtil);

    List<BBSPostComment> findCommentList(PageQueryUtil pageUtil);

    List<BBSPostComment> getRecentCommentListByUserId(@Param("userId") Long userId);
}
