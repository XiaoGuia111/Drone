package com.uav.system.config;

import com.uav.system.filter.JwtAuthenticationFilter;
import com.uav.system.repository.mapper.UserMapper;
import com.uav.system.util.JwtUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 认证过滤器注册配置
 *
 * <p>配置 {@link JwtAuthenticationFilter} 的 Bean 创建和过滤路径绑定。
 * 过滤器仅对 {@code /api/*} 路径生效，登录等公开路径在过滤器内部已做放行处理。</p>
 *
 * @see JwtAuthenticationFilter
 * @see FilterRegistrationBean
 */
@Configuration
public class JwtFilterConfig {

    /**
     * 创建 JWT 认证过滤器实例
     *
     * @param jwtUtil   JWT 工具类
     * @param userMapper 用户 Mapper（用于验证 Token 对应用户是否仍存在）
     * @return JwtAuthenticationFilter 实例
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, UserMapper userMapper) {
        return new JwtAuthenticationFilter(jwtUtil, userMapper);
    }

    /**
     * 注册 JWT 过滤器到 Servlet 容器
     * <p>绑定到 {@code /api/*} 路径模式，优先级设为 1（高于默认过滤器）。
     * 未匹配到该路径的请求（如静态资源、swagger 等）不经过此过滤器。</p>
     *
     * @param jwtAuthenticationFilter 已创建的过滤器实例
     * @return FilterRegistrationBean 注册信息
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtAuthenticationFilter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        return registration;
    }
}
