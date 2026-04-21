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

@Tag(name = "部门管理接口")
@RestController
@RequestMapping("/api/v1/dept")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    @Operation(summary = "获取部门列表")
    @GetMapping
    public Result<List<Map<String, Object>>> getDeptList(
            @Parameter(description = "部门名称") @RequestParam(required = false) String name
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

    @Operation(summary = "获取部门树形结构")
    @GetMapping("/tree")
    public Result<List<Dept>> getDeptTree() {
        return Result.success(deptService.getDeptTree());
    }

    @Operation(summary = "获取部门下拉选项")
    @GetMapping("/options")
    public Result<List<Map<String, Object>>> getDeptOptions() {
        List<Dept> depts = deptService.listAllDepts();
        List<Map<String, Object>> options = buildDeptOptions(depts, 0L);
        return Result.success(options);
    }

    @Operation(summary = "获取部门详情表单数据")
    @GetMapping("/{id}/form")
    public Result<Map<String, Object>> getDeptFormData(@PathVariable Long id) {
        Dept dept = deptService.getById(id);
        if (dept == null) {
            return Result.failed("部门不存在");
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

    @Operation(summary = "根据ID获取部门详情")
    @GetMapping("/{id}")
    public Result<Dept> getDeptById(@PathVariable Long id) {
        return Result.success(deptService.getDeptById(id));
    }

    @Operation(summary = "创建部门")
    @PostMapping
    public Result<Void> createDept(@RequestBody Dept dept) {
        deptService.createDept(dept);
        return Result.success(null, "创建成功");
    }

    @Operation(summary = "更新部门")
    @PutMapping("/{id}")
    public Result<Void> updateDept(@PathVariable Long id, @RequestBody Dept dept) {
        dept.setId(id);
        deptService.updateDept(dept);
        return Result.success(null, "更新成功");
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("/{ids}")
    public Result<Void> deleteDepts(@PathVariable String ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.failed("请选择要删除的部门");
        }
        String[] idArray = ids.split(",");
        for (String idStr : idArray) {
            deptService.deleteDept(Long.parseLong(idStr.trim()));
        }
        return Result.success(null, "删除成功");
    }

    private List<Map<String, Object>> buildDeptOptions(List<Dept> depts, Long parentId) {
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Dept dept : depts) {
            if (java.util.Objects.equals(dept.getParentId(), parentId)) {
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
