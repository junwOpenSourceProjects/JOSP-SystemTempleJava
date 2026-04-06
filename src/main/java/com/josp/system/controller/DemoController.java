package com.josp.system.controller;

import com.josp.system.common.api.PageResult;
import com.josp.system.common.api.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Tag(name = "示例接口")
@RestController
@RequestMapping("/api/v1/demo")
@RequiredArgsConstructor
public class DemoController {

    // 模拟数据库存储
    private static final Map<Long, Map<String, Object>> DATA_STORE = new ConcurrentHashMap<>();
    private static final AtomicLong ID_GENERATOR = new AtomicLong(55);

    static {
        initData();
    }

    private static void initData() {
        String[] statuses = {"published", "draft", "deleted"};
        String[] authors = {"张三", "李四", "王五", "赵六", "钱七"};

        for (int i = 1; i <= 55; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", (long) i);
            item.put("title", "示例文章标题 " + i);
            item.put("author", authors[i % authors.length]);
            item.put("pageviews", (int) (Math.random() * 9000) + 1000);
            item.put("status", statuses[i % statuses.length]);
            item.put("timestamp", "2026-0" + ((i % 9) + 1) + "-" + String.format("%02d", (i % 28) + 1));
            DATA_STORE.put((long) i, item);
        }
    }

    @Operation(summary = "获取表格列表")
    @GetMapping("/table/list")
    public Result<PageResult<Map<String, Object>>> getTableList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status
    ) {
        List<Map<String, Object>> allData = new ArrayList<>(DATA_STORE.values());

        // 过滤
        if (title != null && !title.isEmpty()) {
            allData = allData.stream()
                    .filter(item -> item.get("title").toString().contains(title))
                    .collect(Collectors.toList());
        }
        if (status != null && !status.isEmpty()) {
            allData = allData.stream()
                    .filter(item -> item.get("status").equals(status))
                    .collect(Collectors.toList());
        }

        // 排序
        allData.sort((a, b) -> Long.compare((Long) b.get("id"), (Long) a.get("id")));

        // 分页
        int total = allData.size();
        int start = (page - 1) * limit;
        int end = Math.min(start + limit, total);
        List<Map<String, Object>> pageData = start < total
                ? allData.subList(start, end)
                : new ArrayList<>();

        return Result.success(new PageResult<>(pageData, total));
    }

    @Operation(summary = "获取表格表单数据")
    @GetMapping("/table/{id}")
    public Result<Map<String, Object>> getTableItem(@PathVariable Long id) {
        Map<String, Object> item = DATA_STORE.get(id);
        if (item == null) {
            return Result.failed("记录不存在");
        }
        return Result.success(new HashMap<>(item));
    }

    @Operation(summary = "新增表格记录")
    @PostMapping("/table")
    public Result<Map<String, Object>> createTableItem(@RequestBody Map<String, Object> data) {
        long id = ID_GENERATOR.incrementAndGet();
        data.put("id", id);
        data.put("timestamp", "2026-04-07");
        data.put("pageviews", 0);
        DATA_STORE.put(id, data);
        return Result.success(new HashMap<>(data));
    }

    @Operation(summary = "更新表格记录")
    @PutMapping("/table/{id}")
    public Result<Map<String, Object>> updateTableItem(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        Map<String, Object> existing = DATA_STORE.get(id);
        if (existing == null) {
            return Result.failed("记录不存在");
        }
        // 保留原有 timestamp 和 pageviews，只更新可编辑字段
        data.put("id", id);
        data.put("timestamp", existing.get("timestamp"));
        data.put("pageviews", existing.get("pageviews"));
        DATA_STORE.put(id, data);
        return Result.success(new HashMap<>(data));
    }

    @Operation(summary = "删除表格记录")
    @DeleteMapping("/table/{id}")
    public Result<Void> deleteTableItem(@PathVariable Long id) {
        if (!DATA_STORE.containsKey(id)) {
            return Result.failed("记录不存在");
        }
        DATA_STORE.remove(id);
        return Result.success(null);
    }
}
