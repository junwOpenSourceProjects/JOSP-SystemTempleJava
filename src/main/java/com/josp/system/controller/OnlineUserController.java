package com.josp.system.controller;

import com.josp.system.common.api.PageResult;
import com.josp.system.common.api.Result;
import com.josp.system.entity.OnlineUser;
import com.josp.system.service.OnlineUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 在线用户控制器
 */
@Tag(name = "在线用户接口")
@RestController
@RequestMapping("/api/v1/online-users")
@RequiredArgsConstructor
public class OnlineUserController {

    private final OnlineUserService onlineUserService;

    @Operation(summary = "分页查询在线用户")
    @GetMapping("/page")
    public Result<PageResult<OnlineUser>> pageOnlineUsers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username) {
        return Result.success(onlineUserService.pageOnlineUsers(pageNum, pageSize, username));
    }

    @Operation(summary = "根据token获取在线用户信息")
    @GetMapping("/{token}")
    public Result<OnlineUser> getOnlineUserByToken(@PathVariable String token) {
        OnlineUser onlineUser = onlineUserService.getOnlineUserByToken(token);
        if (onlineUser == null) {
            return Result.failed("用户已下线");
        }
        return Result.success(onlineUser);
    }

    @Operation(summary = "强制下线用户")
    @DeleteMapping("/{token}")
    public Result<Boolean> forceLogout(@PathVariable String token) {
        return Result.success(onlineUserService.forceLogout(token));
    }

    @Operation(summary = "强制下线所有用户")
    @DeleteMapping("/logout-all")
    public Result<Boolean> forceLogoutAll() {
        return Result.success(onlineUserService.forceLogoutAll());
    }

    @Operation(summary = "获取当前在线用户数")
    @GetMapping("/count")
    public Result<Map<String, Long>> getOnlineUserCount() {
        return Result.success(Map.of("count", onlineUserService.getOnlineUserCount()));
    }
}
