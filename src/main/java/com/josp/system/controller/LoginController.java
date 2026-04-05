package com.josp.system.controller;

import com.josp.system.common.api.Result;
import com.josp.system.entity.LoginUser;
import com.josp.system.security.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class LoginController {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginUser loginUser) {
        log.info("Login request for user: {}", loginUser.getUsername());
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

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<UserDetails> getAdminInfo() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Result.success(userDetails);
    }

    @Operation(summary = "注销")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success(null);
    }
}
