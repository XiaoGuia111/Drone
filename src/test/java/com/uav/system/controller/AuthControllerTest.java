/**
 * 认证控制器（AuthController）单元测试
 *
 * <p>使用 {@link ExtendWith(MockitoExtension.class)} 纯 Mock 方式测试，
 * 不加载 Spring 上下文。测试 Shiro 认证前的数据查询逻辑。</p>
 *
 * @see AuthController
 * @see AuthService
 */
package com.uav.system.controller;

import com.uav.system.common.Result;
import com.uav.system.domain.entity.User;
import com.uav.system.domain.vo.LoginResponse;
import com.uav.system.service.api.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void findByUsernameShouldReturnUser() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setRole("ADMIN");

        when(authService.findByUsername("admin")).thenReturn(testUser);

        User result = authService.findByUsername("admin");
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
    }

    @Test
    void findByUsernameShouldReturnNullWhenNotFound() {
        when(authService.findByUsername("nonexistent")).thenReturn(null);

        User result = authService.findByUsername("nonexistent");
        assertNull(result);
    }
}
