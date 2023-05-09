package com.bee.community.service.impl;


import com.bee.community.common.Constants;
import com.bee.community.common.ServiceResultEnum;
import com.bee.community.dao.BBSUserMapper;
import com.bee.community.entity.BBSUser;
import com.bee.community.service.BBSUserService;
import com.bee.community.util.MD5Util;
import com.bee.community.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;

@Service
public class BBSUserServiceImpl implements BBSUserService {

    @Autowired
    private BBSUserMapper bbsUserMapper;

    /**
     * 注册用户
     * @param loginName
     * @param password
     * @param nickName
     * @return
     */
    @Override
    public String register(String loginName, String password, String nickName) {
        if (bbsUserMapper.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        //注册用户
        BBSUser registerUser = new BBSUser();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(nickName);
        //默认头像
        registerUser.setHeadImgUrl("/images/avatar/default.png");
        //默认介绍
        registerUser.setIntroduce(" ");
        //居住地
        registerUser.setLocation("未知");
        registerUser.setGender("未知");
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        registerUser.setPasswordMd5(passwordMD5);
        if (bbsUserMapper.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    /**
     * 登录用户
     * @param loginName
     * @param passwordMD5
     * @param httpSession
     * @return
     */
    @Override
    public String login(String loginName, String passwordMD5, HttpSession httpSession) {
        BBSUser user = bbsUserMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
        if (user != null && httpSession != null) {
            httpSession.setAttribute(Constants.USER_SESSION_KEY, user);
            //修改最近登录时间
            user.setLastLoginTime(new Date());
            bbsUserMapper.updateByPrimaryKeySelective(user);
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }


    /**
     * 修改个人资料
     * @param bbsUser
     * @param httpSession
     * @return
     */
    @Override
    public Boolean updateUserInfo(BBSUser bbsUser, HttpSession httpSession) {
        BBSUser userTemp = (BBSUser) httpSession.getAttribute(Constants.USER_SESSION_KEY);
        BBSUser userFromDB = bbsUserMapper.selectByPrimaryKey(userTemp.getUserId());
        //当前用户非空且状态正常才可以进行更改
        if (userFromDB != null && userFromDB.getUserStatus().intValue() == 0) {
            userFromDB.setIntroduce(SystemUtil.cleanString(bbsUser.getIntroduce()));
            userFromDB.setLocation(SystemUtil.cleanString(bbsUser.getLocation()));
            userFromDB.setGender(SystemUtil.cleanString(bbsUser.getGender()));
            userFromDB.setNickName(SystemUtil.cleanString(bbsUser.getNickName()));
            if (bbsUserMapper.updateByPrimaryKeySelective(userFromDB) > 0) {
                httpSession.setAttribute(Constants.USER_SESSION_KEY, userFromDB);
                return true;
            }
        }
        return false;
    }

    /**
     * 头像修改
     * @param bbsUser
     * @param httpSession
     * @return
     */
    @Override
    public Boolean updateUserHeadImg(BBSUser bbsUser, HttpSession httpSession) {
        BBSUser userFromDB = bbsUserMapper.selectByPrimaryKey(bbsUser.getUserId());
        //当前用户非空且状态正常才可以进行更改
        if (userFromDB != null && userFromDB.getUserStatus().intValue() == 0) {
            userFromDB.setHeadImgUrl(bbsUser.getHeadImgUrl());
            if (bbsUserMapper.updateByPrimaryKeySelective(userFromDB) > 0) {
                httpSession.setAttribute(Constants.USER_SESSION_KEY, userFromDB);
                return true;
            }
        }
        return false;
    }

    @Override
    public BBSUser getUserById(Long userId) {
        return bbsUserMapper.selectByPrimaryKey(userId);
    }

    @Override
    public Boolean updatePassword(Long userId, String originalPassword, String newPassword) {
        BBSUser bbsUser = bbsUserMapper.selectByPrimaryKey(userId);
        //当前用户非空且状态正常才可以进行更改
        if (bbsUser != null && bbsUser.getUserStatus().intValue() == 0) {
            String originalPasswordMd5 = MD5Util.MD5Encode(originalPassword, "UTF-8");
            String newPasswordMd5 = MD5Util.MD5Encode(newPassword, "UTF-8");
            //比较原密码是否正确
            if (originalPasswordMd5.equals(bbsUser.getPasswordMd5())) {
                //设置新密码并修改
                bbsUser.setPasswordMd5(newPasswordMd5);
                if (bbsUserMapper.updateByPrimaryKeySelective(bbsUser) > 0) {
                    //修改成功则返回true
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute(Constants.USER_SESSION_KEY);
        return null;
    }


}