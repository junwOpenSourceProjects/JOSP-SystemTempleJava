package com.josp.system.controller;

import com.josp.system.common.api.Result;
import com.josp.system.common.api.RouteVO;
import com.josp.system.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单接口")
@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "获取路由列表")
    @GetMapping("/routes")
    public Result<List<RouteVO>> getRoutes(@RequestParam Long userId) {
        List<RouteVO> routes = menuService.listRoutesByUserId(userId);
        return Result.success(routes);
    }
}
