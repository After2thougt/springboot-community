package com.bee.community.controller.rest;

import com.bee.community.common.Constants;
import com.bee.community.common.ServiceResultEnum;
import com.bee.community.entity.BBSPostComment;
import com.bee.community.entity.BBSUser;
import com.bee.community.service.BBSPostCommentService;
import com.bee.community.util.Result;
import com.bee.community.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 评论功能
 */
@Controller
public class BBSPostCommentController {

    @Resource
    private BBSPostCommentService bbsPostCommentService;

    /**
     * 评论
     * @param postId
     * @param parentCommentUserId
     * @param commentBody
     * @param verifyCode
     * @param httpSession
     * @return
     */
    @PostMapping("/replyPost")
    @ResponseBody
    public Result replyPost(@RequestParam("postId") Long postId,
                            @RequestParam(value = "parentCommentUserId", required = false) Long parentCommentUserId,
                            @RequestParam("commentBody") String commentBody,
                            @RequestParam("verifyCode") String verifyCode,
                            HttpSession httpSession) {

        if (null == postId || postId < 0) {
            return ResultGenerator.genFailResult("postId参数错误");
        }
        if (!StringUtils.hasLength(commentBody)) {
            return ResultGenerator.genFailResult("commentBody参数错误");
        }
        if (commentBody.trim().length() > 200) {
            return ResultGenerator.genFailResult("评论内容过长");
        }
        String kaptchaCode = httpSession.getAttribute(Constants.VERIFY_CODE_KEY) + "";
        if (!StringUtils.hasLength(kaptchaCode) || !verifyCode.equals(kaptchaCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        BBSUser bbsUser = (BBSUser) httpSession.getAttribute(Constants.USER_SESSION_KEY);

        BBSPostComment bbsPostComment = new BBSPostComment();
        bbsPostComment.setCommentBody(commentBody);
        bbsPostComment.setCommentUserId(bbsUser.getUserId());
        bbsPostComment.setParentCommentUserId(parentCommentUserId);
        bbsPostComment.setPostId(postId);

        if (bbsPostCommentService.addPostComment(bbsPostComment)) {
            httpSession.removeAttribute(Constants.VERIFY_CODE_KEY);
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("请求失败，请检查参数及账号是否有操作权限");
        }
    }


    @PostMapping("/delReply/{commentId}")
    @ResponseBody
    public Result delReply(@PathVariable("commentId") Long commentId,
                           HttpSession httpSession) {

        if (null == commentId || commentId < 0) {
            return ResultGenerator.genFailResult("commentId参数错误");
        }

        BBSUser bbsUser = (BBSUser) httpSession.getAttribute(Constants.USER_SESSION_KEY);

        if (bbsPostCommentService.delPostComment(commentId,bbsUser.getUserId())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("请求失败，请检查参数及账号是否有操作权限");
        }
    }
}
