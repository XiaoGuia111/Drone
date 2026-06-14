package com.uav.system.domain.entity;

/**
 * 用户数据库实体
 *
 * <p>对应数据库 {@code users} 表，存储系统用户信息。
 * 当前密码以明文存储（TODO: 建议生产环境改为 BCrypt 加密存储）。</p>
 *
 * <p><b>角色说明：</b>{@code role} 字段用于区分用户权限，
 * 目前支持 ADMIN 和 USER 两种角色，但尚未实现细粒度的权限控制逻辑。</p>
 *
 * @see com.uav.system.repository.mapper.UserMapper
 */
public class User {
    /** 主键 ID */
    private Long id;
    /** 用户名（唯一） */
    private String username;
    /** 密码（当前为明文，建议加密） */
    private String password;
    /** 角色（ADMIN / USER） */
    private String role;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
