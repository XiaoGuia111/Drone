package com.uav.system.common;

/**
 * 统一 API 响应结果封装（泛型）
 *
 * <p>系统中所有 REST 接口统一使用此类作为响应体，保证前端能够以一致的格式解析数据。
 * 结构包含三个字段：</p>
 * <ul>
 *   <li>{@code code} — 业务状态码（200 成功，4xx/5xx 异常）</li>
 *   <li>{@code message} — 提示消息</li>
 *   <li>{@code data} — 实际负载数据（泛型 T）</li>
 * </ul>
 *
 * <p><b>设计说明：</b>采用静态工厂方法模式（success / error），
 * 避免调用方手动构造实例，提升代码可读性。</p>
 *
 * @param <T> 响应数据类型
 */
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public Result() {
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /** 返回无数据的成功响应 */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    /**
     * 返回带数据的成功响应
     * <p>最常用的成功响应方法，适用于查询、新增等需要返回业务数据的场景。</p>
     *
     * @param data 响应数据对象
     * @param <T>  响应数据类型
     * @return code=200, message="success", data=data
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /** 返回自定义消息的成功响应 */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 返回默认 500 错误响应
     * <p>适用于未明确指定状态码的业务异常，默认使用 HTTP 500。</p>
     *
     * @param message 错误描述
     * @param <T>     响应数据类型（实际为 null）
     * @return code=500, message=错误描述, data=null
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 返回指定状态码的错误响应
     * <p>适用于需要精确控制 HTTP 状态码的场景，由 {@link com.uav.system.exception.GlobalExceptionHandler} 统一调用。</p>
     *
     * @param code    业务状态码（通常与 HTTP 状态码一致）
     * @param message 错误描述
     * @param <T>     响应数据类型（实际为 null）
     * @return code=指定状态码, message=错误描述, data=null
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
