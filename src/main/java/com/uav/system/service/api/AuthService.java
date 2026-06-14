package com.uav.system.service.api;

import com.uav.system.domain.entity.User;

/**
 * 用户认证服务接口
 *
 * <p>定义用户查询和认证相关操作的核心业务契约。
 * 认证核心逻辑由 Shiro Realm 处理，此接口主要负责用户数据查询。</p>
 */
public interface AuthService {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体，不存在时返回 null
     */
    User findByUsername(String username);

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 认证通过的用户实体
     * @throws com.uav.system.exception.BusinessException 用户名或密码错误时抛出
     */
    User login(String username, String password);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前登录用户
     *
     * @return 用户实体，未登录时返回 null
     * @deprecated 建议从 Shiro Subject 获取
     */
    @Deprecated
    User getCurrentUser();
}
