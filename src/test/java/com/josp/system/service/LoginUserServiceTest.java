package com.josp.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.josp.system.dao.LoginUserMapper;
import com.josp.system.entity.LoginUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("LoginUserService 单元测试")
class LoginUserServiceTest {

    @Mock
    private LoginUserMapper loginUserMapper;

    private LoginUserService loginUserService;

    private LoginUser testUser;

    @BeforeEach
    void setUp() throws Exception {
        loginUserService = new LoginUserService();
        
        Class<?> clazz = loginUserService.getClass();
        while (clazz != null) {
            try {
                Field baseMapperField = clazz.getDeclaredField("baseMapper");
                baseMapperField.setAccessible(true);
                baseMapperField.set(loginUserService, loginUserMapper);
                break;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }

        testUser = LoginUser.builder()
                .id(1L)
                .username("testuser")
                .password("encoded_password")
                .name("Test User")
                .phone("13800138000")
                .sex("M")
                .status(1)
                .build();
    }

    @Test
    @DisplayName("根据用户名查询用户 - 成功")
    void testGetByUsername_Success() {
        // getOne(throwEx=false) internally calls baseMapper.selectOne() twice
        when(loginUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenAnswer(inv -> testUser);

        LoginUser result = loginUserService.getByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    @DisplayName("根据用户名查询用户 - 用户不存在")
    void testGetByUsername_NotFound() {
        when(loginUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenAnswer(inv -> null);

        LoginUser result = loginUserService.getByUsername("nonexistent");

        assertNull(result);
    }

    @Test
    @DisplayName("分页查询用户列表")
    void testGetPage() {
        Page<LoginUser> page = new Page<>(1, 10);
        List<LoginUser> users = Arrays.asList(testUser);
        Page<LoginUser> pageResult = new Page<>(1, 10);
        pageResult.setRecords(users);
        when(loginUserMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                .thenReturn(pageResult);

        IPage<LoginUser> result = loginUserService.getPage(page, new LambdaQueryWrapper<>());

        assertNotNull(result);
    }

    @Test
    @DisplayName("根据ID查询用户")
    void testGetById() {
        when(loginUserMapper.selectById(1L)).thenReturn(testUser);

        LoginUser result = loginUserService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("保存用户")
    void testSave() {
        when(loginUserMapper.insert(any(LoginUser.class))).thenReturn(1);

        boolean result = loginUserService.save(testUser);

        assertTrue(result);
        verify(loginUserMapper).insert(testUser);
    }

    @Test
    @DisplayName("更新用户")
    void testUpdate() {
        when(loginUserMapper.updateById(any(LoginUser.class))).thenReturn(1);

        boolean result = loginUserService.updateById(testUser);

        assertTrue(result);
        verify(loginUserMapper).updateById(testUser);
    }

    @Test
    @DisplayName("删除用户")
    void testDelete() {
        when(loginUserMapper.deleteById(1L)).thenReturn(1);

        boolean result = loginUserService.removeById(1L);

        assertTrue(result);
        verify(loginUserMapper).deleteById(1L);
    }

    @Test
    @DisplayName("批量更新用户")
    void testUpdateBatch() {
        List<LoginUser> users = Arrays.asList(testUser);
        when(loginUserMapper.updateBatch(users)).thenReturn(1);

        int result = loginUserService.updateBatch(users);

        assertEquals(1, result);
    }

    @Test
    @DisplayName("批量插入用户")
    void testBatchInsert() {
        List<LoginUser> users = Arrays.asList(testUser);
        when(loginUserMapper.batchInsert(users)).thenReturn(1);

        int result = loginUserService.batchInsert(users);

        assertEquals(1, result);
    }

    @Test
    @DisplayName("插入或更新用户 - 存在则更新")
    void testInsertOrUpdate_Update() {
        testUser.setId(1L);
        when(loginUserMapper.insertOrUpdate(any(LoginUser.class))).thenReturn(true);

        boolean result = loginUserService.insertOrUpdate(testUser);

        assertTrue(result);
    }
}
