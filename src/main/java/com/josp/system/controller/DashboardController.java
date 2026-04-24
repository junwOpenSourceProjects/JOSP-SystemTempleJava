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

/**
 * Dashboard / Home Page API Controller.
 *
 * <p>Provides statistical data for the admin dashboard overview:
 * <ul>
 *   <li>Stat cards (users, orders, revenue, visits)</li>
 *   <li>Weekly visit trend (line chart)</li>
 *   <li>Category sales ratio (pie chart)</li>
 *   <li>Monthly sales vs. target (bar chart)</li>
 * </ul>
 *
 * <p>All endpoints are public (no authentication required).
 * Fallback mock data is returned when no database records exist.
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Tag(name = "Dashboard")
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DailyStatsMapper dailyStatsMapper;
    private final WeeklyVisitsMapper weeklyVisitsMapper;
    private final CategorySalesMapper categorySalesMapper;
    private final MonthlySalesMapper monthlySalesMapper;

    /**
     * Returns the aggregate stat cards for the dashboard.
     * When no database record exists, returns hardcoded fallback mock data.
     * Growth rate is calculated as: (todayVisits - yesterdayVisits) / yesterdayVisits * 100.
     *
     * @return Result containing totalUsers, totalOrders, totalRevenue, todayVisits, yesterdayVisits, growthRate
     */
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
            // Growth rate = (todayVisits - yesterdayVisits) / yesterdayVisits * 100
            if (stats.getYesterdayVisits() != null && stats.getYesterdayVisits() > 0) {
                double rate = (stats.getTodayVisits() - stats.getYesterdayVisits()) * 100.0 / stats.getYesterdayVisits();
                data.put("growthRate", Math.round(rate * 10) / 10.0);
            } else {
                data.put("growthRate", 0);
            }
        } else {
            // Fallback mock data when no DB record exists
            data.put("totalUsers", 10284);
            data.put("totalOrders", 3856);
            data.put("totalRevenue", new BigDecimal("1285600"));
            data.put("todayVisits", 2345);
            data.put("yesterdayVisits", 2187);
            data.put("growthRate", 12.5);
        }
        return Result.success(data);
    }

    /**
     * Returns the 7-day visit and page-view trend for the line chart.
     *
     * @return Result containing xAxis (day names), visits (int array), pageViews (int array)
     */
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

    /**
     * Returns category-wise sales figures for the pie chart.
     *
     * @return Result containing categories (name array), values (int array)
     */
    @Operation(summary = "获取分类占比")
    @GetMapping("/category-ratio")
    public Result<Map<String, Object>> getCategoryRatio() {
        List<CategorySales> list = categorySalesMapper.selectList(null);

        Map<String, Object> data = new HashMap<>();
        data.put("categories", list.stream().map(CategorySales::getCategoryName).toArray(String[]::new));
        data.put("values", list.stream().map(CategorySales::getSalesValue).toArray(Integer[]::new));
        return Result.success(data);
    }

    /**
     * Returns monthly actual sales vs. targets for the bar chart.
     * Data is filtered by yearVal = 2026.
     *
     * @return Result containing months (name array), sales (int array), target (int array)
     */
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
