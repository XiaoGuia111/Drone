package com.uav.system.config;

import com.uav.system.interceptor.LoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置
 *
 * <p>注册自定义拦截器到 Spring MVC 拦截器链中。
 * 当前仅注册了 {@link LoggingInterceptor}，对所有 {@code /api/**} 路径的请求进行日志记录。</p>
 *
 * @see LoggingInterceptor
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoggingInterceptor loggingInterceptor;

    public WebMvcConfig(LoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    /**
     * 添加自定义拦截器
     * <p>拦截所有 {@code /api/**} 路径的请求，记录请求 URL、方法和执行耗时。</p>
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/api/**");
    }
}
