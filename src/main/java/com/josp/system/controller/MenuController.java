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

/**
 * Menu Management Controller providing CRUD operations for system menus.
 * Handles menu hierarchy, routing configuration, and permission management.
 *
 * @author JOSP System
 * @version 1.0
 */
@Tag(name = "Menu Management")
@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * Gets route configuration for a specific user.
     *
     * @param userId user ID
     * @return list of route configurations
     */
    @Operation(summary = "Get user routes")
    @GetMapping("/routes")
    public Result<List<RouteVO>> getRoutes(@RequestParam Long userId) {
        List<RouteVO> routes = menuService.listRoutesByUserId(userId);
        return Result.success(routes);
    }

    /**
     * Gets menu list with optional type filter.
     *
     * @param type menu type filter (optional)
     * @return list of menus
     */
    @Operation(summary = "Get menu list")
    @GetMapping
    public Result<List<Map<String, Object>>> getMenuList(
            @Parameter(description = "Menu type") @RequestParam(required = false) Integer type
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

    /**
     * Gets menu options for dropdown/tree selection.
     *
     * @return hierarchical menu options
     */
    @Operation(summary = "Get menu dropdown options")
    @GetMapping("/options")
    public Result<List<Map<String, Object>>> getMenuOptions() {
        List<Menu> menus = menuService.listAllMenus();
        List<Map<String, Object>> options = buildMenuOptions(menus, 0L);
        return Result.success(options);
    }

    /**
     * Gets menu form data for editing.
     *
     * @param id menu ID
     * @return menu form data
     */
    @Operation(summary = "Get menu form data")
    @GetMapping("/{id}/form")
    public Result<Map<String, Object>> getMenuFormData(@PathVariable Long id) {
        Menu menu = menuService.getById(id);
        if (menu == null) {
            return Result.failed("Menu not found");
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

    /**
     * Creates a new menu.
     *
     * @param menuDTO menu data transfer object
     * @return creation result
     */
    @Operation(summary = "Create menu")
    @PostMapping
    public Result<Void> createMenu(@RequestBody MenuDTO menuDTO) {
        menuService.createMenu(menuDTO);
        return Result.success(null, "Created successfully");
    }

    /**
     * Updates an existing menu.
     *
     * @param id      menu ID
     * @param menuDTO menu data transfer object
     * @return update result
     */
    @Operation(summary = "Update menu")
    @PutMapping("/{id}")
    public Result<Void> updateMenu(@PathVariable Long id, @RequestBody MenuDTO menuDTO) {
        menuDTO.setId(id);
        menuService.updateMenu(menuDTO);
        return Result.success(null, "Updated successfully");
    }

    /**
     * Deletes a menu by ID.
     *
     * @param id menu ID
     * @return deletion result
     */
    @Operation(summary = "Delete menu")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.success(null, "Deleted successfully");
    }

    /**
     * Builds hierarchical menu options recursively.
     *
     * @param menus   all menus
     * @param parentId parent menu ID
     * @return hierarchical options list
     */
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
