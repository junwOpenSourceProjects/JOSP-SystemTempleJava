package com.josp.system.controller;

import com.josp.system.common.api.PageResult;
import com.josp.system.common.api.Result;
import com.josp.system.entity.Dept;
import com.josp.system.service.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门控制器
 */
@Tag(name = "部门管理接口")
@RestController
@RequestMapping("/api/v1/dept")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    @Operation(summary = "获取部门树形结构")
    @GetMapping("/tree")
    public Result<List<Dept>> getDeptTree() {
        return Result.success(deptService.getDeptTree());
    }

    @Operation(summary = "获取部门列表")
    @GetMapping("/list")
    public Result<List<Dept>> listDepts() {
        return Result.success(deptService.listAllDepts());
    }

    @Operation(summary = "获取部门树（用于选择）")
    @GetMapping("/treeSelect")
    public Result<List<Dept>> treeSelect() {
        return Result.success(deptService.getDeptTreeSelect());
    }

    @Operation(summary = "根据ID获取部门详情")
    @GetMapping("/{id}")
    public Result<Dept> getDeptById(@PathVariable Long id) {
        return Result.success(deptService.getDeptById(id));
    }

    @Operation(summary = "创建部门")
    @PostMapping
    public Result<Boolean> createDept(@RequestBody Dept dept) {
        return Result.success(deptService.createDept(dept));
    }

    @Operation(summary = "更新部门")
    @PutMapping
    public Result<Boolean> updateDept(@RequestBody Dept dept) {
        return Result.success(deptService.updateDept(dept));
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteDept(@PathVariable Long id) {
        return Result.success(deptService.deleteDept(id));
    }
}
