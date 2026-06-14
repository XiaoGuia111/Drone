package com.uav.system.exception;

/**
 * 序列号重复异常
 *
 * <p>在创建或更新无人机时，如果提供的序列号（{@code serialNumber}）已存在且非当前记录，
 * 则抛出此异常。数据库表已定义 UNIQUE 约束，应用层先于数据库做此检查。</p>
 *
 * <p>由 {@link GlobalExceptionHandler} 捕获后返回 HTTP 409 Conflict 状态码。</p>
 *
 * @see GlobalExceptionHandler
 * @see com.uav.system.service.impl.UavServiceImpl
 */
public class DuplicateSerialNumberException extends BusinessException {
    public DuplicateSerialNumberException(String message) {
        super(message);
    }
}
