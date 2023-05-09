package com.bee.community.controller.rest;

import com.bee.community.common.Constants;
import com.bee.community.common.ServiceResultEnum;
import com.bee.community.entity.BBSPost;
import com.bee.community.entity.BBSUser;
import com.bee.community.entity.RecentCommentListEntity;
import com.bee.community.service.BBSPostCollectService;
import com.bee.community.service.BBSPostCommentService;
import com.bee.community.service.BBSPostService;
import com.bee.community.service.BBSUserService;
import com.bee.community.util.MD5Util;
import com.bee.community.util.PatternUtil;
import com.bee.community.util.Result;
import com.bee.community.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
public class BBSUserController {

    @Resource
    private BBSUserService bbsUserService;

    @Resource
    private BBSPostService bbsPostService;

    @Resource
    private BBSPostCollectService bbsPostCollectService;

    @Resource
    private BBSPostCommentService bbsPostCommentService;

    @GetMapping({"/login", "/login.html"})
    public String loginPage() {
        return "user/login";
    }

    @GetMapping({"/register", "/register.html"})
    public String registerPage() {
        return "user/reg";
    }

    @GetMapping({"/forget", "/forget.html"})
    public String forgetPage() {
        return "user/forget";
    }

    @GetMapping({"/about", "/about.html"})
    public String contactPage() {
        return "other/about";
    }

    /**
     * 用户中心
     * @param request
     * @param userId
     * @return
     */
    @GetMapping("/userCenter/{userId}")
    public String userCenterPage(HttpServletRequest request, @PathVariable("userId") Long userId) {
        //基本用户信息
        BBSUser bbsUser = bbsUserService.getUserById(userId);
        if (bbsUser == null) {
            return "error/error_404";
        }

        // 最近发布
        List<BBSPost> recentPostList = bbsPostService.getRecentPostListByUserId(userId);

        // 最近评论
        List<RecentCommentListEntity> recentCommentList = bbsPostCommentService.getRecentCommentListByUserId(userId);

        request.setAttribute("bbsUser", bbsUser);
        request.setAttribute("recentPostList", recentPostList);
        request.setAttribute("recentCommentList", recentCommentList);
        return "user/home";
    }

    @GetMapping("/myCenter")
    public String myCenterPage(HttpServletRequest request) {

        // 用户信息
        BBSUser currentUser = (BBSUser) request.getSession().getAttribute(Constants.USER_SESSION_KEY);

        // 我发的贴
        List<BBSPost> myBBSPostList = bbsPostService.getMyBBSPostList(currentUser.getUserId());
        int myBBSPostCount = 0;
        if (!CollectionUtils.isEmpty(myBBSPostList)) {
            myBBSPostCount = myBBSPostList.size();
        }

        // 我赞过的贴
        List<BBSPost> collectRecords = bbsPostCollectService.getCollectRecordsByUserId(currentUser.getUserId());
        int myCollectBBSPostCount = 0;
        if (!CollectionUtils.isEmpty(collectRecords)) {
            myCollectBBSPostCount = collectRecords.size();
        }

        request.setAttribute("myBBSPostList", myBBSPostList);
        request.setAttribute("myBBSPostCount", myBBSPostCount);
        request.setAttribute("collectRecords", collectRecords);
        request.setAttribute("myCollectBBSPostCount", myCollectBBSPostCount);
        request.setAttribute("bbsUser", currentUser);
        return "user/index";
    }


    @GetMapping("/userSet")
    public String userSetPage(HttpServletRequest request) {
        BBSUser currentUser = (BBSUser) request.getSession().getAttribute(Constants.USER_SESSION_KEY);
        request.setAttribute("bbsUser", currentUser);
        return "user/set";
    }

    /**
     * 修改个人信息
     * @param nickName 用户名
     * @param userGender 性别
     * @param location 所在地
     * @param introduce 个人介绍
     * @param httpSession
     * @return
     */
    @PostMapping("/updateUserInfo")
    @ResponseBody
    public Result updateInfo(@RequestParam("nickName") String nickName,
                             @RequestParam("userGender") int userGender,
                             @RequestParam("location") String location,
                             @RequestParam("introduce") String introduce,
                             HttpSession httpSession) {

        if (!StringUtils.hasLength(nickName)) {
            return ResultGenerator.genFailResult("nickName参数错误");
        }
        if (userGender < 1 || userGender > 3) {
            return ResultGenerator.genFailResult("userGender参数错误");
        }
        if (!StringUtils.hasLength(location)) {
            return ResultGenerator.genFailResult("location参数错误");
        }
        if (!StringUtils.hasLength(introduce)) {
            return ResultGenerator.genFailResult("introduce参数错误");
        }
        BBSUser bbsUser = (BBSUser) httpSession.getAttribute(Constants.USER_SESSION_KEY);
        bbsUser.setNickName(nickName);
        if (userGender == 1) {
            bbsUser.setGender("男");
        }
        if (userGender == 2) {
            bbsUser.setGender("女");
        }
        if (userGender == 3) {
            bbsUser.setGender("其他");
        }
        bbsUser.setLocation(location);
        bbsUser.setIntroduce(introduce);
        if (bbsUserService.updateUserInfo(bbsUser, httpSession)) {
            Result result = ResultGenerator.genSuccessResult();
            return result;
        } else {
            Result result = ResultGenerator.genFailResult("修改失败");
            return result;
        }
    }

    /**
     * 修改头像
     * @param userHeadImg
     * @param httpSession
     * @return
     */
    @PostMapping("/updateHeadImg")
    @ResponseBody
    public Result updateHeadImg(@RequestParam("userHeadImg") String userHeadImg,
                                HttpSession httpSession) {

        if (!StringUtils.hasLength(userHeadImg)) {
            return ResultGenerator.genFailResult("userHeadImg参数错误");
        }
        BBSUser bbsUser = (BBSUser) httpSession.getAttribute(Constants.USER_SESSION_KEY);
        bbsUser.setHeadImgUrl(userHeadImg);
        if (bbsUserService.updateUserHeadImg(bbsUser, httpSession)) {
            Result result = ResultGenerator.genSuccessResult();
            return result;
        } else {
            Result result = ResultGenerator.genFailResult("修改失败");
            return result;
        }
    }

    /**
     * 修改密码
     * @param request
     * @param originalPassword 原密码
     * @param newPassword 新密码
     * @return
     */
    @PostMapping("/updatePassword")
    @ResponseBody
    public Result passwordUpdate(HttpServletRequest request, @RequestParam("originalPassword") String originalPassword,
                                 @RequestParam("newPassword") String newPassword) {
        if (!StringUtils.hasLength(originalPassword) || !StringUtils.hasLength(newPassword)) {
            return ResultGenerator.genFailResult("参数不能为空");
        }
        BBSUser currentUser = (BBSUser) request.getSession().getAttribute(Constants.USER_SESSION_KEY);
        if (bbsUserService.updatePassword(currentUser.getUserId(), originalPassword, newPassword)) {
            // 修改成功后清空session中的数据，前端控制跳转至登录页
            request.getSession().removeAttribute(Constants.USER_SESSION_KEY);
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

    /**
     * 注册
     * @param loginName
     * @param nickName
     * @param password
     * @param httpSession
     * @return
     */
    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestParam("loginName") String loginName,
                           @RequestParam("nickName") String nickName,
                           @RequestParam("password") String password,
                           HttpSession httpSession) {
        if (!StringUtils.hasLength(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (!PatternUtil.isEmail(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NOT_EMAIL.getResult());
        }
        if (!StringUtils.hasLength(password)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }

        String registerResult = bbsUserService.register(loginName, password, nickName);
        //注册成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(registerResult)) {
            httpSession.removeAttribute(Constants.VERIFY_CODE_KEY);//删除session中的验证码
            return ResultGenerator.genSuccessResult();
        }
        //注册失败
        return ResultGenerator.genFailResult(registerResult);
    }

    /**
     * 登录
     * @param loginName
     * @param verifyCode
     * @param password
     * @param httpSession
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public Result login(@RequestParam("loginName") String loginName,
                        @RequestParam("verifyCode") String verifyCode,
                        @RequestParam("password") String password,
                        HttpSession httpSession) {
        if (!StringUtils.hasLength(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (!PatternUtil.isEmail(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NOT_EMAIL.getResult());
        }
        if (!StringUtils.hasLength(password)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if (!StringUtils.hasLength(verifyCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }

        // 验证码
        String kaptchaCode = httpSession.getAttribute(Constants.VERIFY_CODE_KEY) + "";

        if (!StringUtils.hasLength(kaptchaCode) || !verifyCode.equals(kaptchaCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        String loginResult = bbsUserService.login(loginName, MD5Util.MD5Encode(password, "UTF-8"), httpSession);
        //登录成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(loginResult)) {
            httpSession.removeAttribute(Constants.VERIFY_CODE_KEY);//删除session中的验证码
            return ResultGenerator.genSuccessResult();
        }
        //登录失败
        return ResultGenerator.genFailResult(loginResult);
    }

    /**
     * 退出登录
     * @param httpSession
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        bbsUserService.logout(httpSession);
        return "redirect:/index";
    }
}
