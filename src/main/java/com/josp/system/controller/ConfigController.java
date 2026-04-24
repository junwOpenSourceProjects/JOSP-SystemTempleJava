package com.josp.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.josp.system.common.api.PageResult;
import com.josp.system.common.api.Result;
import com.josp.system.entity.Config;
import com.josp.system.entity.LoginUser;
import com.josp.system.service.ConfigService;
import com.josp.system.service.LoginUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统配置控制器
 */
@Tag(name = "系统配置接口")
@RestController
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;
    private final LoginUserService loginUserService;

    @Operation(summary = "分页查询系统配置")
    @GetMapping("/page")
    public Result<PageResult<Config>> pageConfigs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "配置名称") @RequestParam(required = false) String configName,
            @Parameter(description = "配置键名") @RequestParam(required = false) String configKey
    ) {
        return Result.success(configService.pageConfigs(page, limit, configName, configKey));
    }

    @Operation(summary = "获取所有系统配置")
    @GetMapping("/list")
    public Result<List<Config>> listConfigs() {
        return Result.success(configService.list());
    }

    @Operation(summary = "根据ID获取配置详情")
    @GetMapping("/{id}")
    public Result<Config> getConfigById(@PathVariable Long id) {
        Config config = configService.getById(id);
        if (config == null) {
            return Result.failed("配置不存在");
        }
        return Result.success(config);
    }

    @Operation(summary = "根据键名获取配置值")
    @GetMapping("/key/{configKey}")
    public Result<String> getConfigByKey(@PathVariable String configKey) {
        String value = configService.getValueByKey(configKey);
        return Result.success(value);
    }

    @Operation(summary = "创建系统配置")
    @PostMapping
    public Result<Boolean> createConfig(@RequestBody Config config) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LambdaQueryWrapper<LoginUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginUser::getUsername, username);
        LoginUser user = loginUserService.getOne(wrapper);
        config.setCreateUser(user != null ? user.getId() : null);
        return Result.success(configService.save(config), "创建成功");
    }

    @Operation(summary = "更新系统配置")
    @PutMapping("/{id}")
    public Result<Boolean> updateConfig(@PathVariable Long id, @RequestBody Config config) {
        config.setId(id);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LambdaQueryWrapper<LoginUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginUser::getUsername, username);
        LoginUser user = loginUserService.getOne(wrapper);
        config.setUpdateUser(user != null ? user.getId() : null);
        return Result.success(configService.updateById(config), "更新成功");
    }

    @Operation(summary = "删除系统配置")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteConfig(@PathVariable Long id) {
        return Result.success(configService.removeById(id), "删除成功");
    }

    @Operation(summary = "批量删除系统配置")
    @DeleteMapping("/batch")
    public Result<Boolean> deleteConfigs(@RequestBody List<Long> ids) {
        return Result.success(configService.removeByIds(ids), "批量删除成功");
    }

    @Operation(summary = "刷新系统配置缓存")
    @PostMapping("/refresh")
    public Result<Void> refreshConfig() {
        configService.refreshCache();
        return Result.success(null, "刷新成功");
    }

    @Operation(summary = "获取所有配置（Map形式，用于前端全局配置）")
    @GetMapping("/all")
    public Result<Map<String, String>> getAllConfigMap() {
        return Result.success(configService.getAllConfigMap());
    }
}
