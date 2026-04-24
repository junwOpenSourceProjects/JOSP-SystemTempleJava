package com.josp.system.service;

import com.josp.system.common.api.PageResult;
import com.josp.system.config.TestRedisConfig;
import com.josp.system.entity.Dept;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DeptService 单元测试
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
@Transactional
class DeptServiceTest {

    @Autowired
    private DeptService deptService;

    // ============================================
    // 辅助方法
    // ============================================

    private Dept buildDept(String suffix) {
        Dept dept = new Dept();
        dept.setName("测试部门_" + suffix);
        dept.setCode("DEPT_T_" + suffix + "_" + System.currentTimeMillis());
        dept.setSort(0);
        dept.setStatus(1);
        dept.setLeader("测试负责人");
        dept.setPhone("13800138000");
        dept.setEmail("test@test.com");
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        dept.setCreateUser(1L);
        dept.setUpdateUser(1L);
        return dept;
    }

    // ============================================
    // CRUD 测试
    // ============================================

    @Test
    void testCreateDept() {
        Dept dept = buildDept("create");

        boolean result = deptService.createDept(dept);

        assertTrue(result, "创建部门应返回 true");
        assertNotNull(dept.getId(), "ID 应在保存后回写");
        assertNotNull(dept.getCreateTime(), "创建时间应有值");
    }

    @Test
    void testCreateDept_WithParent() {
        Dept parent = buildDept("parent");
        deptService.createDept(parent);

        Dept child = buildDept("child");
        child.setParentId(parent.getId());
        boolean result = deptService.createDept(child);

        assertTrue(result);
        assertEquals(parent.getId(), child.getParentId());
    }

    @Test
    void testCreateDept_NullParentId() {
        Dept dept = buildDept("nullparent");
        dept.setParentId(null);

        boolean result = deptService.createDept(dept);

        assertTrue(result);
        assertEquals(0L, dept.getParentId(), "null parentId 应自动转为 0");
    }

    @Test
    void testGetDeptById() {
        Dept dept = buildDept("getbyid");
        deptService.createDept(dept);

        Dept found = deptService.getDeptById(dept.getId());

        assertNotNull(found);
        assertEquals(dept.getName(), found.getName());
        assertEquals(dept.getCode(), found.getCode());
    }

    @Test
    void testGetDeptById_NotFound() {
        Dept found = deptService.getDeptById(999999L);
        assertNull(found);
    }

    @Test
    void testUpdateDept() {
        Dept dept = buildDept("update");
        deptService.createDept(dept);

        dept.setName("修改后的部门名_" + System.currentTimeMillis());
        dept.setLeader("新负责人");
        boolean result = deptService.updateDept(dept);

        assertTrue(result);
        Dept updated = deptService.getDeptById(dept.getId());
        assertEquals("修改后的部门名_" + System.currentTimeMillis(), updated.getName());
        assertEquals("新负责人", updated.getLeader());
    }

    @Test
    void testUpdateDept_NotFound() {
        Dept dept = buildDept("updatenotfound");
        dept.setId(999999L);

        assertThrows(RuntimeException.class, () -> deptService.updateDept(dept),
                "更新不存在的部门应抛出异常");
    }

    @Test
    void testUpdateDept_SelfAsParent() {
        Dept dept = buildDept("selfparent");
        deptService.createDept(dept);

        dept.setParentId(dept.getId());

        assertThrows(RuntimeException.class, () -> deptService.updateDept(dept),
                "将自己设为父部门应抛出异常");
    }

    @Test
    void testDeleteDept() {
        Dept dept = buildDept("delete");
        deptService.createDept(dept);

        boolean result = deptService.deleteDept(dept.getId());

        assertTrue(result);
        assertNull(deptService.getDeptById(dept.getId()));
    }

    @Test
    void testDeleteDept_NotFound() {
        assertThrows(RuntimeException.class, () -> deptService.deleteDept(999999L),
                "删除不存在的部门应抛出异常");
    }

    @Test
    void testDeleteDept_WithChildren() {
        Dept parent = buildDept("parentdelete");
        deptService.createDept(parent);

        Dept child = buildDept("childdelete");
        child.setParentId(parent.getId());
        deptService.createDept(child);

        assertThrows(RuntimeException.class, () -> deptService.deleteDept(parent.getId()),
                "删除有子部门的部门应抛出异常");
    }

    @Test
    void testDeleteDept_LeafCanBeDeleted() {
        Dept dept = buildDept("leafdelete");
        deptService.createDept(dept);

        boolean result = deptService.deleteDept(dept.getId());

        assertTrue(result);
    }

    @Test
    void testListAllDepts() {
        deptService.createDept(buildDept("list1"));
        deptService.createDept(buildDept("list2"));

        List<Dept> list = deptService.listAllDepts();

        assertNotNull(list);
        assertTrue(list.size() >= 2);
    }

    // ============================================
    // 树形结构测试
    // ============================================

    @Test
    void testGetDeptTree() {
        Dept root = buildDept("tree_root");
        root.setParentId(0L);
        deptService.createDept(root);

        Dept level1 = buildDept("tree_l1");
        level1.setParentId(root.getId());
        deptService.createDept(level1);

        Dept level2 = buildDept("tree_l2");
        level2.setParentId(level1.getId());
        deptService.createDept(level2);

        List<Dept> tree = deptService.getDeptTree();

        assertNotNull(tree);
        Dept foundRoot = tree.stream()
                .filter(d -> d.getId().equals(root.getId()))
                .findFirst()
                .orElse(null);
        assertNotNull(foundRoot, "根节点应出现在树中");
        assertNotNull(foundRoot.getChildren());
    }

    @Test
    void testGetDeptTree_HierarchicalStructure() {
        Dept root = buildDept("hier_root");
        root.setParentId(0L);
        deptService.createDept(root);

        Dept child1 = buildDept("hier_child1");
        child1.setParentId(root.getId());
        deptService.createDept(child1);

        Dept child2 = buildDept("hier_child2");
        child2.setParentId(root.getId());
        deptService.createDept(child2);

        List<Dept> tree = deptService.getDeptTree();

        Dept foundRoot = tree.stream()
                .filter(d -> d.getId().equals(root.getId()))
                .findFirst()
                .orElse(null);
        assertNotNull(foundRoot);
        assertEquals(2, foundRoot.getChildren().size(), "根节点应有 2 个子部门");
    }

    @Test
    void testGetDeptTreeSelect() {
        Dept dept = buildDept("treeselect");
        deptService.createDept(dept);

        List<Dept> tree = deptService.getDeptTreeSelect();

        assertNotNull(tree);
        assertTrue(tree.size() > 0);
    }

    // ============================================
    // 分页查询测试
    // ============================================

    @Test
    void testGetDeptPage() {
        for (int i = 0; i < 5; i++) {
            deptService.createDept(buildDept("page_" + i));
        }

        PageResult<Map<String, Object>> result = deptService.getDeptPage(1, 10, null, null);

        assertNotNull(result);
        assertTrue(result.getTotal() >= 5);
        assertEquals(10, result.getRecords().size());
    }

    @Test
    void testGetDeptPage_WithNameFilter() {
        String uniqueName = "filter_unique_" + System.currentTimeMillis();
        Dept dept = buildDept("filterpage");
        dept.setName(uniqueName);
        deptService.createDept(dept);

        PageResult<Map<String, Object>> result = deptService.getDeptPage(1, 10, uniqueName, null);

        assertNotNull(result);
        assertTrue(result.getRecords().stream()
                .anyMatch(m -> uniqueName.equals(m.get("name"))));
    }

    @Test
    void testGetDeptPage_WithStatusFilter() {
        Dept active = buildDept("active_status");
        active.setStatus(1);
        deptService.createDept(active);

        Dept inactive = buildDept("inactive_status");
        inactive.setStatus(0);
        deptService.createDept(inactive);

        PageResult<Map<String, Object>> result = deptService.getDeptPage(1, 10, null, 1);

        assertTrue(result.getRecords().stream()
                .allMatch(m -> 1 == ((Number) m.get("status")).intValue()));
    }

    @Test
    void testGetDeptPage_SecondPage() {
        for (int i = 0; i < 15; i++) {
            deptService.createDept(buildDept("page2_" + i));
        }

        PageResult<Map<String, Object>> result = deptService.getDeptPage(2, 10, null, null);

        assertNotNull(result);
        assertEquals(10, result.getRecords().size(), "第二页应返回 10 条");
    }

    @Test
    void testGetDeptPage_EmptyResult() {
        PageResult<Map<String, Object>> result = deptService.getDeptPage(1, 10, "完全不存在的部门名_xxxxx", null);

        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
    }
}
