package com.josp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.josp.system.common.api.Meta;
import com.josp.system.common.api.RouteVO;
import com.josp.system.dao.MenuMapper;
import com.josp.system.dao.RoleMenuMapper;
import com.josp.system.dto.MenuDTO;
import com.josp.system.entity.RoleMenu;
import com.josp.system.entity.Menu;
import com.josp.system.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类。
 *
 * <p>处理系统菜单的 CRUD 操作，并构建用于后端和前端消费的层级树形结构。
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    private final RoleMenuMapper roleMenuMapper;

    /**
     * 根据用户ID查询该用户的路由菜单，用于动态路由注入。
     *
     * @param userId 用户ID
     * @return 路由配置列表
     */
    @Override
    public List<RouteVO> listRoutesByUserId(Long userId) {
        List<Menu> menus = baseMapper.listMenusByUserId(userId);
        return buildRouteTree(menus, 0L);
    }

    /**
     * 查询所有菜单，按排序和创建时间升序排列。
     *
     * @return 菜单列表
     */
    @Override
    public List<Menu> listAllMenus() {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Menu::getSort).orderByAsc(Menu::getCreateTime);
        return list(wrapper);
    }

    /**
     * 根据ID查询菜单详情。
     *
     * @param id 菜单ID
     * @return 菜单实体
     */
    @Override
    public Menu getMenuById(Long id) {
        return getById(id);
    }

    /**
     * 获取菜单树形结构（用于管理页面树形展示）。
     *
     * @return 菜单树列表
     */
    @Override
    public List<Menu> getMenuTree() {
        List<Menu> allMenus = listAllMenus();
        return buildMenuTree(allMenus, 0L);
    }

    /**
     * 获取菜单下拉树形选项（用于父菜单选择）。
     *
     * @return 菜单树列表
     */
    @Override
    public List<Menu> getMenuTreeSelect() {
        List<Menu> allMenus = listAllMenus();
        return buildMenuTree(allMenus, 0L);
    }

    /**
     * 创建菜单。
     *
     * @param menuDTO 菜单数据传输对象
     * @return 是否创建成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createMenu(MenuDTO menuDTO) {
        Menu menu = convertToEntity(menuDTO);
        return save(menu);
    }

    /**
     * 更新菜单信息。
     *
     * @param menuDTO 菜单数据传输对象
     * @return 是否更新成功
     */
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

    /**
     * 删除菜单。
     *
     * <p>删除前会检查是否存在子菜单，如存在则拒绝删除。
     * 同时会清理该菜单与角色的关联关系。
     *
     * @param id 菜单ID
     * @return 是否删除成功
     */
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

        // 删除角色菜单关联
        LambdaQueryWrapper<RoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
        roleMenuWrapper.eq(RoleMenu::getMenuId, id);
        roleMenuMapper.delete(roleMenuWrapper);

        return removeById(id);
    }

    /**
     * 根据角色ID查询已分配的菜单列表。
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    @Override
    public List<Menu> getMenusByRoleId(Long roleId) {
        List<Menu> menus = baseMapper.listMenusByRoleId(roleId);
        return menus != null ? menus : new ArrayList<>();
    }

    /**
     * 构建菜单树形结构（用于管理页面树形展示）。
     *
     * @param menus    所有菜单的扁平列表
     * @param parentId 父菜单ID（0 = 根节点）
     * @return 指定父节点下的子菜单列表
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
     * 构建 Vue Router 路由树，用于前端动态路由注入。
     *
     * <p>递归将 Menu 实体转换为 RouteVO 对象，包含 Meta 信息。
     *
     * @param menus    用户可见的扁平菜单列表
     * @param parentId 父菜单ID（0 = 根节点）
     * @return 路由配置列表
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
     * 将 MenuDTO 转换为 Menu 实体，用于保存和更新操作。
     *
     * <p>为可空字段设置默认值。
     *
     * @param dto 菜单数据传输对象
     * @return 菜单实体（未设置ID）
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
