package com.uav.system.domain.vo;

import com.uav.system.domain.entity.User;

//登录响应结果
public class LoginResponse {
    /** 当前登录用户信息 */
    private User user;
    /** JWT Token 字符串（Bearer 模式） */
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}