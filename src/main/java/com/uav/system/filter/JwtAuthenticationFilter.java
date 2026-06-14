package com.uav.system.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uav.system.common.Result;
import com.uav.system.domain.entity.User;
import com.uav.system.repository.mapper.UserMapper;
import com.uav.system.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// JWT 认证过滤器
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 不需要 JWT 认证的公开路径列表（请求路径以此前缀开头即放行）
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/auth/login",     // 登录接口（需要先认证才能获取 Token）
            "/api/auth/logout",    // 登出接口（允许携带无效 Token 登出）
            "/swagger-ui",         // Swagger 文档页面
            "/v3/api-docs"         // OpenAPI 规范文档
    );

    /**
     * 构造 JWT 认证过滤器
     *
     * @param jwtUtil   JWT 令牌工具类，用于验证和解析 Token
     * @param userMapper 用户数据访问接口，用于验证 Token 对应用户是否存在
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    /**
     * 核心过滤逻辑 — 对每个 /api/* 请求执行 JWT 认证
     *
     * <p>处理流程（按优先级）：</p>
     * <ol>
     *   <li><b>预检请求放行：</b>OPTIONS 请求直接通过（跨域场景下由 CorsFilter 处理）</li>
     *   <li><b>公开路径放行：</b>匹配 PUBLIC_PATHS 的请求无需 Token</li>
     *   <li><b>Token 校验：</b>从 Authorization header 提取 Bearer Token，验证签名和有效期</li>
     *   <li><b>用户验证：</b>从 Token 提取用户名并查询数据库，确保用户仍存在</li>
     *   <li><b>鉴权结果：</b>验证通过则放行请求；失败则返回 401 JSON 错误响应</li>
     * </ol>
     *
     * @param request      HTTP 请求
     * @param response     HTTP 响应
     * @param filterChain  过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String requestMethod = request.getMethod();

        // 步骤 1: OPTIONS 预检请求直接放行（CORS 场景下由 CorsFilter 处理跨域头）
        if ("OPTIONS".equalsIgnoreCase(requestMethod)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 步骤 2: 检查是否为公开路径
        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(requestPath::startsWith);
        if (isPublic) {
            filterChain.doFilter(request, response);
            return;
        }

        // 步骤 3: 检查 Authorization header 格式（必须为 "Bearer <token>"）
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            sendError(response, "Missing or invalid Authorization header");
            return;
        }

        // 步骤 4: 提取并校验 Token
        String token = authorizationHeader.substring(7);
        boolean isValid = jwtUtil.validateToken(token);
        if (!isValid) {
            sendError(response, "Invalid or expired token");
            return;
        }

        // 步骤 5: 从 Token 提取用户名，验证用户仍存在于数据库
        String username = jwtUtil.extractUsername(token);
        User user = userMapper.findByUsername(username);
        if (user == null) {
            sendError(response, "User not found — account may have been deleted");
            return;
        }

        // 步骤 6: 认证通过，继续执行后续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 构造并发送 401 认证错误响应
     * <p>向客户端返回 JSON 格式的 {@link Result} 错误消息，
     * 同时设置 CORS 响应头以允许跨域场景下的错误信息传递。</p>
     *
     * @param response HTTP 响应
     * @param message  错误描述信息
     * @throws IOException JSON 序列化或 IO 异常
     */
    private void sendError(HttpServletResponse response, String message) throws IOException {
        // 设置 CORS 响应头（确保跨域场景下前端能读取到错误信息）
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> errorResult = Result.error(HttpStatus.UNAUTHORIZED.value(), message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResult));
    }
}