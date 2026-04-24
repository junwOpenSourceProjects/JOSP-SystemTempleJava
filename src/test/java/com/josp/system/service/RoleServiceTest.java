package com.josp.system.service;

import com.josp.system.config.TestRedisConfig;
import com.josp.system.dao.RoleMapper;
import com.josp.system.dto.RoleDTO;
import com.josp.system.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RoleService 单元测试
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
@Transactional
class RoleServiceTest {

    @Autowired(required = false)
    private RoleMapper roleMapper;

    @Autowired
    private RoleService roleService;

    // ============================================
    // 辅助方法
    // ============================================

    private RoleDTO buildRoleDTO(String suffix) {
        RoleDTO dto = new RoleDTO();
        dto.setName("测试角色_" + suffix);
        dto.setCode("ROLE_T_" + suffix + "_" + System.currentTimeMillis());
        dto.setSort(0);
        dto.setStatus(1);
        dto.setRemark("测试备注");
        return dto;
    }

    // ============================================
    // CRUD 测试
    // ============================================

    @Test
    void testCreateRole() {
        RoleDTO dto = buildRoleDTO("create");

        boolean result = roleService.createRole(dto);

        assertTrue(result, "创建角色应返回 true");
        assertNotNull(dto.getId(), "DTO 的 ID 应在保存后回写");
    }

    @Test
    void testGetRoleById() {
        RoleDTO dto = buildRoleDTO("getbyid");
        roleService.createRole(dto);

        Role found = roleService.getRoleById(dto.getId());

        assertNotNull(found, "应能找到角色");
        assertEquals(dto.getName(), found.getName());
        assertEquals(dto.getCode(), found.getCode());
    }

    @Test
    void testGetRoleById_NotFound() {
        Role found = roleService.getRoleById(999999L);
        assertNull(found, "不存在的 ID 应返回 null");
    }

    @Test
    void testUpdateRole() {
        RoleDTO dto = buildRoleDTO("update");
        roleService.createRole(dto);

        dto.setName("修改后的角色名_" + System.currentTimeMillis());
        dto.setRemark("修改后的备注");
        boolean result = roleService.updateRole(dto);

        assertTrue(result, "更新应返回 true");

        Role updated = roleService.getRoleById(dto.getId());
        assertEquals("修改后的角色名_" + System.currentTimeMillis(), updated.getName());
    }

    @Test
    void testDeleteRole() {
        RoleDTO dto = buildRoleDTO("delete");
        roleService.createRole(dto);

        boolean result = roleService.deleteRole(dto.getId());

        assertTrue(result, "删除应返回 true");
        assertNull(roleService.getRoleById(dto.getId()));
    }

    @Test
    void testDeleteRole_NotFound() {
        assertThrows(RuntimeException.class, () -> roleService.deleteRole(999999L),
                "删除不存在的角色应抛出异常");
    }

    @Test
    void testListAllRoles() {
        roleService.createRole(buildRoleDTO("list1"));
        roleService.createRole(buildRoleDTO("list2"));

        List<Role> list = roleService.listAllRoles();

        assertNotNull(list);
        assertTrue(list.size() >= 2);
    }

    // ============================================
    // 菜单绑定测试
    // ============================================

    @Test
    void testAssignMenus() {
        RoleDTO dto = buildRoleDTO("assignmenus");
        roleService.createRole(dto);

        // 从测试数据中取一个已存在的菜单 ID
        Long menuId = 1879832672004342785L;

        boolean result = roleService.assignMenus(dto.getId(), Collections.singletonList(menuId));

        assertTrue(result, "分配菜单应返回 true");

        List<Long> menuIds = roleService.getMenuIdsByRoleId(dto.getId());
        assertTrue(menuIds.contains(menuId), "角色应包含分配的菜单 ID");
    }

    @Test
    void testAssignMenus_ReplaceExisting() {
        RoleDTO dto = buildRoleDTO("assignreplace");
        roleService.createRole(dto);

        Long menuId1 = 1879832672004342785L;
        Long menuId2 = 1879832672004342786L;

        roleService.assignMenus(dto.getId(), Collections.singletonList(menuId1));
        roleService.assignMenus(dto.getId(), Collections.singletonList(menuId2));

        List<Long> menuIds = roleService.getMenuIdsByRoleId(dto.getId());
        assertEquals(1, menuIds.size(), "重新分配后应只有 1 个菜单");
        assertTrue(menuIds.contains(menuId2));
    }

    @Test
    void testAssignMenus_ClearMenus() {
        RoleDTO dto = buildRoleDTO("clearmenus");
        roleService.createRole(dto);

        Long menuId = 1879832672004342785L;
        roleService.assignMenus(dto.getId(), Collections.singletonList(menuId));
        roleService.assignMenus(dto.getId(), Collections.emptyList());

        List<Long> menuIds = roleService.getMenuIdsByRoleId(dto.getId());
        assertTrue(menuIds.isEmpty(), "传入空列表应清空菜单权限");
    }

    @Test
    void testAssignMenus_NullId() {
        assertThrows(RuntimeException.class,
                () -> roleService.assignMenus(null, Collections.emptyList()),
                "roleId 为 null 应抛出异常");
    }

    @Test
    void testGetMenuIdsByRoleId() {
        RoleDTO dto = buildRoleDTO("menuids");
        roleService.createRole(dto);

        List<Long> menuIds = roleService.getMenuIdsByRoleId(dto.getId());
        assertNotNull(menuIds, "返回列表不应为 null");
    }

    // ============================================
    // 创建/更新时同时分配菜单
    // ============================================

    @Test
    void testCreateRole_WithMenuIds() {
        RoleDTO dto = buildRoleDTO("withmenus");
        dto.setMenuIds(Collections.singletonList(1879832672004342785L));

        boolean result = roleService.createRole(dto);

        assertTrue(result);
        List<Long> menuIds = roleService.getMenuIdsByRoleId(dto.getId());
        assertTrue(menuIds.contains(1879832672004342785L));
    }

    @Test
    void testUpdateRole_WithMenuIds() {
        RoleDTO dto = buildRoleDTO("updatemenu");
        dto.setMenuIds(null);
        roleService.createRole(dto);

        dto.setMenuIds(Collections.singletonList(1879832672004342785L));
        boolean result = roleService.updateRole(dto);

        assertTrue(result);
        List<Long> menuIds = roleService.getMenuIdsByRoleId(dto.getId());
        assertTrue(menuIds.contains(1879832672004342785L));
    }

    // ============================================
    // 异常/边界测试
    // ============================================

    @Test
    void testCreateRole_DuplicateCode() {
        RoleDTO dto1 = buildRoleDTO("dupcode");
        roleService.createRole(dto1);

        RoleDTO dto2 = buildRoleDTO("dupcode2");
        dto2.setCode(dto1.getCode());

        assertThrows(RuntimeException.class, () -> roleService.createRole(dto2),
                "重复编码应抛出异常");
    }

    @Test
    void testUpdateRole_NotFound() {
        RoleDTO dto = buildRoleDTO("updatenotfound");
        dto.setId(999999L);

        assertThrows(RuntimeException.class, () -> roleService.updateRole(dto),
                "更新不存在的角色应抛出异常");
    }

    @Test
    void testUpdateRole_DuplicateCode() {
        RoleDTO dto1 = buildRoleDTO("dupupdate1");
        roleService.createRole(dto1);

        RoleDTO dto2 = buildRoleDTO("dupupdate2");
        roleService.createRole(dto2);

        dto2.setCode(dto1.getCode());

        assertThrows(RuntimeException.class, () -> roleService.updateRole(dto2),
                "更新为重复编码应抛出异常");
    }

    @Test
    void testDeleteRole_WithMenuBindings() {
        RoleDTO dto = buildRoleDTO("deletewithmenus");
        dto.setMenuIds(Collections.singletonList(1879832672004342785L));
        roleService.createRole(dto);

        boolean result = roleService.deleteRole(dto.getId());

        assertTrue(result, "删除有菜单绑定的角色应成功");
        assertNull(roleService.getRoleById(dto.getId()));
    }
}
