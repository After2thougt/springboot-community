package com.bee.community.controller.rest;

import com.bee.community.common.Constants;
import com.bee.community.entity.BBSUser;
import com.bee.community.service.BBSPostCollectService;
import com.bee.community.util.Result;
import com.bee.community.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
public class BBSPostCollectController {

    @Resource
    private BBSPostCollectService bbsPostCollectService;

    @PostMapping("/addCollect/{postId}")
    @ResponseBody
    public Result addCollect(@PathVariable("postId") Long postId,
                             HttpSession httpSession) {
        if (null == postId || postId < 0) {
            return ResultGenerator.genFailResult("postId参数错误");
        }
        BBSUser bbsUser = (BBSUser) httpSession.getAttribute(Constants.USER_SESSION_KEY);
        if (bbsPostCollectService.addCollectRecord(bbsUser.getUserId(), postId)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("请求失败，请检查参数及账号是否有操作权限");
        }
    }

    @PostMapping("/delCollect/{postId}")
    @ResponseBody
    public Result delCollect(@PathVariable("postId") Long postId,
                             HttpSession httpSession) {
        if (null == postId || postId < 0) {
            return ResultGenerator.genFailResult("postId参数错误");
        }
        BBSUser bbsUser = (BBSUser) httpSession.getAttribute(Constants.USER_SESSION_KEY);
        if (bbsPostCollectService.deleteCollectRecord(bbsUser.getUserId(), postId)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("请求失败，请检查参数及账号是否有操作权限");
        }
    }
}
