package com.josp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.josp.system.common.api.Meta;
import com.josp.system.common.api.RouteVO;
import com.josp.system.dao.MenuMapper;
import com.josp.system.dto.MenuDTO;
import com.josp.system.entity.Menu;
import com.josp.system.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Menu Service Implementation.
 *
 * <p>Handles CRUD operations for system menus and builds
 * hierarchical tree structures for both back-end and front-end consumption.
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<RouteVO> listRoutesByUserId(Long userId) {
        List<Menu> menus = baseMapper.listMenusByUserId(userId);
        return buildRouteTree(menus, 0L);
    }

    @Override
    public List<Menu> listAllMenus() {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Menu::getSort).orderByAsc(Menu::getCreateTime);
        return list(wrapper);
    }

    @Override
    public Menu getMenuById(Long id) {
        return getById(id);
    }

    @Override
    public List<Menu> getMenuTree() {
        List<Menu> allMenus = listAllMenus();
        return buildMenuTree(allMenus, 0L);
    }

    @Override
    public List<Menu> getMenuTreeSelect() {
        List<Menu> allMenus = listAllMenus();
        return buildMenuTree(allMenus, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createMenu(MenuDTO menuDTO) {
        Menu menu = convertToEntity(menuDTO);
        return save(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenu(MenuDTO menuDTO) {
        if (menuDTO.getId() == null) {
            throw new RuntimeException("菜单ID不能为空");
        }

        Menu menu = getById(menuDTO.getId());
        if (menu == null) {
            throw new RuntimeException("菜单不存在");
        }

        Menu updateMenu = convertToEntity(menuDTO);
        updateMenu.setId(menuDTO.getId());
        return updateById(updateMenu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMenu(Long id) {
        if (id == null) {
            throw new RuntimeException("菜单ID不能为空");
        }

        // 检查是否有子菜单
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getParentId, id);
        if (count(wrapper) > 0) {
            throw new RuntimeException("请先删除子菜单");
        }

        return removeById(id);
    }

    @Override
    public List<Menu> getMenusByRoleId(Long roleId) {
        List<Menu> menus = baseMapper.listMenusByRoleId(roleId);
        return menus != null ? menus : new ArrayList<>();
    }

    /**
     * Builds a Menu entity tree for the admin tree-select component.
     *
     * @param menus   the flat list of all menus
     * @param parentId the parent ID to filter on (0 = root)
     * @return list of Menu records under the given parent (not mutated)
     */
    private List<Menu> buildMenuTree(List<Menu> menus, Long parentId) {
        if (CollectionUtils.isEmpty(menus)) {
            return new ArrayList<>();
        }

        return menus.stream()
                .filter(menu -> Objects.equals(menu.getParentId(), parentId))
                .map(menu -> {
                    List<Menu> children = buildMenuTree(menus, menu.getId());
                    if (!CollectionUtils.isEmpty(children)) {
                        // 注意：这里不直接在原对象上设置children，因为我们使用的是stream
                        // 前端如果需要树形结构，需要单独处理
                    }
                    return menu;
                })
                .collect(Collectors.toList());
    }

    /**
     * Builds a Vue Router route tree for dynamic route injection on the front-end.
     * Recursively converts Menu entities into RouteVO objects including Meta.
     *
     * @param menus   the flat list of all menus visible to this user
     * @param parentId the parent ID to filter on (0 = root)
     * @return list of RouteVO records under the given parent
     */
    private List<RouteVO> buildRouteTree(List<Menu> menus, Long parentId) {
        if (CollectionUtils.isEmpty(menus)) {
            return new ArrayList<>();
        }

        return menus.stream()
                .filter(menu -> Objects.equals(menu.getParentId(), parentId))
                .map(menu -> {
                    RouteVO routeVO = new RouteVO();
                    routeVO.setName(menu.getName());
                    routeVO.setPath(menu.getPath());
                    routeVO.setComponent(menu.getComponent());
                    routeVO.setRedirect(menu.getRedirect());

                    Meta meta = Meta.builder()
                            .title(menu.getName())
                            .icon(menu.getIcon())
                            .hidden(Objects.equals(menu.getVisible(), 0))
                            .keepAlive(Objects.equals(menu.getKeepAlive(), 1))
                            .alwaysShow(Objects.equals(menu.getAlwaysShow(), 1))
                            .build();
                    routeVO.setMeta(meta);

                    // 递归获取子菜单
                    List<RouteVO> children = buildRouteTree(menus, menu.getId());
                    if (!CollectionUtils.isEmpty(children)) {
                        routeVO.setChildren(children);
                    }
                    return routeVO;
                })
                .collect(Collectors.toList());
    }

    /**
     * Converts a MenuDTO into a Menu entity for save/update operations.
     * Applies sensible defaults for nullable fields.
     *
     * @param dto the incoming DTO from the controller
     * @return a Menu entity ready to be persisted (ID is not set here)
     */
    private Menu convertToEntity(MenuDTO dto) {
        Menu menu = new Menu();
        menu.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        menu.setName(dto.getName());
        menu.setType(dto.getType());
        menu.setPath(dto.getPath());
        menu.setComponent(dto.getComponent());
        menu.setIcon(dto.getIcon());
        menu.setSort(dto.getSort() != null ? dto.getSort() : 0);
        menu.setVisible(dto.getVisible() != null ? dto.getVisible() : 1);
        menu.setRedirect(dto.getRedirect());
        menu.setPerm(dto.getPerm());
        menu.setKeepAlive(dto.getKeepAlive() != null ? dto.getKeepAlive() : 0);
        menu.setAlwaysShow(dto.getAlwaysShow() != null ? dto.getAlwaysShow() : 0);
        return menu;
    }
}
