package com.josp.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.josp.system.common.api.Result;
import com.josp.system.dao.*;
import com.josp.system.entity.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "首页看板")
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DailyStatsMapper dailyStatsMapper;
    private final WeeklyVisitsMapper weeklyVisitsMapper;
    private final CategorySalesMapper categorySalesMapper;
    private final MonthlySalesMapper monthlySalesMapper;

    @Operation(summary = "获取统计卡片数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        QueryWrapper<DailyStats> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("stat_date").last("LIMIT 1");
        DailyStats stats = dailyStatsMapper.selectOne(queryWrapper);

        Map<String, Object> data = new HashMap<>();
        if (stats != null) {
            data.put("totalUsers", stats.getTotalUsers());
            data.put("totalOrders", stats.getTotalOrders());
            data.put("totalRevenue", stats.getTotalRevenue());
            data.put("todayVisits", stats.getTodayVisits());
            data.put("yesterdayVisits", stats.getYesterdayVisits());
            // 计算增长率
            if (stats.getYesterdayVisits() != null && stats.getYesterdayVisits() > 0) {
                double rate = (stats.getTodayVisits() - stats.getYesterdayVisits()) * 100.0 / stats.getYesterdayVisits();
                data.put("growthRate", Math.round(rate * 10) / 10.0);
            } else {
                data.put("growthRate", 0);
            }
        } else {
            // 无数据时返回默认值
            data.put("totalUsers", 10284);
            data.put("totalOrders", 3856);
            data.put("totalRevenue", new BigDecimal("1285600"));
            data.put("todayVisits", 2345);
            data.put("yesterdayVisits", 2187);
            data.put("growthRate", 12.5);
        }
        return Result.success(data);
    }

    @Operation(summary = "获取周访问趋势")
    @GetMapping("/weekly-visits")
    public Result<Map<String, Object>> getWeeklyVisits() {
        List<WeeklyVisits> list = weeklyVisitsMapper.selectList(null);

        Map<String, Object> data = new HashMap<>();
        data.put("xAxis", list.stream().map(WeeklyVisits::getWeekDay).toArray(String[]::new));
        data.put("visits", list.stream().map(WeeklyVisits::getVisits).toArray(Integer[]::new));
        data.put("pageViews", list.stream().map(WeeklyVisits::getPageViews).toArray(Integer[]::new));
        return Result.success(data);
    }

    @Operation(summary = "获取分类占比")
    @GetMapping("/category-ratio")
    public Result<Map<String, Object>> getCategoryRatio() {
        List<CategorySales> list = categorySalesMapper.selectList(null);

        Map<String, Object> data = new HashMap<>();
        data.put("categories", list.stream().map(CategorySales::getCategoryName).toArray(String[]::new));
        data.put("values", list.stream().map(CategorySales::getSalesValue).toArray(Integer[]::new));
        return Result.success(data);
    }

    @Operation(summary = "获取月度销售趋势")
    @GetMapping("/monthly-sales")
    public Result<Map<String, Object>> getMonthlySales() {
        QueryWrapper<MonthlySales> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("year_val", 2026).orderByAsc("id");
        List<MonthlySales> list = monthlySalesMapper.selectList(queryWrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("months", list.stream().map(MonthlySales::getMonthName).toArray(String[]::new));
        data.put("sales", list.stream().map(MonthlySales::getSales).toArray(Integer[]::new));
        data.put("target", list.stream().map(MonthlySales::getTarget).toArray(Integer[]::new));
        return Result.success(data);
    }
}
