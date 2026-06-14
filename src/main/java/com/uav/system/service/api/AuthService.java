package com.uav.system.service.api;

import com.uav.system.domain.entity.User;

/**
 * 用户认证服务接口
 *
 * <p>定义登录、登出和获取当前用户等认证相关操作的核心业务契约。</p>
 *
 * <p><b>实现类：</b>{@link com.uav.system.service.impl.AuthServiceImpl}</p>
 *
 * @see com.uav.system.domain.entity.User
 */
public interface AuthService {

    /**
     * 用户登录
     * <p>根据用户名和密码进行身份验证。</p>
     * <p><b>注意：</b>当前采用明文密码比对，生产环境应升级为 BCrypt 加密存储。</p>
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 认证通过的用户实体 {@link User}
     * @throws com.uav.system.exception.BusinessException 用户名或密码错误时抛出
     */
    User login(String username, String password);

    /**
     * 用户登出
     * <p>JWT 无状态认证模式下，服务端无需保存会话状态，此方法当前为空实现。
     * 实际登出由前端清除 localStorage 中的 Token 完成。</p>
     */
    void logout();

    /**
     * 获取当前登录用户
     * <p>注：此方法在当前实现中已不推荐使用，用户信息应由
     * {@link com.uav.system.filter.JwtAuthenticationFilter} 从 Token 中解析。</p>
     *
     * @return 用户实体，未登录时返回 null
     * @deprecated 建议改用从 JWT Token 中直接获取用户信息的方式
     */
    User getCurrentUser();
}
