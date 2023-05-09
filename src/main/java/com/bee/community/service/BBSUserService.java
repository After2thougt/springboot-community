package com.bee.community.service;
import com.bee.community.entity.BBSUser;

import javax.servlet.http.HttpSession;

public interface BBSUserService {

    String register(String loginName, String password, String nickName);

    String login(String loginName, String passwordMD5, HttpSession httpSession);

    Boolean updateUserInfo(BBSUser bbsUser, HttpSession httpSession);

    Boolean updateUserHeadImg(BBSUser bbsUser, HttpSession httpSession);

    BBSUser getUserById(Long userId);

    Boolean updatePassword(Long userId, String originalPassword, String newPassword);

    String logout(HttpSession httpSession);


}
