package com.uav.system.exception;

/**
 * 通用业务异常
 *
 * <p>业务逻辑层面的异常基类，继承自 {@link RuntimeException}（非受检异常），
 * 由 {@link GlobalExceptionHandler} 统一捕获并转换为标准错误响应返回前端。</p>
 *
 * <p>当抛出此异常时，HTTP 状态码默认为 500，消息内容会直接返回给前端。
 * <b>注意：</b>不要在消息中泄露敏感信息（如数据库详情、堆栈信息）。</p>
 *
 * @see GlobalExceptionHandler
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
