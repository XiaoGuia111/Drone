package com.uav.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域资源共享（CORS）配置
 *
 * <p>允许所有来源、所有请求头和所有 HTTP 方法的跨域请求，适用于前后端分离的开发模式。
 * 过滤器优先级设置为 {@link Ordered#HIGHEST_PRECEDENCE}，确保在所有其他过滤器之前执行。</p>
 *
 * <p><b>安全提示：</b>当前配置 {@code addAllowedOrigin("*")} 在生产环境中过于宽松，
 * 建议上线后修改为明确的前端域名白名单。</p>
 */
@Configuration
public class CorsConfig {

    /**
     * 注册 CORS 过滤器
     * <p>允许所有来源（Origin）、所有请求头（Header）和所有 HTTP 方法（GET/POST/PUT/DELETE 等），
     * 预检请求缓存时长为 3600 秒。</p>
     *
     * @return CorsFilter 实例
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
