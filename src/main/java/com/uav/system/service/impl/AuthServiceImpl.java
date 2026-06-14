package com.uav.system.service.impl;

import com.uav.system.domain.entity.User;
import com.uav.system.exception.BusinessException;
import com.uav.system.repository.mapper.UserMapper;
import com.uav.system.service.api.AuthService;
import org.springframework.stereotype.Service;

/**
 * 用户认证服务实现
 *
 * <p>提供登录认证的核心逻辑。当前采用明文密码比对方式，
 * <b>生产环境必须升级为 BCrypt + Salt 加密存储。</b></p>
 *
 * <p><b>安全提示：</b>为防御暴力枚举攻击，建议后续增加登录失败次数限制和验证码机制。</p>
 *
 * @see com.uav.system.repository.mapper.UserMapper
 * @see com.uav.system.domain.entity.User
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;

    public AuthServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 用户登录认证
     *
     * <p>执行步骤：</p>
     * <ol>
     *   <li>根据用户名查询用户（{@link UserMapper#findByUsername}）</li>
     *   <li>如果用户不存在，抛出 {@link BusinessException}（模糊提示"用户名或密码错误"，防止用户名枚举）</li>
     *   <li>比对明文密码，不一致则抛出相同异常</li>
     *   <li>认证成功返回完整用户对象</li>
     * </ol>
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 认证通过的用户实体
     * @throws BusinessException 认证失败时抛出，消息不区分具体原因（防用户名枚举）
     */
    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException("Invalid username or password");
        }
        // TODO: 替换为 BCryptPasswordEncoder.matches() 进行加密比对
        if (!password.equals(user.getPassword())) {
            throw new BusinessException("Invalid username or password");
        }
        return user;
    }

    /**
     * 登出操作
     * <p>JWT 为无状态认证协议，服务端无需维护会话状态（无 HttpSession）。
     * 登除逻辑由前端清除 localStorage 中的 Token 完成。</p>
     */
    @Override
    public void logout() {
        // JWT is stateless: no server-side session to clear
    }

    /**
     * 获取当前登录用户信息
     * <p><b>已弃用：</b>此方法已不再推荐使用。当前返回 null，
     * 用户信息应通过 {@link com.uav.system.filter.JwtAuthenticationFilter} 从 JWT Token 中解析并传递。</p>
     *
     * @return 始终返回 null
     * @deprecated 应由前端在 Token 中传递用户身份标识，后端通过 Filter 提取
     */
    @Override
    public User getCurrentUser() {
        // Deprecated in favor of JWT-based identification via AuthController
        return null;
    }
}