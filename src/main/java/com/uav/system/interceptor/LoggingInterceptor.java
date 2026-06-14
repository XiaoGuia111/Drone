package com.uav.system.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 请求日志拦截器
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final String START_TIME_ATTR = "startTime";

    @Override
    // 记录请求开始时间
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTR, startTime);

        logger.info("Request URL: {}, Method: {}, Parameters: {}",
                request.getRequestURI(),
                request.getMethod(),
                request.getParameterMap());

        return true;
    }

    @Override
    // 记录请求完成状态码和总执行耗时
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Request completed - URL: {}, Status: {}, Duration: {}ms",
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);
        }
    }
}
