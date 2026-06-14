package com.uav.system.config;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MyBatis 类型处理器 —— LocalDateTime ↔ SQLite 字符串
 *
 * <p>由于 SQLite 没有原生的日期时间类型，LocalDateTime 需要以字符串格式存储。
 * 本处理器将 Java 8 {@link LocalDateTime} 与 SQLite 中的字符串进行双向转换。</p>
 *
 * <p><b>存储格式：</b>{@code yyyy-MM-dd HH:mm:ss}</p>
 * <p><b>回读策略：</b>优先使用自定义格式解析；如果失败，尝试使用 ISO_LOCAL_DATE_TIME 格式解析，提供兼容性。</p>
 *
 * <p><b>性能注意：</b>DateTimeFormatter 是线程安全的，定义为 static final 常量复用。</p>
 *
 * @see org.apache.ibatis.type.BaseTypeHandler
 * @see LocalDateTypeHandler
 */
@MappedTypes(LocalDateTime.class)
public class LocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 将 LocalDateTime 转换为字符串写入 PreparedStatement
     * <p>使用 "yyyy-MM-dd HH:mm:ss" 格式写入，适配 SQLite 的文本存储。</p>
     *
     * @param ps        预编译 SQL 语句
     * @param i         参数索引（从 1 开始）
     * @param parameter Java 8 LocalDateTime 对象
     * @param jdbcType  JDBC 类型（自动映射，可忽略）
     * @throws SQLException SQL 执行异常
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.format(FORMATTER));
    }

    /**
     * 从 ResultSet 中按列名读取 LocalDateTime
     * <p><b>回读策略：</b>优先使用 "yyyy-MM-dd HH:mm:ss" 格式解析；
     * 若失败，尝试使用 ISO_LOCAL_DATE_TIME 格式作为降级方案。
     * 这种双格式兼容策略确保了前后数据格式不一致时的容错性。</p>
     *
     * @param rs         结果集
     * @param columnName 列名
     * @return LocalDateTime 对象；数据库值为 null 时返回 null
     * @throws SQLException SQL 执行异常
     */
    @Override
    public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        if (value != null) {
            try {
                return LocalDateTime.parse(value, FORMATTER);
            } catch (Exception e) {
                return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        }
        return null;
    }

    /**
     * 从 ResultSet 中按列索引读取 LocalDateTime
     *
     * @param rs          结果集
     * @param columnIndex 列索引（从 1 开始）
     * @return LocalDateTime 对象；数据库值为 null 时返回 null
     * @throws SQLException SQL 执行异常
     */
    @Override
    public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        if (value != null) {
            try {
                return LocalDateTime.parse(value, FORMATTER);
            } catch (Exception e) {
                return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        }
        return null;
    }

    /**
     * 从 CallableStatement 中按列索引读取 LocalDateTime
     * <p>用于存储过程或函数的出参读取。</p>
     *
     * @param cs          可调用语句
     * @param columnIndex 列索引（从 1 开始）
     * @return LocalDateTime 对象；数据库值为 null 时返回 null
     * @throws SQLException SQL 执行异常
     */
    @Override
    public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        if (value != null) {
            try {
                return LocalDateTime.parse(value, FORMATTER);
            } catch (Exception e) {
                return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        }
        return null;
    }
}