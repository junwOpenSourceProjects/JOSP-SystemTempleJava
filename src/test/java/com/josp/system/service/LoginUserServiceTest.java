package com.josp.system.service;

import com.josp.system.config.TestRedisConfig;
import com.josp.system.dao.AccountRoleMapper;
import com.josp.system.dao.RoleMapper;
import com.josp.system.dao.LoginUserMapper;
import com.josp.system.entity.AccountRole;
import com.josp.system.entity.LoginUser;
import com.josp.system.entity.Role;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LoginUserService 单元测试
 *
 * LoginUserService 本身是 @Service 类（继承 ServiceImpl），不是接口。
 * JDK 25 Byte Buddy 无法 mock MyBatis-Plus BaseMapper，改用 @SpringBootTest + H2。
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
@Transactional
class LoginUserServiceTest {

    @Autowired(required = false)
    private LoginUserMapper loginUserMapper;

    @Autowired(required = false)
    private AccountRoleMapper accountRoleMapper;

    @Autowired(required = false)
    private RoleMapper roleMapper;

    @Autowired
    private LoginUserService loginUserService;

    // ============================================
    // 辅助方法
    // ============================================

    private LoginUser buildTestUser(String suffix) {
        LoginUser user = new LoginUser();
        user.setUsername("u_" + suffix + "_" + System.currentTimeMillis());
        user.setPassword("password123");
        user.setName("测试用户_" + suffix);
        user.setPhone("13800000000");
        user.setSex("1");
        user.setIdNumber("3200000000000000" + suffix);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return user;
    }

    // ============================================
    // CRUD 测试
    // ============================================

    @Test
    void testSave() {
        LoginUser user = buildTestUser("save");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        boolean result = loginUserService.save(user);

        assertTrue(result, "保存用户应返回 true");
        assertNotNull(user.getId(), "ID 应在保存后回写");
    }

    @Test
    void testGetByUsername() {
        LoginUser user = buildTestUser("getbyusername");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        loginUserService.save(user);

        LoginUser found = loginUserService.getByUsername(user.getUsername());

        assertNotNull(found, "应能通过用户名找到用户");
        assertEquals(user.getUsername(), found.getUsername());
    }

    @Test
    void testGetByUsername_NotFound() {
        LoginUser found = loginUserService.getByUsername("nonexistent_xxxxx_user");
        assertNull(found, "不存在的用户名应返回 null");
    }

    @Test
    void testBatchInsert() {
        LoginUser user1 = buildTestUser("batch1");
        user1.setCreateTime(LocalDateTime.now());
        user1.setUpdateTime(LocalDateTime.now());
        LoginUser user2 = buildTestUser("batch2");
        user2.setCreateTime(LocalDateTime.now());
        user2.setUpdateTime(LocalDateTime.now());
        List<LoginUser> users = Arrays.asList(user1, user2);

        int result = loginUserService.batchInsert(users);

        assertTrue(result > 0, "批量插入应返回受影响行数");
        assertNotNull(user1.getId(), "批量插入后 ID 应回写");
    }

    @Test
    void testBatchInsertSelective() {
        LoginUser user1 = buildTestUser("batchsel1");
        user1.setCreateTime(LocalDateTime.now());
        user1.setUpdateTime(LocalDateTime.now());
        LoginUser user2 = buildTestUser("batchsel2");
        user2.setCreateTime(LocalDateTime.now());
        user2.setUpdateTime(LocalDateTime.now());
        List<LoginUser> users = Arrays.asList(user1, user2);

        int result = loginUserService.batchInsert(users);

        assertTrue(result > 0, "批量插入应返回受影响行数");
    }

    @Test
    void testUpdateBatchSelective() {
        LoginUser user1 = buildTestUser("updbatch1");
        user1.setCreateTime(LocalDateTime.now());
        user1.setUpdateTime(LocalDateTime.now());
        loginUserService.batchInsert(Collections.singletonList(user1));

        user1.setName("批量更新后名字_" + System.currentTimeMillis());
        int result = loginUserService.updateBatchSelective(Collections.singletonList(user1));

        assertTrue(result > 0, "批量更新应返回受影响行数");
    }

    @Test
    void testGetPage() {
        for (int i = 0; i < 3; i++) {
            LoginUser u = buildTestUser("page_" + i);
            u.setCreateTime(LocalDateTime.now());
            u.setUpdateTime(LocalDateTime.now());
            loginUserService.save(u);
        }

        Page<LoginUser> pageParam = new Page<>(1, 10);
        IPage<LoginUser> result = loginUserService.getPage(pageParam, null);

        assertNotNull(result, "分页结果不应为 null");
        assertTrue(result.getTotal() >= 3, "总记录数应 >= 3");
    }

    @Test
    void testUpdateBatch() {
        LoginUser user = buildTestUser("updbatch");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        loginUserService.batchInsert(Collections.singletonList(user));

        user.setName("update_batch_name_" + System.currentTimeMillis());
        int result = loginUserService.updateBatch(Collections.singletonList(user));

        assertTrue(result > 0, "批量更新应返回受影响行数");
    }

    // ============================================
    // 角色绑定测试
    // ============================================

    @Test
    void testBindRoles() {
        LoginUser user = buildTestUser("bindroles");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        loginUserService.save(user);

        // 创建一个角色
        Role role = new Role();
        role.setName("测试角色_bind_" + System.currentTimeMillis());
        role.setCode("ROLE_BIND_" + System.currentTimeMillis());
        role.setSort(0);
        role.setStatus(1);
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.insert(role);

        loginUserService.bindRoles(user.getId(), Collections.singletonList(role.getId()));

        List<Long> roleIds = loginUserService.getRoleIdsByUserId(user.getId());
        assertTrue(roleIds.contains(role.getId()), "应包含绑定的角色 ID");
    }

    @Test
    void testBindRoles_ClearExisting() {
        LoginUser user = buildTestUser("bindclear");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        loginUserService.save(user);

        Role role1 = new Role();
        role1.setName("角色1_" + System.currentTimeMillis());
        role1.setCode("R1_" + System.currentTimeMillis());
        role1.setSort(0);
        role1.setStatus(1);
        role1.setCreateTime(LocalDateTime.now());
        role1.setUpdateTime(LocalDateTime.now());
        roleMapper.insert(role1);

        Role role2 = new Role();
        role2.setName("角色2_" + System.currentTimeMillis());
        role2.setCode("R2_" + System.currentTimeMillis());
        role2.setSort(0);
        role2.setStatus(1);
        role2.setCreateTime(LocalDateTime.now());
        role2.setUpdateTime(LocalDateTime.now());
        roleMapper.insert(role2);

        // 先绑定 role1，再绑定 role2
        loginUserService.bindRoles(user.getId(), Collections.singletonList(role1.getId()));
        loginUserService.bindRoles(user.getId(), Collections.singletonList(role2.getId()));

        List<Long> roleIds = loginUserService.getRoleIdsByUserId(user.getId());
        assertEquals(1, roleIds.size(), "重新绑定后应只有 1 个角色");
        assertTrue(roleIds.contains(role2.getId()), "应包含新绑定的角色");
    }

    @Test
    void testBindRoles_EmptyList() {
        LoginUser user = buildTestUser("bindempty");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        loginUserService.save(user);

        Role role = new Role();
        role.setName("待清空角色_" + System.currentTimeMillis());
        role.setCode("ROLE_CLEAR_" + System.currentTimeMillis());
        role.setSort(0);
        role.setStatus(1);
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.insert(role);

        loginUserService.bindRoles(user.getId(), Collections.singletonList(role.getId()));
        loginUserService.bindRoles(user.getId(), Collections.emptyList());

        List<Long> roleIds = loginUserService.getRoleIdsByUserId(user.getId());
        assertTrue(roleIds.isEmpty(), "传入空列表应清空角色");
    }

    @Test
    void testGetRoleIdsByUserId_NoRoles() {
        LoginUser user = buildTestUser("noroles");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        loginUserService.save(user);

        List<Long> roleIds = loginUserService.getRoleIdsByUserId(user.getId());
        assertNotNull(roleIds, "未绑定角色应返回空列表而非 null");
        assertTrue(roleIds.isEmpty());
    }

    @Test
    void testUnbindRoles() {
        LoginUser user = buildTestUser("unbind");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        loginUserService.save(user);

        Role role = new Role();
        role.setName("待解绑角色_" + System.currentTimeMillis());
        role.setCode("ROLE_UNBIND_" + System.currentTimeMillis());
        role.setSort(0);
        role.setStatus(1);
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.insert(role);

        loginUserService.bindRoles(user.getId(), Collections.singletonList(role.getId()));
        loginUserService.unbindRoles(user.getId());

        List<Long> roleIds = loginUserService.getRoleIdsByUserId(user.getId());
        assertTrue(roleIds.isEmpty(), "解绑后应无角色");
    }

    // ============================================
    // insertOrUpdate 测试
    // ============================================

    @Test
    void testInsertOrUpdate() {
        LoginUser user = buildTestUser("insupd");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        loginUserService.save(user);

        user.setName("insertOrUpdate_修改_" + System.currentTimeMillis());
        int result = loginUserService.insertOrUpdate(user);

        assertTrue(result > 0, "insertOrUpdate 应返回受影响行数");
    }

    // ============================================
    // 异常/边界测试
    // ============================================

    @Test
    void testSave_DuplicateUsername() {
        LoginUser user1 = buildTestUser("dup");
        user1.setCreateTime(LocalDateTime.now());
        user1.setUpdateTime(LocalDateTime.now());
        loginUserService.save(user1);

        LoginUser user2 = buildTestUser("dup");
        user2.setUsername(user1.getUsername());
        user2.setCreateTime(LocalDateTime.now());
        user2.setUpdateTime(LocalDateTime.now());
        assertThrows(Exception.class, () -> loginUserService.save(user2),
                "重复用户名应抛出异常");
    }

    @Test
    void testRemoveById() {
        LoginUser user = buildTestUser("remove");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        loginUserService.save(user);

        boolean result = loginUserService.removeById(user.getId());

        assertTrue(result, "删除应返回 true");
    }
}
