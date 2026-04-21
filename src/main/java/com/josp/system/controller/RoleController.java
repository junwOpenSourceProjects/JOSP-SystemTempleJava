package com.josp.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.josp.system.common.api.PageResult;
import com.josp.system.common.api.Result;
import com.josp.system.dto.RoleDTO;
import com.josp.system.entity.Role;
import com.josp.system.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "角色管理接口")
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "获取角色分页列表")
    @GetMapping("/page")
    public Result<PageResult<Map<String, Object>>> getRolePage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status
    ) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Role::getName, keyword).or().like(Role::getCode, keyword));
        }
        if (status != null) {
            wrapper.eq(Role::getStatus, status);
        }
        wrapper.orderByDesc(Role::getCreateTime);

        Page<Role> pageParam = new Page<>(page, limit);
        IPage<Role> pageResult = roleService.page(pageParam, wrapper);

        List<Map<String, Object>> records = pageResult.getRecords().stream().map(role -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", role.getId());
            map.put("name", role.getName());
            map.put("code", role.getCode());
            map.put("sort", role.getSort());
            map.put("status", role.getStatus());
            map.put("remark", role.getRemark());
            map.put("createTime", role.getCreateTime());
            map.put("updateTime", role.getUpdateTime());
            return map;
        }).toList();

        return Result.success(new PageResult<>(records, pageResult.getTotal()));
    }

    @Operation(summary = "获取角色选项列表")
    @GetMapping("/options")
    public Result<List<Map<String, Object>>> getRoleOptions() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getStatus, 1);
        wrapper.orderByAsc(Role::getSort);
        List<Role> roles = roleService.list(wrapper);

        List<Map<String, Object>> options = roles.stream().map(role -> {
            Map<String, Object> map = new HashMap<>();
            map.put("value", role.getId());
            map.put("label", role.getName());
            return map;
        }).toList();

        return Result.success(options);
    }

    @Operation(summary = "获取所有角色列表")
    @GetMapping
    public Result<List<Role>> listAllRoles() {
        List<Role> roles = roleService.listAllRoles();
        return Result.success(roles);
    }

    @Operation(summary = "根据ID获取角色详情")
    @GetMapping("/{id}")
    public Result<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return Result.success(role);
    }

    @Operation(summary = "获取角色的菜单ID列表")
    @GetMapping("/{id}/menuIds")
    public Result<List<Long>> getRoleMenuIds(@PathVariable Long id) {
        List<Long> menuIds = roleService.getMenuIdsByRoleId(id);
        return Result.success(menuIds);
    }

    @Operation(summary = "获取角色表单数据")
    @GetMapping("/{id}/form")
    public Result<Map<String, Object>> getRoleFormData(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return Result.failed("角色不存在");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", role.getId());
        map.put("name", role.getName());
        map.put("code", role.getCode());
        map.put("sort", role.getSort());
        map.put("status", role.getStatus());
        map.put("remark", role.getRemark());
        map.put("menuIds", roleService.getMenuIdsByRoleId(id));
        return Result.success(map);
    }

    @Operation(summary = "创建角色")
    @PostMapping
    public Result<Boolean> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        boolean result = roleService.createRole(roleDTO);
        return Result.success(result, "创建成功");
    }

    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    public Result<Boolean> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO) {
        roleDTO.setId(id);
        boolean result = roleService.updateRole(roleDTO);
        return Result.success(result, "更新成功");
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteRole(@PathVariable Long id) {
        boolean result = roleService.deleteRole(id);
        return Result.success(result, "删除成功");
    }

    @Operation(summary = "批量删除角色")
    @DeleteMapping("/{ids}")
    public Result<Void> deleteRolesByIds(@PathVariable String ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.failed("请选择要删除的角色");
        }
        String[] idArray = ids.split(",");
        for (String idStr : idArray) {
            roleService.deleteRole(Long.parseLong(idStr.trim()));
        }
        return Result.success(null, "批量删除成功");
    }

    @Operation(summary = "分配菜单权限给角色")
    @PutMapping("/{id}/menus")
    public Result<Boolean> assignMenusToRole(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        boolean result = roleService.assignMenus(id, menuIds);
        return Result.success(result, "分配成功");
    }
}
