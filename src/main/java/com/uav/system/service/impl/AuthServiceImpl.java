package com.uav.system.service.impl;

import com.uav.system.domain.entity.User;
import com.uav.system.exception.BusinessException;
import com.uav.system.repository.mapper.UserMapper;
import com.uav.system.service.api.AuthService;
import org.springframework.stereotype.Service;

/**
 * 用户认证服务实现
 *
 * <p>提供用户查询功能，认证逻辑由 Shiro Realm 处理。</p>
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;

    public AuthServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User login(String username, String password) {
        // 认证由 Shiro Realm 处理，此方法保留以供其他场景使用
        User user = userMapper.findByUsername(username);
        if (user == null || !password.equals(user.getPassword())) {
            throw new BusinessException("Invalid username or password");
        }
        return user;
    }

    @Override
    public void logout() {
        // Shiro 会话管理，无需额外逻辑
    }

    @Override
    public User getCurrentUser() {
        return null;
    }
}
