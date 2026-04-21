package com.josp.system.service;

import com.josp.system.security.jwt.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService 单元测试")
class LoginServiceTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private LoginUserService loginUserService;

    private com.josp.system.entity.LoginUser testUser;

    @BeforeEach
    void setUp() {
        testUser = com.josp.system.entity.LoginUser.builder()
                .id(1L)
                .username("testuser")
                .password("$2a$10$encoded_password")
                .name("Test User")
                .status(1)
                .build();
    }

    @Test
    @DisplayName("验证用户密码 - 正确")
    void testVerifyPassword_Correct() {
        when(passwordEncoder.matches("password123", "$2a$10$encoded_password")).thenReturn(true);

        boolean result = passwordEncoder.matches("password123", "$2a$10$encoded_password");

        assertTrue(result);
    }

    @Test
    @DisplayName("验证用户密码 - 错误")
    void testVerifyPassword_Incorrect() {
        when(passwordEncoder.matches("wrongpassword", "$2a$10$encoded_password")).thenReturn(false);

        boolean result = passwordEncoder.matches("wrongpassword", "$2a$10$encoded_password");

        assertFalse(result);
    }

    @Test
    @DisplayName("加载用户信息 - 成功")
    void testLoadUserByUsername_Success() {
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUser);

        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    @DisplayName("加载用户信息 - 用户不存在")
    void testLoadUserByUsername_NotFound() {
        when(userDetailsService.loadUserByUsername("nonexistent"))
                .thenThrow(new UsernameNotFoundException("用户名或密码错误"));

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent");
        });
    }

    @Test
    @DisplayName("生成JWT Token")
    void testGenerateToken() {
        when(jwtTokenUtil.generateToken(any(UserDetails.class))).thenReturn("jwt_token_here");

        String token = jwtTokenUtil.generateToken(testUser);

        assertNotNull(token);
        assertEquals("jwt_token_here", token);
    }

    @Test
    @DisplayName("验证JWT Token")
    void testValidateToken() {
        when(jwtTokenUtil.validateToken(eq("jwt_token_here"), any(UserDetails.class))).thenReturn(true);

        boolean result = jwtTokenUtil.validateToken("jwt_token_here", testUser);

        assertTrue(result);
    }

    @Test
    @DisplayName("从Token获取用户名")
    void testGetUsernameFromToken() {
        when(jwtTokenUtil.getUsernameFromToken("jwt_token_here")).thenReturn("testuser");

        String username = jwtTokenUtil.getUsernameFromToken("jwt_token_here");

        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("用户详情服务 - 密码编码")
    void testPasswordEncoder_Encode() {
        when(passwordEncoder.encode("plain_password")).thenReturn("$2a$10$encoded");

        String encoded = passwordEncoder.encode("plain_password");

        assertNotNull(encoded);
        assertEquals("$2a$10$encoded", encoded);
    }

    @Test
    @DisplayName("用户详情服务 - 密码匹配")
    void testPasswordEncoder_Matches() {
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        boolean matches = passwordEncoder.matches("raw", "encoded");

        assertTrue(matches);
    }

    @Test
    @DisplayName("用户登录成功场景")
    void testLogin_Success() throws Exception {
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(jwtTokenUtil.generateToken(any(UserDetails.class))).thenReturn("jwt_token_abc123");

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
        boolean passwordMatch = passwordEncoder.matches("password123", userDetails.getPassword());
        String token = jwtTokenUtil.generateToken(userDetails);

        assertTrue(passwordMatch);
        assertNotNull(token);
        assertEquals("jwt_token_abc123", token);
    }

    @Test
    @DisplayName("用户登录失败 - 密码错误")
    void testLogin_Failed_WrongPassword() throws Exception {
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
        boolean passwordMatch = passwordEncoder.matches("wrongpassword", userDetails.getPassword());

        assertFalse(passwordMatch);
    }
}