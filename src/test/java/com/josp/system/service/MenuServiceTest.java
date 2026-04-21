package com.josp.system.service;

import com.josp.system.common.api.RouteVO;
import com.josp.system.dao.MenuMapper;
import com.josp.system.dto.MenuDTO;
import com.josp.system.entity.Menu;
import com.josp.system.service.impl.MenuServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MenuService 单元测试")
class MenuServiceTest {

    @Mock
    private MenuMapper menuMapper;

    @InjectMocks
    private MenuServiceImpl menuService;

    private Menu testMenu;
    private MenuDTO testMenuDTO;

    @BeforeEach
    void setUp() {
        testMenu = new Menu();
        testMenu.setId(1L);
        testMenu.setParentId(0L);
        testMenu.setName("系统管理");
        testMenu.setType(0);
        testMenu.setPath("/system");
        testMenu.setComponent("Layout");
        testMenu.setIcon("setting");
        testMenu.setSort(1);
        testMenu.setVisible(1);
        testMenu.setKeepAlive(0);
        testMenu.setAlwaysShow(1);
        testMenu.setCreateTime(LocalDateTime.now());

        testMenuDTO = new MenuDTO();
        testMenuDTO.setName("系统管理");
        testMenuDTO.setType(0);
        testMenuDTO.setPath("/system");
        testMenuDTO.setComponent("Layout");
        testMenuDTO.setIcon("setting");
        testMenuDTO.setSort(1);
    }

    @Test
    @DisplayName("获取当前用户的动态路由列表")
    void testListRoutesByUserId() {
        List<Menu> menus = Arrays.asList(testMenu);
        when(menuMapper.listMenusByUserId(1L)).thenReturn(menus);

        List<RouteVO> result = menuService.listRoutesByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("系统管理", result.get(0).getName());
    }

    @Test
    @DisplayName("获取所有菜单列表")
    void testListAllMenus() {
        List<Menu> menus = Arrays.asList(testMenu);
        when(menuMapper.selectList(any())).thenReturn(menus);

        List<Menu> result = menuService.listAllMenus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("根据ID获取菜单详情")
    void testGetMenuById() {
        when(menuMapper.selectById(1L)).thenReturn(testMenu);

        Menu result = menuService.getMenuById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("获取菜单树")
    void testGetMenuTree() {
        Menu childMenu = new Menu();
        childMenu.setId(2L);
        childMenu.setParentId(1L);
        childMenu.setName("用户管理");
        childMenu.setType(1);

        List<Menu> menus = Arrays.asList(testMenu, childMenu);
        when(menuMapper.selectList(any())).thenReturn(menus);

        List<Menu> result = menuService.getMenuTree();

        assertNotNull(result);
    }

    @Test
    @DisplayName("获取菜单树选择")
    void testGetMenuTreeSelect() {
        List<Menu> menus = Arrays.asList(testMenu);
        when(menuMapper.selectList(any())).thenReturn(menus);

        List<Menu> result = menuService.getMenuTreeSelect();

        assertNotNull(result);
    }

    @Test
    @DisplayName("创建菜单 - 成功")
    void testCreateMenu_Success() {
        when(menuMapper.insert(any(Menu.class))).thenReturn(1);

        boolean result = menuService.createMenu(testMenuDTO);

        assertTrue(result);
        verify(menuMapper).insert(any(Menu.class));
    }

    @Test
    @DisplayName("更新菜单 - 成功")
    void testUpdateMenu_Success() {
        when(menuMapper.selectById(1L)).thenReturn(testMenu);
        when(menuMapper.updateById(any(Menu.class))).thenReturn(1);

        testMenuDTO.setId(1L);
        boolean result = menuService.updateMenu(testMenuDTO);

        assertTrue(result);
        verify(menuMapper).updateById(any(Menu.class));
    }

    @Test
    @DisplayName("更新菜单 - 菜单不存在")
    void testUpdateMenu_NotFound() {
        when(menuMapper.selectById(1L)).thenReturn(null);

        testMenuDTO.setId(1L);
        assertThrows(RuntimeException.class, () -> menuService.updateMenu(testMenuDTO));
    }

    @Test
    @DisplayName("更新菜单 - ID为空")
    void testUpdateMenu_NullId() {
        assertThrows(RuntimeException.class, () -> menuService.updateMenu(testMenuDTO));
    }

    @Test
    @DisplayName("删除菜单 - 成功")
    void testDeleteMenu_Success() {
        when(menuMapper.selectById(1L)).thenReturn(testMenu);
        when(menuMapper.selectCount(any())).thenReturn(0L);
        when(menuMapper.deleteById(1L)).thenReturn(1);

        boolean result = menuService.deleteMenu(1L);

        assertTrue(result);
        verify(menuMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除菜单 - 有子菜单")
    void testDeleteMenu_HasChildren() {
        Menu childMenu = new Menu();
        childMenu.setParentId(1L);

        when(menuMapper.selectById(1L)).thenReturn(testMenu);
        when(menuMapper.selectCount(any())).thenReturn(1L);

        assertThrows(RuntimeException.class, () -> menuService.deleteMenu(1L));
    }

    @Test
    @DisplayName("删除菜单 - ID为空")
    void testDeleteMenu_NullId() {
        assertThrows(RuntimeException.class, () -> menuService.deleteMenu(null));
    }

    @Test
    @DisplayName("根据角色ID获取菜单列表")
    void testGetMenusByRoleId() {
        List<Menu> menus = Arrays.asList(testMenu);
        when(menuMapper.listMenusByRoleId(1L)).thenReturn(menus);

        List<Menu> result = menuService.getMenusByRoleId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("根据角色ID获取菜单列表 - 返回空")
    void testGetMenusByRoleId_Empty() {
        when(menuMapper.listMenusByRoleId(1L)).thenReturn(null);

        List<Menu> result = menuService.getMenusByRoleId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}