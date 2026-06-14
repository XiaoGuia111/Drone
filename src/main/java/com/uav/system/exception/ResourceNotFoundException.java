package com.uav.system.exception;

/**
 * 资源未找到异常
 *
 * <p>当根据 ID 或序列号查询无人机或用户等资源不存在时抛出。
 * 由 {@link GlobalExceptionHandler} 捕获后返回 HTTP 404 Not Found 状态码。</p>
 *
 * @see GlobalExceptionHandler
 * @see com.uav.system.service.impl.UavServiceImpl
 */
public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
