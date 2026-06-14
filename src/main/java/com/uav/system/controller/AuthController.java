package com.uav.system.controller;

import com.uav.system.common.Result;
import com.uav.system.domain.entity.User;
import com.uav.system.domain.vo.LoginResponse;
import com.uav.system.service.api.AuthService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器 — 处理登录、登出、获取当前用户等认证相关请求
 *
 * <p>
 * 使用 Apache Shiro 进行认证管理。登录成功后，Shiro 自动维护用户会话；
 * 登出时清除 Shiro 会话状态。
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录（Shiro 认证）
     *
     * <p>
     * 接收用户名和密码，通过 Shiro Subject 进行身份认证。
     * </p>
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        try {
            subject.login(token);
            User user = authService.findByUsername(username);
            String sessionId = (String) subject.getSession().getId();
            LoginResponse response = new LoginResponse(user, sessionId);
            return Result.success(response);
        } catch (UnknownAccountException | IncorrectCredentialsException e) {
            return Result.error(401, "Invalid username or password");
        } catch (LockedAccountException e) {
            return Result.error(403, "Account is locked");
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            subject.logout();
        }
        return Result.success();
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public Result<User> getCurrentUser() {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return Result.error(401, "Not authenticated");
        }
        User user = authService.findByUsername(username);
        return Result.success(user);
    }
}
