package com.uav.system.repository.mapper;

import com.uav.system.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户数据访问层
 *
 * <p>提供用户表的查询操作，主要用于 Shiro 认证和登录功能。</p>
 */
@Mapper
public interface UserMapper {

    /** 根据用户名查询用户 */
    User findByUsername(@Param("username") String username);
}
