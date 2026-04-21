package com.josp.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.josp.system.common.api.Result;
import com.josp.system.common.api.RouteVO;
import com.josp.system.dto.MenuDTO;
import com.josp.system.entity.Menu;
import com.josp.system.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Operation(summary = "获取菜单列表")
    @GetMapping
    public Result<List<Map<String, Object>>> getMenuList(
            @Parameter(description = "菜单类型") @RequestParam(required = false) Integer type
    ) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(Menu::getType, type);
        }
        wrapper.orderByAsc(Menu::getSort).orderByAsc(Menu::getCreateTime);
        List<Menu> menus = menuService.list(wrapper);

        List<Map<String, Object>> result = menus.stream().map(menu -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", menu.getId());
            map.put("parentId", menu.getParentId());
            map.put("name", menu.getName());
            map.put("type", menu.getType());
            map.put("path", menu.getPath());
            map.put("component", menu.getComponent());
            map.put("icon", menu.getIcon());
            map.put("sort", menu.getSort());
            map.put("visible", menu.getVisible());
            map.put("redirect", menu.getRedirect());
            map.put("perm", menu.getPerm());
            map.put("keepAlive", menu.getKeepAlive());
            map.put("alwaysShow", menu.getAlwaysShow());
            map.put("createTime", menu.getCreateTime());
            map.put("updateTime", menu.getUpdateTime());
            return map;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    @Operation(summary = "获取菜单下拉选项")
    @GetMapping("/options")
    public Result<List<Map<String, Object>>> getMenuOptions() {
        List<Menu> menus = menuService.listAllMenus();
        List<Map<String, Object>> options = buildMenuOptions(menus, 0L);
        return Result.success(options);
    }

    @Operation(summary = "获取菜单表单数据")
    @GetMapping("/{id}/form")
    public Result<Map<String, Object>> getMenuFormData(@PathVariable Long id) {
        Menu menu = menuService.getById(id);
        if (menu == null) {
            return Result.failed("菜单不存在");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", menu.getId());
        map.put("parentId", menu.getParentId());
        map.put("name", menu.getName());
        map.put("type", menu.getType());
        map.put("path", menu.getPath());
        map.put("component", menu.getComponent());
        map.put("icon", menu.getIcon());
        map.put("sort", menu.getSort());
        map.put("visible", menu.getVisible());
        map.put("redirect", menu.getRedirect());
        map.put("perm", menu.getPerm());
        map.put("keepAlive", menu.getKeepAlive());
        map.put("alwaysShow", menu.getAlwaysShow());
        return Result.success(map);
    }

    @Operation(summary = "创建菜单")
    @PostMapping
    public Result<Void> createMenu(@RequestBody MenuDTO menuDTO) {
        menuService.createMenu(menuDTO);
        return Result.success(null, "创建成功");
    }

    @Operation(summary = "更新菜单")
    @PutMapping("/{id}")
    public Result<Void> updateMenu(@PathVariable Long id, @RequestBody MenuDTO menuDTO) {
        menuDTO.setId(id);
        menuService.updateMenu(menuDTO);
        return Result.success(null, "更新成功");
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.success(null, "删除成功");
    }

    private List<Map<String, Object>> buildMenuOptions(List<Menu> menus, Long parentId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Menu menu : menus) {
            if (Objects.equals(menu.getParentId(), parentId)) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", menu.getId());
                map.put("label", menu.getName());
                List<Map<String, Object>> children = buildMenuOptions(menus, menu.getId());
                if (!children.isEmpty()) {
                    map.put("children", children);
                }
                result.add(map);
            }
        }
        return result;
    }
}
