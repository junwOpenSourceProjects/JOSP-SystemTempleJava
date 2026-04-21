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
import java.util.concurrent.ConcurrentHashMap;

@Tag(name = "认证中心")
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

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    // 简易内存缓存，生产建议使用 Redis
    private static final Map<String, String> captchaCache = new ConcurrentHashMap<>();

    @Operation(summary = "获取验证码")
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

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginUser loginUser) {
        log.info("Login request for user: {}", loginUser.getUsername());

        // 验证验证码 (开发环境暂时屏蔽)
        /*
        String cachedCode = captchaCache.get(loginUser.getCaptchaKey());
        if (cachedCode == null || !cachedCode.equalsIgnoreCase(loginUser.getCaptchaCode())) {
            return Result.failed("验证码错误或已失效");
        }
        captchaCache.remove(loginUser.getCaptchaKey());
        */

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginUser.getUsername());
        if (!passwordEncoder.matches(loginUser.getPassword(), userDetails.getPassword())) {
            return Result.failed("密码错误");
        }
        
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String token = jwtTokenUtil.generateToken(userDetails);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return Result.success(tokenMap);
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/me")
    public Result<UserInfo> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LoginUser user = loginUserService.getByUsername(username);

        if (user == null) {
            return Result.failed("用户不存在");
        }

        // 从数据库获取用户角色列表
        List<String> roles = accountRoleMapper.selectRoleCodesByUserId(user.getId());
        if (roles == null || roles.isEmpty()) {
            roles = Collections.singletonList("USER");
        }

        // 从数据库获取用户权限标识列表（按钮权限）
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

    @Operation(summary = "注销")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success(null);
    }
}
