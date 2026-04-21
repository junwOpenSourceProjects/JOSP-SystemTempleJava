package com.josp.system.controller;

import com.josp.system.common.api.MonitorVO;
import com.josp.system.common.api.Result;
import com.josp.system.service.MonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统监控控制器
 */
@Tag(name = "系统监控接口")
@RestController
@RequestMapping("/api/v1/monitor")
@RequiredArgsConstructor
public class MonitorController {

    private final MonitorService monitorService;

    @Operation(summary = "获取系统监控信息")
    @GetMapping("/info")
    public Result<MonitorVO> getMonitorInfo() {
        MonitorVO monitorVO = new MonitorVO();
        monitorVO.setServer(monitorService.getServerInfo());
        monitorVO.setDatabase(monitorService.getDatabaseInfo());
        monitorVO.setRedis(monitorService.getRedisInfo());
        monitorVO.setTimestamp(System.currentTimeMillis());
        return Result.success(monitorVO);
    }

    @Operation(summary = "获取服务器信息")
    @GetMapping("/server")
    public Result<Object> getServerInfo() {
        return Result.success(monitorService.getServerInfo());
    }

    @Operation(summary = "获取数据库连接信息")
    @GetMapping("/database")
    public Result<Object> getDatabaseInfo() {
        return Result.success(monitorService.getDatabaseInfo());
    }

    @Operation(summary = "获取Redis状态信息")
    @GetMapping("/redis")
    public Result<Object> getRedisInfo() {
        return Result.success(monitorService.getRedisInfo());
    }
}
