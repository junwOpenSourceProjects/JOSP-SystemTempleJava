package com.josp.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.josp.system.common.api.ChartVO;
import com.josp.system.common.api.DashboardStatisticsVO;
import com.josp.system.common.api.Result;
import com.josp.system.entity.LoginUser;
import com.josp.system.service.LoginUserService;
import com.josp.system.service.MenuService;
import com.josp.system.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "仪表盘接口")
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final LoginUserService loginUserService;
    private final MenuService menuService;
    private final RoleService roleService;

    @Operation(summary = "获取统计数据")
    @GetMapping("/stats")
    public Result<DashboardStatisticsVO> getStats() {
        DashboardStatisticsVO stats = DashboardStatisticsVO.builder()
                .userCount(loginUserService.count())
                .menuCount(menuService.count())
                .roleCount(roleService.count())
                .loginCount(1024L) // 模拟登录量
                .build();
        return Result.success(stats);
    }

    @Operation(summary = "获取饼图数据 (用户性别分布)")
    @GetMapping("/chart/pie")
    public Result<ChartVO> getPieChart() {
        List<LoginUser> users = loginUserService.list();
        // 1:男, 2:女, 其他:未知
        Map<String, Long> sexMap = users.stream()
                .collect(Collectors.groupingBy(u -> {
                    if ("1".equals(u.getSex())) return "男";
                    if ("2".equals(u.getSex())) return "女";
                    return "未知";
                }, Collectors.counting()));

        List<Object> data = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        sexMap.forEach((k, v) -> {
            labels.add(k);
            data.add(new PieData(v, k));
        });

        ChartVO chart = ChartVO.builder()
                .labels(labels)
                .series(Collections.singletonList(
                        ChartVO.Series.builder()
                                .name("用户性别分布")
                                .type("pie")
                                .data(data)
                                .build()
                ))
                .build();
        return Result.success(chart);
    }

    @Operation(summary = "获取柱状图数据 (菜单类型分布)")
    @GetMapping("/chart/bar")
    public Result<ChartVO> getBarChart() {
        // 简单统计不同类型的菜单数量
        long dirCount = menuService.count(new LambdaQueryWrapper<com.josp.system.entity.Menu>().eq(com.josp.system.entity.Menu::getType, 1));
        long menuCount = menuService.count(new LambdaQueryWrapper<com.josp.system.entity.Menu>().eq(com.josp.system.entity.Menu::getType, 2));
        long btnCount = menuService.count(new LambdaQueryWrapper<com.josp.system.entity.Menu>().eq(com.josp.system.entity.Menu::getType, 3));

        ChartVO chart = ChartVO.builder()
                .labels(Arrays.asList("目录", "菜单", "按钮"))
                .series(Arrays.asList(
                    ChartVO.Series.builder()
                        .name("菜单统计")
                        .type("bar")
                        .data(Arrays.asList(dirCount, menuCount, btnCount))
                        .build()
                ))
                .build();
        return Result.success(chart);
    }

    private static class PieData {
        public Long value;
        public String name;
        public PieData(Long value, String name) {
            this.value = value;
            this.name = name;
        }
    }
}

