package com.josp.system.controller;

import com.josp.system.common.api.PageResult;
import com.josp.system.common.api.Result;
import com.josp.system.entity.Dept;
import com.josp.system.service.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Department Management Controller providing CRUD operations for system departments.
 * Handles department hierarchy, tree structure, and organizational management.
 *
 * @author JOSP System
 * @version 1.0
 */
@Tag(name = "Department Management")
@RestController
@RequestMapping("/api/v1/dept")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    /**
     * Gets department list with optional name filter.
     *
     * @param name department name filter (optional)
     * @return list of departments
     */
    @Operation(summary = "Get department list")
    @GetMapping
    public Result<List<Map<String, Object>>> getDeptList(
            @Parameter(description = "Department name") @RequestParam(required = false) String name
    ) {
        List<Dept> depts = deptService.listAllDepts();
        List<Map<String, Object>> result = depts.stream().map(dept -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dept.getId());
            map.put("parentId", dept.getParentId());
            map.put("name", dept.getName());
            map.put("sort", dept.getSort());
            map.put("status", dept.getStatus());
            map.put("leader", dept.getLeader());
            map.put("phone", dept.getPhone());
            map.put("email", dept.getEmail());
            map.put("createTime", dept.getCreateTime());
            map.put("updateTime", dept.getUpdateTime());
            return map;
        }).toList();
        return Result.success(result);
    }

    @Operation(summary = "Get department paginated list")
    @GetMapping("/page")
    public Result<com.josp.system.common.api.PageResult<Map<String, Object>>> getDeptPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "Department name") @RequestParam(required = false) String name,
            @Parameter(description = "Status") @RequestParam(required = false) Integer status
    ) {
        return Result.success(deptService.getDeptPage(page, limit, name, status));
    }

    /**
     * Gets department tree structure.
     *
     * @return hierarchical department tree
     */
    @Operation(summary = "Get department tree")
    @GetMapping("/tree")
    public Result<List<Dept>> getDeptTree() {
        return Result.success(deptService.getDeptTree());
    }

    /**
     * Gets department options for dropdown selection.
     *
     * @return hierarchical department options
     */
    @Operation(summary = "Get department dropdown options")
    @GetMapping("/options")
    public Result<List<Map<String, Object>>> getDeptOptions() {
        List<Dept> depts = deptService.listAllDepts();
        List<Map<String, Object>> options = buildDeptOptions(depts, 0L);
        return Result.success(options);
    }

    /**
     * Gets department form data for editing.
     *
     * @param id department ID
     * @return department form data
     */
    @Operation(summary = "Get department form data")
    @GetMapping("/{id}/form")
    public Result<Map<String, Object>> getDeptFormData(@PathVariable Long id) {
        Dept dept = deptService.getById(id);
        if (dept == null) {
            return Result.failed("Department not found");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", dept.getId());
        map.put("parentId", dept.getParentId());
        map.put("name", dept.getName());
        map.put("sort", dept.getSort());
        map.put("status", dept.getStatus());
        map.put("leader", dept.getLeader());
        map.put("phone", dept.getPhone());
        map.put("email", dept.getEmail());
        return Result.success(map);
    }

    /**
     * Gets department details by ID.
     *
     * @param id department ID
     * @return department details
     */
    @Operation(summary = "Get department by ID")
    @GetMapping("/{id}")
    public Result<Dept> getDeptById(@PathVariable Long id) {
        return Result.success(deptService.getDeptById(id));
    }

    /**
     * Creates a new department.
     *
     * @param dept department data
     * @return creation result
     */
    @Operation(summary = "Create department")
    @PostMapping
    public Result<Void> createDept(@RequestBody Dept dept) {
        deptService.createDept(dept);
        return Result.success(null, "Created successfully");
    }

    /**
     * Updates an existing department.
     *
     * @param id   department ID
     * @param dept department data
     * @return update result
     */
    @Operation(summary = "Update department")
    @PutMapping("/{id}")
    public Result<Void> updateDept(@PathVariable Long id, @RequestBody Dept dept) {
        dept.setId(id);
        deptService.updateDept(dept);
        return Result.success(null, "Updated successfully");
    }

    /**
     * Deletes departments by IDs.
     *
     * @param ids comma-separated department IDs
     * @return deletion result
     */
    @Operation(summary = "Delete departments")
    @DeleteMapping("/{ids}")
    public Result<Void> deleteDepts(@PathVariable String ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.failed("Please select departments to delete");
        }
        String[] idArray = ids.split(",");
        for (String idStr : idArray) {
            deptService.deleteDept(Long.parseLong(idStr.trim()));
        }
        return Result.success(null, "Deleted successfully");
    }

    /**
     * Builds hierarchical department options recursively.
     *
     * @param depts    all departments
     * @param parentId parent department ID
     * @return hierarchical options list
     */
    private List<Map<String, Object>> buildDeptOptions(List<Dept> depts, Long parentId) {
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Dept dept : depts) {
            if (Objects.equals(dept.getParentId(), parentId)) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", dept.getId());
                map.put("label", dept.getName());
                List<Map<String, Object>> children = buildDeptOptions(depts, dept.getId());
                if (!children.isEmpty()) {
                    map.put("children", children);
                }
                result.add(map);
            }
        }
        return result;
    }
}
