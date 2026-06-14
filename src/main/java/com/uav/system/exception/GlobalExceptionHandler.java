package com.uav.system.exception;

import com.uav.system.common.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

// ============================================================
// 全局异常处理器
// 统一捕获业务异常、参数校验异常、未知异常，返回标准 Result 格式
// ============================================================
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理序列号重复异常 → HTTP 409 Conflict
     */
    @ExceptionHandler(DuplicateSerialNumberException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Result<Void> handleDuplicateSerialNumber(DuplicateSerialNumberException e) {
        return Result.error(HttpStatus.CONFLICT.value(), e.getMessage());
    }

    /**
     * 处理资源未找到异常 → HTTP 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleResourceNotFoundException(ResourceNotFoundException e) {
        return Result.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    /**
     * 处理通用业务异常 → HTTP 500 Internal Server Error
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getMessage());
    }

    /**
     * 处理 @Valid 参数校验失败异常 → HTTP 400 Bad Request
     * <p>提取所有字段的校验失败信息，组装为可读的字符串返回。</p>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Result.error(HttpStatus.BAD_REQUEST.value(), "Validation failed: " + errors);
    }

    /**
     * 兜底异常处理器 — 处理所有未被上述方法捕获的未知异常
     * <p><b>注意：</b>此处不返回具体异常消息，防止泄露敏感信息。</p>
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleGenericException(Exception e) {
        // TODO: 添加日志记录，便于排查问题
        return Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error");
    }
}
