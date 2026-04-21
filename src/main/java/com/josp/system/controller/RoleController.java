package com.josp.system.controller;

import com.josp.system.common.api.PageResult;
import com.josp.system.common.api.Result;
import com.josp.system.dto.RoleDTO;
import com.josp.system.entity.Role;
import com.josp.system.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理接口")
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

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
    @GetMapping("/{id}/menus")
    public Result<List<Long>> getRoleMenus(@PathVariable Long id) {
        List<Long> menuIds = roleService.getMenuIdsByRoleId(id);
        return Result.success(menuIds);
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

    @Operation(summary = "分配菜单权限给角色")
    @PostMapping("/{id}/menus")
    public Result<Boolean> assignMenusToRole(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        boolean result = roleService.assignMenus(id, menuIds);
        return Result.success(result, "分配成功");
    }
}
