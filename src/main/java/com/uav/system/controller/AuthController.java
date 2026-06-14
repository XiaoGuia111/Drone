package com.uav.system.controller;

import com.uav.system.common.Result;
import com.uav.system.domain.entity.User;
import com.uav.system.domain.vo.LoginResponse;
import com.uav.system.service.api.AuthService;
import com.uav.system.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器 — 处理登录、登出、获取当前用户等认证相关请求
 *
 * <p>所有接口均映射到 {@code /api/auth} 路径下，登录接口为公开路径（无需 JWT），
 * 登出和获取当前用户接口需要携带有效的 Bearer Token。</p>
 *
 * <p><b>认证流程：</b>登录成功后后端签发 JWT → 前端保存在 localStorage →
 * 后续请求通过 Authorization header 携带 → {@link com.uav.system.filter.JwtAuthenticationFilter} 拦截校验。</p>
 *
 * @see com.uav.system.service.api.AuthService
 * @see com.uav.system.util.JwtUtil
 */
@Tag(name = "Authentication", description = "Authentication APIs")
@RestController// 标记为 RESTful 控制器，处理 HTTP 请求
@RequestMapping("/api/auth")// 映射路径，处理认证相关请求
public class AuthController {

    private final AuthService authService;// 认证服务，用于处理登录、登出等认证相关逻辑
    private final JwtUtil jwtUtil;// JWT 工具类，用于生成、解析 JWT Token

    public AuthController(AuthService authService, JwtUtil jwtUtil) {// 构造函数，注入认证服务和 JWT 工具类
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录
     * <p>接收用户名和密码，认证通过后返回 JWT Token 和用户信息。</p>
     * <p><b>注意：</b>当前密码为明文比对，生产环境应升级为加密存储（如 BCrypt）。</p>
     *
     * @param credentials 包含 username 和 password 的请求体（Map）
     * @return {@link Result} 包含 {@link LoginResponse}（含 User 对象和 Token 字符串）
     */
    @Operation(summary = "Login")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        User user = authService.login(username, password);
        String token = jwtUtil.generateToken(username);
        LoginResponse response = new LoginResponse(user, token);
        return Result.success(response);
    }

    /**
     * 用户登出
     * <p>JWT 为无状态认证，服务端无需清除会话状态，客户端自行移除本地 Token 即可。
     * 此接口设计为留给前端调用以保持 API 对称性，实际登出逻辑由前端控制。</p>
     *
     * @return {@link Result} 空成功响应
     */
    @Operation(summary = "Logout")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    /**
     * 获取当前登录用户信息
     * <p>从数据库中查询 Token 对应用户的详细信息。</p>
     *
     * @return {@link Result} 包含 {@link User} 对象；未取到时 data 为 null
     */
    @Operation(summary = "Get current user")
    @GetMapping("/current")
    public Result<User> getCurrentUser() {
        User user = authService.getCurrentUser();
        return Result.success(user);
    }
}
