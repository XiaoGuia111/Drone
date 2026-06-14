package com.uav.system.domain.vo;

import com.uav.system.domain.entity.User;

/**
 * 登录响应结果
 *
 * <p>登录成功后返回用户信息和 Shiro 会话 ID。</p>
 */
public class LoginResponse {
    /** 当前登录用户信息 */
    private User user;
    /** Shiro 会话 ID */
    private String sessionId;

    public LoginResponse() {
    }

    public LoginResponse(User user, String sessionId) {
        this.user = user;
        this.sessionId = sessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
