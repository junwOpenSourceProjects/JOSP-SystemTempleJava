package com.josp.system.service;

import com.josp.system.dao.DeptMapper;
import com.josp.system.entity.Dept;
import com.josp.system.service.impl.DeptServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeptService 单元测试")
class DeptServiceTest {

    @Mock
    private DeptMapper deptMapper;

    @InjectMocks
    private DeptServiceImpl deptService;

    private Dept testDept;
    private Dept childDept;

    @BeforeEach
    void setUp() {
        testDept = new Dept();
        testDept.setId(1L);
        testDept.setParentId(0L);
        testDept.setName("总公司");
        testDept.setCode("HQ001");
        testDept.setSort(1);
        testDept.setLeader("张三");
        testDept.setPhone("13800138000");
        testDept.setEmail("hq@example.com");
        testDept.setStatus(1);
        testDept.setCreateTime(LocalDateTime.now());

        childDept = new Dept();
        childDept.setId(2L);
        childDept.setParentId(1L);
        childDept.setName("研发部");
        childDept.setCode("DEV001");
        childDept.setSort(1);
        childDept.setStatus(1);
    }

    @Test
    @DisplayName("获取所有部门列表")
    void testListAllDepts() {
        List<Dept> depts = Arrays.asList(testDept, childDept);
        when(deptMapper.selectList(any())).thenReturn(depts);

        List<Dept> result = deptService.listAllDepts();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("获取部门树形结构")
    void testGetDeptTree() {
        List<Dept> depts = Arrays.asList(testDept, childDept);
        when(deptMapper.selectList(any())).thenReturn(depts);

        List<Dept> result = deptService.getDeptTree();

        assertNotNull(result);
    }

    @Test
    @DisplayName("根据ID获取部门详情")
    void testGetDeptById() {
        when(deptMapper.selectById(1L)).thenReturn(testDept);

        Dept result = deptService.getDeptById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("总公司", result.getName());
    }

    @Test
    @DisplayName("创建部门 - 成功")
    void testCreateDept_Success() {
        when(deptMapper.insert(any(Dept.class))).thenReturn(1);

        Dept newDept = new Dept();
        newDept.setName("新部门");
        newDept.setCode("NEW001");
        boolean result = deptService.createDept(newDept);

        assertTrue(result);
        verify(deptMapper).insert(any(Dept.class));
    }

    @Test
    @DisplayName("创建部门 - 父ID为空默认设为0")
    void testCreateDept_NullParentId() {
        when(deptMapper.insert(any(Dept.class))).thenReturn(1);

        Dept newDept = new Dept();
        newDept.setName("新部门");
        newDept.setParentId(null);
        boolean result = deptService.createDept(newDept);

        assertTrue(result);
        assertEquals(0L, newDept.getParentId());
    }

    @Test
    @DisplayName("更新部门 - 成功")
    void testUpdateDept_Success() {
        when(deptMapper.selectById(1L)).thenReturn(testDept);
        when(deptMapper.updateById(any(Dept.class))).thenReturn(1);

        testDept.setName("更新后的部门");
        boolean result = deptService.updateDept(testDept);

        assertTrue(result);
        verify(deptMapper).updateById(any(Dept.class));
    }

    @Test
    @DisplayName("更新部门 - 部门不存在")
    void testUpdateDept_NotFound() {
        when(deptMapper.selectById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> deptService.updateDept(testDept));
    }

    @Test
    @DisplayName("更新部门 - ID为空")
    void testUpdateDept_NullId() {
        assertThrows(RuntimeException.class, () -> deptService.updateDept(new Dept()));
    }

    @Test
    @DisplayName("更新部门 - 循环依赖检查")
    void testUpdateDept_CircularDependency() {
        when(deptMapper.selectById(1L)).thenReturn(testDept);

        Dept dept = new Dept();
        dept.setId(1L);
        dept.setParentId(1L); // 将自己设为父部门

        assertThrows(RuntimeException.class, () -> deptService.updateDept(dept));
    }

    @Test
    @DisplayName("删除部门 - 成功")
    void testDeleteDept_Success() {
        when(deptMapper.selectById(1L)).thenReturn(testDept);
        when(deptMapper.selectCount(any())).thenReturn(0L);
        when(deptMapper.deleteById(1L)).thenReturn(1);

        boolean result = deptService.deleteDept(1L);

        assertTrue(result);
        verify(deptMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除部门 - 有子部门")
    void testDeleteDept_HasChildren() {
        when(deptMapper.selectById(1L)).thenReturn(testDept);
        when(deptMapper.selectCount(any())).thenReturn(1L);

        assertThrows(RuntimeException.class, () -> deptService.deleteDept(1L));
    }

    @Test
    @DisplayName("删除部门 - ID为空")
    void testDeleteDept_NullId() {
        assertThrows(RuntimeException.class, () -> deptService.deleteDept(null));
    }

    @Test
    @DisplayName("获取部门树选择")
    void testGetDeptTreeSelect() {
        List<Dept> depts = Arrays.asList(testDept);
        when(deptMapper.selectList(any())).thenReturn(depts);

        List<Dept> result = deptService.getDeptTreeSelect();

        assertNotNull(result);
    }

    @Test
    @DisplayName("构建部门树 - 空列表")
    void testBuildDeptTree_Empty() {
        when(deptMapper.selectList(any())).thenReturn(new ArrayList<>());

        List<Dept> result = deptService.getDeptTree();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("构建部门树 - 多层级")
    void testBuildDeptTree_MultiLevel() {
        Dept level1 = new Dept();
        level1.setId(1L);
        level1.setParentId(0L);
        level1.setName("Level 1");

        Dept level2 = new Dept();
        level2.setId(2L);
        level2.setParentId(1L);
        level2.setName("Level 2");

        Dept level3 = new Dept();
        level3.setId(3L);
        level3.setParentId(2L);
        level3.setName("Level 3");

        when(deptMapper.selectList(any())).thenReturn(Arrays.asList(level1, level2, level3));

        List<Dept> result = deptService.getDeptTree();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}