/**
 * 认证控制器（AuthController）单元测试
 *
 * <p>使用 {@link ExtendWith(MockitoExtension.class)} 纯 Mock 方式测试，
 * 不加载 Spring 上下文。通过 Mock {@link AuthService} 和 {@link JwtUtil} 的
 * 行为来验证控制器的业务分发逻辑。</p>
 *
 * <p>测试覆盖：登录成功/失败、登出、获取当前用户等场景。</p>
 *
 * @see AuthController
 * @see AuthService
 * @see JwtUtil
 */
package com.uav.system.controller;

import com.uav.system.common.Result;
import com.uav.system.domain.entity.User;
import com.uav.system.domain.vo.LoginResponse;
import com.uav.system.exception.BusinessException;
import com.uav.system.service.api.AuthService;
import com.uav.system.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setPassword("e10adc3949ba59abbe56e057f20f883e");
        testUser.setRole("admin");
    }

    @Test
    void loginSuccessReturns200() {
        when(authService.login("admin", "123456")).thenReturn(testUser);
        when(jwtUtil.generateToken("admin")).thenReturn("test-token");

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "admin");
        credentials.put("password", "123456");

        Result<LoginResponse> result = authController.login(credentials);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("admin", result.getData().getUser().getUsername());
        assertEquals("test-token", result.getData().getToken());
    }

    @Test
    void loginFailureReturnsError() {
        when(authService.login("wrong", "wrong"))
                .thenThrow(new BusinessException("Invalid username or password"));

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "wrong");
        credentials.put("password", "wrong");

        assertThrows(BusinessException.class, () -> {
            authController.login(credentials);
        });
    }

    @Test
    void logoutSuccess() {
        doNothing().when(authService).logout();

        Result<Void> result = authController.logout();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(authService, times(1)).logout();
    }

    @Test
    void getCurrentUserReturnsUser() {
        when(authService.getCurrentUser()).thenReturn(testUser);

        Result<User> result = authController.getCurrentUser();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("admin", result.getData().getUsername());
    }

    @Test
    void getCurrentUserReturnsNullWhenNotAuthenticated() {
        when(authService.getCurrentUser()).thenReturn(null);

        Result<User> result = authController.getCurrentUser();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNull(result.getData());
    }
}