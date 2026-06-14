package com.uav.system.repository.mapper;

import com.uav.system.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户数据访问层（MyBatis Mapper）
 *
 * <p>定义 {@code users} 表的数据操作接口，当前仅提供按用户名查询的方法。
 * 对应的 SQL 映射定义在 {@code mapper/UserMapper.xml} 中。</p>
 *
 * <p><b>注意：</b>当前登录认证直接使用 findByUsername 查询出完整用户记录，
 * 包括密码字段。后续引入 BCrypt 加密后，建议在此处或 Service 层做隔离。</p>
 *
 * @see com.uav.system.domain.entity.User
 * @see UserMapper.xml
 */
@Mapper
public interface UserMapper {
    /**
     * 根据用户名查询用户
     * <p>用于登录认证时的身份查找。返回的 User 对象包含密码字段，
     * 调用方需注意不要在日志中等地方泄露密码。</p>
     *
     * @param username 用户名
     * @return 用户实体，未找到时返回 null
     */
    User findByUsername(@Param("username") String username);
}
