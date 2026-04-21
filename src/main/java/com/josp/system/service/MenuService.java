package com.josp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.josp.system.common.api.RouteVO;
import com.josp.system.dto.MenuDTO;
import com.josp.system.entity.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {

    /**
     * 获取当前用户的动态路由列表（根据用户角色）
     */
    List<RouteVO> listRoutesByUserId(Long userId);

    /**
     * 获取所有菜单列表（树形结构）
     */
    List<Menu> listAllMenus();

    /**
     * 根据ID获取菜单详情
     */
    Menu getMenuById(Long id);

    /**
     * 获取菜单树
     */
    List<Menu> getMenuTree();

    /**
     * 获取菜单树（用于前端选择）
     */
    List<Menu> getMenuTreeSelect();

    /**
     * 创建菜单
     */
    boolean createMenu(MenuDTO menuDTO);

    /**
     * 更新菜单
     */
    boolean updateMenu(MenuDTO menuDTO);

    /**
     * 删除菜单
     */
    boolean deleteMenu(Long id);

    /**
     * 根据角色ID获取菜单列表
     */
    List<Menu> getMenusByRoleId(Long roleId);
}
