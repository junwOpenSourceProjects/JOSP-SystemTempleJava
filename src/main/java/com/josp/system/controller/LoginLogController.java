package com.josp.system.controller;

import com.josp.system.common.api.PageResult;
import com.josp.system.common.api.Result;
import com.josp.system.common.utils.ExportUtils;
import com.josp.system.entity.LoginLog;
import com.josp.system.service.LoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录日志控制器
 */
@Tag(name = "登录日志接口")
@RestController
@RequestMapping("/api/v1/login-logs")
@RequiredArgsConstructor
public class LoginLogController {

    private final LoginLogService loginLogService;

    @Operation(summary = "分页查询登录日志")
    @GetMapping("/page")
    public Result<PageResult<LoginLog>> pageLoginLogs(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status) {
        return Result.success(loginLogService.pageLoginLogs(pageNum, pageSize, username, status));
    }

    @Operation(summary = "根据ID获取登录日志详情")
    @GetMapping("/{id}")
    public Result<LoginLog> getLoginLogById(@PathVariable Long id) {
        return Result.success(loginLogService.getById(id));
    }

    @Operation(summary = "根据用户名查询登录日志")
    @GetMapping("/username/{username}")
    public Result<List<LoginLog>> getByUsername(@PathVariable String username) {
        return Result.success(loginLogService.getByUsername(username));
    }

    @Operation(summary = "根据用户ID查询登录日志")
    @GetMapping("/user/{userId}")
    public Result<List<LoginLog>> getByUserId(@PathVariable Long userId) {
        return Result.success(loginLogService.getByUserId(userId));
    }

    @Operation(summary = "删除登录日志")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteLoginLog(@PathVariable Long id) {
        return Result.success(loginLogService.removeById(id));
    }

    @Operation(summary = "批量删除登录日志")
    @DeleteMapping("/batch")
    public Result<Boolean> deleteLoginLogs(@RequestBody List<Long> ids) {
        return Result.success(loginLogService.removeByIds(ids));
    }

    @Operation(summary = "清理登录日志")
    @DeleteMapping("/clean")
    public Result<Integer> cleanLoginLogs(@RequestParam(defaultValue = "30") int days) {
        return Result.success(loginLogService.cleanLoginLogs(days));
    }

    @Operation(summary = "导出登录日志")
    @GetMapping("/export")
    public void exportLoginLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status,
            jakarta.servlet.http.HttpServletResponse response) {
        try {
            List<LoginLog> loginLogs = loginLogService.listLoginLogs(username, status);
            ExportUtils.exportLoginLogs(loginLogs, response);
        } catch (Exception e) {
            throw new RuntimeException("导出登录日志失败: " + e.getMessage());
        }
    }
}
