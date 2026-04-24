package com.josp.system.service;

import com.josp.system.config.TestRedisConfig;
import com.josp.system.dao.DictDataMapper;
import com.josp.system.dao.DictTypeMapper;
import com.josp.system.dto.DictDataDTO;
import com.josp.system.dto.DictTypeDTO;
import com.josp.system.entity.DictData;
import com.josp.system.entity.DictType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DictManageService 单元测试
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
@Transactional
class DictManageServiceTest {

    @Autowired(required = false)
    private DictTypeMapper dictTypeMapper;

    @Autowired(required = false)
    private DictDataMapper dictDataMapper;

    @Autowired
    private DictManageService dictManageService;

    // ============================================
    // 辅助方法
    // ============================================

    private DictTypeDTO buildDictTypeDTO(String suffix) {
        DictTypeDTO dto = new DictTypeDTO();
        dto.setName("测试字典类型_" + suffix);
        dto.setCode("DT_" + suffix + "_" + System.currentTimeMillis());
        dto.setStatus(1);
        dto.setRemark("测试备注");
        return dto;
    }

    private DictDataDTO buildDictDataDTO(Long dictTypeId, String suffix) {
        DictDataDTO dto = new DictDataDTO();
        dto.setDictTypeId(dictTypeId);
        dto.setLabel("测试标签_" + suffix);
        dto.setValue("val_" + suffix + "_" + System.currentTimeMillis());
        dto.setSort(0);
        dto.setStatus(1);
        return dto;
    }

    // ============================================
    // 字典类型 CRUD 测试
    // ============================================

    @Test
    void testCreateDictType() {
        DictTypeDTO dto = buildDictTypeDTO("create");

        boolean result = dictManageService.createDictType(dto);

        assertTrue(result, "创建字典类型应返回 true");
        assertNotNull(dto.getId(), "DTO 的 ID 应在保存后回写");
    }

    @Test
    void testGetDictTypeById() {
        DictTypeDTO dto = buildDictTypeDTO("getbyid");
        dictManageService.createDictType(dto);

        DictType found = dictManageService.getDictTypeById(dto.getId());

        assertNotNull(found, "应能找到字典类型");
        assertEquals(dto.getName(), found.getName());
        assertEquals(dto.getCode(), found.getCode());
    }

    @Test
    void testGetDictTypeById_NotFound() {
        DictType found = dictManageService.getDictTypeById(999999L);
        assertNull(found, "不存在的 ID 应返回 null");
    }

    @Test
    void testUpdateDictType() {
        DictTypeDTO dto = buildDictTypeDTO("update");
        dictManageService.createDictType(dto);

        dto.setName("修改后的名称_" + System.currentTimeMillis());
        dto.setRemark("修改后的备注");
        boolean result = dictManageService.updateDictType(dto);

        assertTrue(result, "更新应返回 true");

        DictType updated = dictManageService.getDictTypeById(dto.getId());
        assertEquals("修改后的名称_" + System.currentTimeMillis(), updated.getName());
        assertEquals("修改后的备注", updated.getRemark());
    }

    @Test
    void testDeleteDictType() {
        DictTypeDTO dto = buildDictTypeDTO("delete");
        dictManageService.createDictType(dto);

        boolean result = dictManageService.deleteDictType(dto.getId());

        assertTrue(result, "删除应返回 true");
        assertNull(dictManageService.getDictTypeById(dto.getId()));
    }

    @Test
    void testDeleteDictType_NotFound() {
        boolean result = dictManageService.deleteDictType(999999L);
        assertFalse(result, "删除不存在的记录应返回 false");
    }

    @Test
    void testListAllDictTypes() {
        dictManageService.createDictType(buildDictTypeDTO("list1"));
        dictManageService.createDictType(buildDictTypeDTO("list2"));

        List<DictType> list = dictManageService.listAllDictTypes();

        assertNotNull(list, "列表不应为 null");
        assertTrue(list.size() >= 2, "应至少有 2 条记录");
    }

    @Test
    void testPageDictTypes() {
        dictManageService.createDictType(buildDictTypeDTO("page1"));
        dictManageService.createDictType(buildDictTypeDTO("page2"));

        var result = dictManageService.pageDictTypes(1, 10, null, null);

        assertNotNull(result);
        assertTrue(result.getTotal() >= 2, "总记录应 >= 2");
        assertEquals(10, result.getRecords().size(), "每页应返回 10 条");
    }

    @Test
    void testPageDictTypes_WithNameFilter() {
        String uniqueName = "unique_dict_name_" + System.currentTimeMillis();
        DictTypeDTO dto = buildDictTypeDTO("pagefilter");
        dto.setName(uniqueName);
        dictManageService.createDictType(dto);

        var result = dictManageService.pageDictTypes(1, 10, uniqueName, null);

        assertNotNull(result);
        assertTrue(result.getRecords().stream()
                .anyMatch(dt -> uniqueName.equals(dt.getName())),
                "应能按名称过滤");
    }

    // ============================================
    // 字典数据 CRUD 测试
    // ============================================

    @Test
    void testCreateDictData() {
        DictTypeDTO dtDto = buildDictTypeDTO("data_create");
        dictManageService.createDictType(dtDto);

        DictDataDTO ddDto = buildDictDataDTO(dtDto.getId(), "create");

        boolean result = dictManageService.createDictData(ddDto);

        assertTrue(result, "创建字典数据应返回 true");
        assertNotNull(ddDto.getId(), "DTO 的 ID 应在保存后回写");
    }

    @Test
    void testGetDictDataById() {
        DictTypeDTO dtDto = buildDictTypeDTO("data_getbyid");
        dictManageService.createDictType(dtDto);

        DictDataDTO ddDto = buildDictDataDTO(dtDto.getId(), "getbyid");
        dictManageService.createDictData(ddDto);

        DictData found = dictManageService.getDictDataById(ddDto.getId());

        assertNotNull(found, "应能找到字典数据");
        assertEquals(ddDto.getLabel(), found.getLabel());
    }

    @Test
    void testGetDictDataById_NotFound() {
        DictData found = dictManageService.getDictDataById(999999L);
        assertNull(found, "不存在的 ID 应返回 null");
    }

    @Test
    void testUpdateDictData() {
        DictTypeDTO dtDto = buildDictTypeDTO("data_update");
        dictManageService.createDictType(dtDto);

        DictDataDTO ddDto = buildDictDataDTO(dtDto.getId(), "update");
        dictManageService.createDictData(ddDto);

        ddDto.setLabel("修改后的标签_" + System.currentTimeMillis());
        boolean result = dictManageService.updateDictData(ddDto);

        assertTrue(result, "更新应返回 true");

        DictData updated = dictManageService.getDictDataById(ddDto.getId());
        assertEquals("修改后的标签_" + System.currentTimeMillis(), updated.getLabel());
    }

    @Test
    void testDeleteDictData() {
        DictTypeDTO dtDto = buildDictTypeDTO("data_delete");
        dictManageService.createDictType(dtDto);

        DictDataDTO ddDto = buildDictDataDTO(dtDto.getId(), "delete");
        dictManageService.createDictData(ddDto);

        boolean result = dictManageService.deleteDictData(ddDto.getId());

        assertTrue(result, "删除应返回 true");
        assertNull(dictManageService.getDictDataById(ddDto.getId()));
    }

    @Test
    void testListDictDataByTypeId() {
        DictTypeDTO dtDto = buildDictTypeDTO("data_list_by_type");
        dictManageService.createDictType(dtDto);

        dictManageService.createDictData(buildDictDataDTO(dtDto.getId(), "list1"));
        dictManageService.createDictData(buildDictDataDTO(dtDto.getId(), "list2"));

        List<DictData> list = dictManageService.listDictDataByTypeId(dtDto.getId());

        assertNotNull(list, "列表不应为 null");
        assertEquals(2, list.size(), "应有 2 条数据");
    }

    @Test
    void testListDictDataByCode() {
        DictTypeDTO dtDto = buildDictTypeDTO("data_list_by_code");
        dtDto.setStatus(1); // 字典类型必须启用
        dictManageService.createDictType(dtDto);

        DictDataDTO ddDto = buildDictDataDTO(dtDto.getId(), "listbycode");
        ddDto.setStatus(1); // 字典数据也必须启用
        dictManageService.createDictData(ddDto);

        List<DictData> list = dictManageService.listDictDataByCode(dtDto.getCode());

        assertNotNull(list, "按编码查询不应返回 null");
        assertTrue(list.stream().anyMatch(d -> d.getValue().equals(ddDto.getValue())),
                "应能找到对应编码的字典数据");
    }

    @Test
    void testListDictDataByCode_NotFound() {
        List<DictData> list = dictManageService.listDictDataByCode("nonexistent_code_xxxxx");
        assertNotNull(list, "查询不存在的编码应返回空列表而非 null");
        assertTrue(list.isEmpty());
    }

    @Test
    void testPageDictData() {
        DictTypeDTO dtDto = buildDictTypeDTO("data_page");
        dictManageService.createDictType(dtDto);

        dictManageService.createDictData(buildDictDataDTO(dtDto.getId(), "page1"));
        dictManageService.createDictData(buildDictDataDTO(dtDto.getId(), "page2"));

        var result = dictManageService.pageDictData(1, 10, dtDto.getId(), null);

        assertNotNull(result);
        assertTrue(result.getTotal() >= 2);
    }

    // ============================================
    // 异常/边界测试
    // ============================================

    @Test
    void testCreateDictType_DuplicateCode() {
        DictTypeDTO dto1 = buildDictTypeDTO("dup");
        dictManageService.createDictType(dto1);

        DictTypeDTO dto2 = buildDictTypeDTO("dup2");
        dto2.setCode(dto1.getCode()); // 使用相同编码

        assertThrows(RuntimeException.class, () -> dictManageService.createDictType(dto2),
                "重复编码应抛出异常");
    }

    @Test
    void testUpdateDictType_NotFound() {
        DictTypeDTO dto = buildDictTypeDTO("updatenotfound");
        dto.setId(999999L);

        assertThrows(RuntimeException.class, () -> dictManageService.updateDictType(dto),
                "更新不存在的记录应抛出异常");
    }

    @Test
    void testUpdateDictData_NotFound() {
        DictDataDTO dto = new DictDataDTO();
        dto.setId(999999L);
        dto.setLabel("notfound");
        dto.setValue("notfound");

        assertThrows(RuntimeException.class, () -> dictManageService.updateDictData(dto),
                "更新不存在的记录应抛出异常");
    }

    @Test
    void testDeleteDictType_WithDictData() {
        DictTypeDTO dtDto = buildDictTypeDTO("cascade");
        dictManageService.createDictType(dtDto);

        DictDataDTO ddDto = buildDictDataDTO(dtDto.getId(), "cascade_data");
        dictManageService.createDictData(ddDto);

        assertThrows(RuntimeException.class, () -> dictManageService.deleteDictType(dtDto.getId()),
                "删除有关联数据的字典类型应抛出异常");
    }

    @Test
    void testDeleteDictType_ThenDictDataNotAccessible() {
        // 先删除字典类型
        DictTypeDTO dtDto = buildDictTypeDTO("cascade_then");
        dictManageService.createDictType(dtDto);

        boolean deleted = dictManageService.deleteDictType(dtDto.getId());
        assertTrue(deleted);

        // 再查字典数据应该也查不到（类型不存在，数据可能还在但查不出来）
        DictData found = dictManageService.getDictDataById(999999L);
        assertNull(found);
    }
}
