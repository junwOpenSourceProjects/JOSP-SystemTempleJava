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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Role Management Controller providing CRUD operations for system roles.
 * Handles role pagination, creation, update, deletion and menu assignment.
 *
 * @author JOSP System
 * @version 1.0
 */
@Tag(name = "Role Management")
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * Retrieves paginated role list with optional filtering.
     *
     * @param page    page number (default 1)
     * @param limit   page size (default 10)
     * @param keyword search keyword for name or code
     * @param status  role status filter
     * @return paginated role list
     */
    @Operation(summary = "Get role paginated list")
    @GetMapping("/page")
    public Result<PageResult<Map<String, Object>>> getRolePage(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "Keyword") @RequestParam(required = false) String keyword,
            @Parameter(description = "Status") @RequestParam(required = false) Integer status
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

    /**
     * Gets role options for dropdown selection.
     *
     * @return list of enabled roles
     */
    @Operation(summary = "Get role options list")
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

    /**
     * Gets all roles without pagination.
     *
     * @return list of all roles
     */
    @Operation(summary = "Get all roles")
    @GetMapping
    public Result<List<Role>> listAllRoles() {
        List<Role> roles = roleService.listAllRoles();
        return Result.success(roles);
    }

    /**
     * Gets role details by ID.
     *
     * @param id role ID
     * @return role details
     */
    @Operation(summary = "Get role by ID")
    @GetMapping("/{id}")
    public Result<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return Result.success(role);
    }

    /**
     * Gets menu IDs assigned to a specific role.
     *
     * @param id role ID
     * @return list of menu IDs
     */
    @Operation(summary = "Get role menu IDs")
    @GetMapping("/{id}/menuIds")
    public Result<List<Long>> getRoleMenuIds(@PathVariable Long id) {
        List<Long> menuIds = roleService.getMenuIdsByRoleId(id);
        return Result.success(menuIds);
    }

    /**
     * Gets role form data including assigned menu IDs.
     *
     * @param id role ID
     * @return role form data with menu IDs
     */
    @Operation(summary = "Get role form data")
    @GetMapping("/{id}/form")
    public Result<Map<String, Object>> getRoleFormData(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return Result.failed("Role not found");
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

    /**
     * Creates a new role.
     *
     * @param roleDTO role data transfer object
     * @return creation result
     */
    @Operation(summary = "Create role")
    @PostMapping
    public Result<Boolean> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        boolean result = roleService.createRole(roleDTO);
        return Result.success(result, "Created successfully");
    }

    /**
     * Updates an existing role.
     *
     * @param id      role ID
     * @param roleDTO role data transfer object
     * @return update result
     */
    @Operation(summary = "Update role")
    @PutMapping("/{id}")
    public Result<Boolean> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO) {
        roleDTO.setId(id);
        boolean result = roleService.updateRole(roleDTO);
        return Result.success(result, "Updated successfully");
    }

    /**
     * Deletes a role by ID.
     *
     * @param id role ID
     * @return deletion result
     */
    @Operation(summary = "Delete role")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteRole(@PathVariable Long id) {
        boolean result = roleService.deleteRole(id);
        return Result.success(result, "Deleted successfully");
    }

    /**
     * Batch deletes multiple roles.
     *
     * @param ids comma-separated role IDs
     * @return batch deletion result
     */
    @Operation(summary = "Batch delete roles")
    @DeleteMapping("/{ids}")
    public Result<Void> deleteRolesByIds(@PathVariable String ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.failed("Please select roles to delete");
        }
        String[] idArray = ids.split(",");
        for (String idStr : idArray) {
            roleService.deleteRole(Long.parseLong(idStr.trim()));
        }
        return Result.success(null, "Batch deleted successfully");
    }

    /**
     * Assigns menu permissions to a role.
     *
     * @param id      role ID
     * @param menuIds list of menu IDs to assign
     * @return assignment result
     */
    @Operation(summary = "Assign menus to role")
    @PutMapping("/{id}/menus")
    public Result<Boolean> assignMenusToRole(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        boolean result = roleService.assignMenus(id, menuIds);
        return Result.success(result, "Assigned successfully");
    }
}
