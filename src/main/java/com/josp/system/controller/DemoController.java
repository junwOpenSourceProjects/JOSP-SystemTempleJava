package com.josp.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.josp.system.common.api.PageResult;
import com.josp.system.common.api.Result;
import com.josp.system.dao.DemoMapper;
import com.josp.system.entity.Demo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 示例表格接口
 */
@Tag(name = "示例接口")
@RestController
@RequestMapping("/api/v1/demo")
@RequiredArgsConstructor
public class DemoController {

    private final DemoMapper demoMapper;

    @Operation(summary = "获取表格列表")
    @GetMapping("/table/list")
    public Result<PageResult<Map<String, Object>>> getTableList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status
    ) {
        Page<Demo> pageParam = new Page<>(page, limit);

        LambdaQueryWrapper<Demo> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like(Demo::getTitle, title);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Demo::getStatus, status);
        }
        wrapper.orderByDesc(Demo::getId);

        IPage<Demo> result = demoMapper.selectPage(pageParam, wrapper);

        PageResult<Map<String, Object>> pageResult = new PageResult<>(
                result.getRecords().stream().map(this::toMap).toList(),
                result.getTotal()
        );
        return Result.success(pageResult);
    }

    @Operation(summary = "获取表格表单数据")
    @GetMapping("/table/{id}")
    public Result<Map<String, Object>> getTableItem(@PathVariable Long id) {
        Demo demo = demoMapper.selectById(id);
        if (demo == null) {
            return Result.failed("记录不存在");
        }
        return Result.success(toMap(demo));
    }

    @Operation(summary = "新增表格记录")
    @PostMapping("/table")
    public Result<Map<String, Object>> createTableItem(@RequestBody Demo demo) {
        // 设置创建时间
        if (demo.getTimestamp() == null) {
            demo.setTimestamp(LocalDate.now());
        }
        // pageviews 默认 0
        if (demo.getPageviews() == null) {
            demo.setPageviews(0);
        }

        demoMapper.insert(demo);
        return Result.success(toMap(demo));
    }

    @Operation(summary = "更新表格记录")
    @PutMapping("/table/{id}")
    public Result<Map<String, Object>> updateTableItem(@PathVariable Long id, @RequestBody Demo demo) {
        Demo existing = demoMapper.selectById(id);
        if (existing == null) {
            return Result.failed("记录不存在");
        }

        // 保留原有 timestamp 和 pageviews，只更新可编辑字段
        demo.setId(id);
        demo.setTimestamp(existing.getTimestamp());
        demo.setPageviews(existing.getPageviews());

        demoMapper.updateById(demo);
        return Result.success(toMap(demo));
    }

    @Operation(summary = "删除表格记录")
    @DeleteMapping("/table/{id}")
    public Result<Void> deleteTableItem(@PathVariable Long id) {
        if (demoMapper.selectById(id) == null) {
            return Result.failed("记录不存在");
        }
        demoMapper.deleteById(id);
        return Result.success(null);
    }

    private Map<String, Object> toMap(Demo demo) {
        Map<String, Object> map = new HashMap<>();
        // Long 类型 id 转为字符串，避免 JavaScript 精度丢失
        map.put("id", String.valueOf(demo.getId()));
        map.put("title", demo.getTitle());
        map.put("author", demo.getAuthor());
        map.put("pageviews", demo.getPageviews());
        map.put("status", demo.getStatus());
        map.put("timestamp", demo.getTimestamp() != null ? demo.getTimestamp().toString() : null);
        return map;
    }
}
