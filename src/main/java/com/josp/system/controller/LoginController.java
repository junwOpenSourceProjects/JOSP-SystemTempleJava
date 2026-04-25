package com.josp.system.controller;

import com.josp.system.common.api.CaptchaResult;
import com.josp.system.common.api.Result;
import com.josp.system.common.api.UserInfo;
import com.josp.system.entity.LoginUser;
import com.josp.system.security.jwt.JwtTokenUtil;
import com.josp.system.service.LoginUserService;
import com.josp.system.dao.AccountRoleMapper;
import com.josp.system.dao.MenuMapper;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Authentication Controller handling login, logout, captcha and user info operations.
 * Provides endpoints for user authentication and current user information retrieval.
 *
 * @author JOSP System
 * @version 1.0
 */
@Tag(name = "Authentication Center")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final UserDetailsService userDetailsService;
    private final LoginUserService loginUserService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AccountRoleMapper accountRoleMapper;
    private final MenuMapper menuMapper;
    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Value("${jwt.expiration:7200}")
    private Long expiration;

    /**
     * In-memory captcha cache for development.
     * Production should use Redis.
     */
    private static final Map<String, String> captchaCache = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * Generates a captcha image for login verification.
     *
     * @return Result containing captcha key and base64 encoded image
     */
    @Operation(summary = "Get captcha")
    @GetMapping("/captcha")
    public Result<CaptchaResult> getCaptcha() {
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(116, 36);
        String code = lineCaptcha.getCode();
        String imageBase64 = lineCaptcha.getImageBase64Data();
        String captchaKey = IdUtil.fastSimpleUUID();

        captchaCache.put(captchaKey, code);

        CaptchaResult result = CaptchaResult.builder()
                .captchaKey(captchaKey)
                .captchaBase64(imageBase64)
                .build();

        return Result.success(result);
    }

    /**
     * Authenticates user credentials and returns JWT token.
     * Note: Captcha verification is currently commented out for development.
     *
     * @param loginUser login request containing username and password
     * @return Result containing JWT token and token head
     */
    @Operation(summary = "User login")
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginUser loginUser) {
        log.info("Login request for user: {}", loginUser.getUsername());

        // Verify captcha (currently disabled for development)
        /*
        String cachedCode = captchaCache.get(loginUser.getCaptchaKey());
        if (cachedCode == null || !cachedCode.equalsIgnoreCase(loginUser.getCaptchaCode())) {
            return Result.failed("Captcha error or expired");
        }
        captchaCache.remove(loginUser.getCaptchaKey());
        */

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginUser.getUsername());
        // Note: In production, uncomment the following block to enable password verification.
        // The admin password is "123456" (BCrypt hash stored in database).
        // Password verification is currently bypassed for development convenience.
        if (!passwordEncoder.matches(loginUser.getPassword(), userDetails.getPassword())) {
            return Result.failed("Incorrect password");
        }
        
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String token = jwtTokenUtil.generateToken(userDetails);
        
        // Store token in Redis for logout support
        redisTemplate.opsForValue().set("login_tokens:" + loginUser.getUsername(), token, expiration, TimeUnit.SECONDS);
        
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return Result.success(tokenMap);
    }

    /**
     * Retrieves current authenticated user's information.
     *
     * @return Result containing user info including roles and permissions
     */
    @Operation(summary = "Get current user info")
    @GetMapping("/me")
    public Result<UserInfo> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LoginUser user = loginUserService.getByUsername(username);

        if (user == null) {
            return Result.failed("User not found");
        }

        // Get user roles from database
        List<String> roles = accountRoleMapper.selectRoleCodesByUserId(user.getId());
        if (roles == null || roles.isEmpty()) {
            roles = Collections.singletonList("USER");
        }

        // Get user permission identifiers from database (button permissions)
        List<String> perms = menuMapper.selectMenuPermsByUserId(user.getId());
        if (perms == null || perms.isEmpty()) {
            perms = Collections.singletonList("*:*:*");
        }

        UserInfo userInfo = UserInfo.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getName())
                .avatar("https://api.dicebear.com/7.x/avataaars/svg?seed=" + user.getUsername())
                .roles(roles)
                .perms(perms)
                .build();

        return Result.success(userInfo);
    }

    /**
     * Logs out the current user by clearing security context and removing token from Redis.
     *
     * @return Result indicating successful logout
     */
    @Operation(summary = "User logout")
    @PostMapping("/logout")
    public Result<String> logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Clear token from Redis
        redisTemplate.delete("login_tokens:" + username);
        
        // Clear security context
        SecurityContextHolder.clearContext();
        
        return Result.success(null, "Logout successful");
    }
}
