package com.josp.system.service;

import com.josp.system.dao.RoleMapper;
import com.josp.system.dao.RoleMenuMapper;
import com.josp.system.dto.RoleDTO;
import com.josp.system.entity.Role;
import com.josp.system.service.impl.RoleServiceImpl;
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
@DisplayName("RoleService 单元测试")
class RoleServiceTest {

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private RoleMenuMapper roleMenuMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role testRole;
    private RoleDTO testRoleDTO;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setName("管理员");
        testRole.setCode("admin");
        testRole.setSort(1);
        testRole.setStatus(1);
        testRole.setRemark("管理员角色");
        testRole.setCreateTime(LocalDateTime.now());

        testRoleDTO = new RoleDTO();
        testRoleDTO.setName("管理员");
        testRoleDTO.setCode("admin");
        testRoleDTO.setSort(1);
        testRoleDTO.setStatus(1);
    }

    @Test
    @DisplayName("获取所有角色列表")
    void testListAllRoles() {
        List<Role> roles = Arrays.asList(testRole);
        when(roleMapper.selectList(any())).thenReturn(roles);

        List<Role> result = roleService.listAllRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getCode());
    }

    @Test
    @DisplayName("根据ID获取角色详情")
    void testGetRoleById() {
        when(roleMapper.selectById(1L)).thenReturn(testRole);

        Role result = roleService.getRoleById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("管理员", result.getName());
    }

    @Test
    @DisplayName("创建角色 - 成功")
    void testCreateRole_Success() {
        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenReturn(1);

        testRoleDTO.setName("新角色");
        boolean result = roleService.createRole(testRoleDTO);

        assertTrue(result);
        verify(roleMapper).insert(any(Role.class));
    }

    @Test
    @DisplayName("创建角色 - 编码已存在")
    void testCreateRole_CodeExists() {
        when(roleMapper.selectCount(any())).thenReturn(1L);

        assertThrows(RuntimeException.class, () -> roleService.createRole(testRoleDTO));
    }

    @Test
    @DisplayName("创建角色并分配菜单")
    void testCreateRole_WithMenus() {
        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenReturn(1);
        doNothing().when(roleMenuMapper).deleteByRoleId(anyLong());
        doNothing().when(roleMenuMapper).batchInsert(anyLong(), any());

        testRoleDTO.setMenuIds(Arrays.asList(1L, 2L, 3L));
        boolean result = roleService.createRole(testRoleDTO);

        assertTrue(result);
        verify(roleMenuMapper).deleteByRoleId(anyLong());
        verify(roleMenuMapper).batchInsert(anyLong(), any());
    }

    @Test
    @DisplayName("更新角色 - 成功")
    void testUpdateRole_Success() {
        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        testRoleDTO.setId(1L);
        testRoleDTO.setName("更新后的角色");
        boolean result = roleService.updateRole(testRoleDTO);

        assertTrue(result);
        verify(roleMapper).updateById(any(Role.class));
    }

    @Test
    @DisplayName("更新角色 - 角色不存在")
    void testUpdateRole_NotFound() {
        when(roleMapper.selectById(1L)).thenReturn(null);

        testRoleDTO.setId(1L);
        assertThrows(RuntimeException.class, () -> roleService.updateRole(testRoleDTO));
    }

    @Test
    @DisplayName("更新角色 - ID为空")
    void testUpdateRole_NullId() {
        assertThrows(RuntimeException.class, () -> roleService.updateRole(testRoleDTO));
    }

    @Test
    @DisplayName("删除角色 - 成功")
    void testDeleteRole_Success() {
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(roleMapper.deleteById(1L)).thenReturn(1);
        doNothing().when(roleMenuMapper).deleteByRoleId(anyLong());

        boolean result = roleService.deleteRole(1L);

        assertTrue(result);
        verify(roleMenuMapper).deleteByRoleId(1L);
        verify(roleMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除角色 - ID为空")
    void testDeleteRole_NullId() {
        assertThrows(RuntimeException.class, () -> roleService.deleteRole(null));
    }

    @Test
    @DisplayName("根据角色ID获取菜单ID列表")
    void testGetMenuIdsByRoleId() {
        when(roleMenuMapper.selectMenuIdsByRoleId(1L)).thenReturn(Arrays.asList(1L, 2L, 3L));

        List<Long> result = roleService.getMenuIdsByRoleId(1L);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("根据角色ID获取菜单ID列表 - 返回空列表")
    void testGetMenuIdsByRoleId_Empty() {
        when(roleMenuMapper.selectMenuIdsByRoleId(1L)).thenReturn(null);

        List<Long> result = roleService.getMenuIdsByRoleId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("分配菜单权限")
    void testAssignMenus() {
        doNothing().when(roleMenuMapper).deleteByRoleId(1L);
        doNothing().when(roleMenuMapper).batchInsert(anyLong(), any());

        List<Long> menuIds = Arrays.asList(1L, 2L);
        boolean result = roleService.assignMenus(1L, menuIds);

        assertTrue(result);
        verify(roleMenuMapper).deleteByRoleId(1L);
        verify(roleMenuMapper).batchInsert(1L, menuIds);
    }

    @Test
    @DisplayName("分配菜单权限 - 角色ID为空")
    void testAssignMenus_NullRoleId() {
        assertThrows(RuntimeException.class, () -> roleService.assignMenus(null, Arrays.asList(1L)));
    }

    @Test
    @DisplayName("分配菜单权限 - 空菜单列表")
    void testAssignMenus_EmptyMenus() {
        doNothing().when(roleMenuMapper).deleteByRoleId(1L);

        boolean result = roleService.assignMenus(1L, null);

        assertTrue(result);
        verify(roleMenuMapper).deleteByRoleId(1L);
        verify(roleMenuMapper, never()).batchInsert(anyLong(), any());
    }
}