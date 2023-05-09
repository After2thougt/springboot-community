package com.bee.community.entity;


/**
 * 验证码重置密码参数VO
 *
 */

public class Mail {

    /**
     * 用户账号
     */
    private String mailAddress;

    /**
     * 邮箱验证码
     */
    private String code;

    /**
     * 新密码
     */
    private String password;

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "CodeUpdatePwdVo{" +
                "mailAddress='" + mailAddress + '\'' +
                ", code='" + code + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
