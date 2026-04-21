package com.josp.system.controller;

import com.josp.system.common.api.PageResult;
import com.josp.system.common.api.Result;
import com.josp.system.entity.OperLog;
import com.josp.system.service.OperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志控制器
 */
@Tag(name = "操作日志接口")
@RestController
@RequestMapping("/api/v1/operLog")
@RequiredArgsConstructor
public class OperLogController {

    private final OperLogService operLogService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/page")
    public Result<PageResult<OperLog>> pageOperLogs(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String operName,
            @RequestParam(required = false) String status) {
        return Result.success(operLogService.pageOperLogs(pageNum, pageSize, title, operName, status));
    }

    @Operation(summary = "根据ID获取操作日志详情")
    @GetMapping("/{id}")
    public Result<OperLog> getOperLogById(@PathVariable Long id) {
        return Result.success(operLogService.getOperLogById(id));
    }

    @Operation(summary = "删除操作日志")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteOperLog(@PathVariable Long id) {
        return Result.success(operLogService.deleteOperLog(id));
    }

    @Operation(summary = "批量删除操作日志")
    @DeleteMapping("/batch")
    public Result<Boolean> deleteOperLogs(@RequestBody List<Long> ids) {
        return Result.success(operLogService.deleteOperLogs(ids));
    }

    @Operation(summary = "清理操作日志")
    @DeleteMapping("/clean")
    public Result<Integer> cleanOperLogs(@RequestParam(defaultValue = "30") int days) {
        return Result.success(operLogService.cleanOperLogs(days));
    }
}
