package com.josp.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.josp.system.common.api.PageResult;
import com.josp.system.dao.DictDataMapper;
import com.josp.system.dao.DictTypeMapper;
import com.josp.system.dto.DictDataDTO;
import com.josp.system.dto.DictTypeDTO;
import com.josp.system.entity.DictData;
import com.josp.system.entity.DictType;
import com.josp.system.service.impl.DictManageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DictManageService 单元测试")
class DictManageServiceTest {

    @Mock
    private DictTypeMapper dictTypeMapper;

    @Mock
    private DictDataMapper dictDataMapper;

    private DictManageServiceImpl dictManageService;

    private DictType testDictType;
    private DictTypeDTO testDictTypeDTO;
    private DictData testDictData;
    private DictDataDTO testDictDataDTO;

    @BeforeEach
    void setUp() throws Exception {
        dictManageService = new DictManageServiceImpl(dictDataMapper);
        
        // ServiceImpl<DictTypeMapper, DictType> has baseMapper in its parent hierarchy
        Class<?> clazz = dictManageService.getClass();
        while (clazz != null) {
            try {
                Field baseMapperField = clazz.getDeclaredField("baseMapper");
                baseMapperField.setAccessible(true);
                baseMapperField.set(dictManageService, dictTypeMapper);
                break;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }

        testDictType = new DictType();
        testDictType.setId(1L);
        testDictType.setName("性别");
        testDictType.setCode("sex");
        testDictType.setStatus(1);
        testDictType.setRemark("性别字典");
        testDictType.setCreateTime(LocalDateTime.now());

        testDictTypeDTO = new DictTypeDTO();
        testDictTypeDTO.setName("性别");
        testDictTypeDTO.setCode("sex");
        testDictTypeDTO.setStatus(1);
        testDictTypeDTO.setRemark("性别字典");

        testDictData = new DictData();
        testDictData.setId(1L);
        testDictData.setDictTypeId(1L);
        testDictData.setLabel("男");
        testDictData.setValue("1");
        testDictData.setSort(1);
        testDictData.setStatus(1);
        testDictData.setCreateTime(LocalDateTime.now());

        testDictDataDTO = new DictDataDTO();
        testDictDataDTO.setDictTypeId(1L);
        testDictDataDTO.setLabel("男");
        testDictDataDTO.setValue("1");
        testDictDataDTO.setSort(1);
        testDictDataDTO.setStatus(1);
    }

    // ==================== 字典类型测试 ====================

    @Test
    @DisplayName("分页查询字典类型 - 成功")
    void testPageDictTypes_Success() {
        Page<DictType> pageResult = new Page<>(1, 10);
        pageResult.setRecords(Arrays.asList(testDictType));
        pageResult.setTotal(1);
        when(dictTypeMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(pageResult);

        PageResult<DictType> result = dictManageService.pageDictTypes(1, 10, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    @DisplayName("分页查询字典类型 - 按名称模糊查询")
    void testPageDictTypes_ByName() {
        Page<DictType> pageResult = new Page<>(1, 10);
        pageResult.setRecords(Arrays.asList(testDictType));
        pageResult.setTotal(1);
        when(dictTypeMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(pageResult);

        PageResult<DictType> result = dictManageService.pageDictTypes(1, 10, "性别", null);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("性别", result.getRecords().get(0).getName());
    }

    @Test
    @DisplayName("分页查询字典类型 - 按编码模糊查询")
    void testPageDictTypes_ByCode() {
        Page<DictType> pageResult = new Page<>(1, 10);
        pageResult.setRecords(Arrays.asList(testDictType));
        pageResult.setTotal(1);
        when(dictTypeMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(pageResult);

        PageResult<DictType> result = dictManageService.pageDictTypes(1, 10, null, "sex");

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("sex", result.getRecords().get(0).getCode());
    }

    @Test
    @DisplayName("获取所有字典类型")
    void testListAllDictTypes() {
        when(dictTypeMapper.selectList(any())).thenReturn(Arrays.asList(testDictType));

        List<DictType> result = dictManageService.listAllDictTypes();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("sex", result.get(0).getCode());
    }

    @Test
    @DisplayName("根据ID获取字典类型详情")
    void testGetDictTypeById() {
        when(dictTypeMapper.selectById(1L)).thenReturn(testDictType);

        DictType result = dictManageService.getDictTypeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("性别", result.getName());
    }

    @Test
    @DisplayName("根据ID获取字典类型详情 - 不存在")
    void testGetDictTypeById_NotFound() {
        when(dictTypeMapper.selectById(999L)).thenReturn(null);

        DictType result = dictManageService.getDictTypeById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("创建字典类型 - 成功")
    void testCreateDictType_Success() {
        when(dictTypeMapper.insert(any(DictType.class))).thenReturn(1);

        boolean result = dictManageService.createDictType(testDictTypeDTO);

        assertTrue(result);
        verify(dictTypeMapper).insert(any(DictType.class));
    }

    @Test
    @DisplayName("创建字典类型 - 使用默认状态")
    void testCreateDictType_DefaultStatus() {
        testDictTypeDTO.setStatus(null);
        when(dictTypeMapper.insert(any(DictType.class))).thenReturn(1);

        boolean result = dictManageService.createDictType(testDictTypeDTO);

        assertTrue(result);
        verify(dictTypeMapper).insert(any(DictType.class));
    }

    @Test
    @DisplayName("更新字典类型 - 成功")
    void testUpdateDictType_Success() {
        when(dictTypeMapper.selectById(1L)).thenReturn(testDictType);
        when(dictTypeMapper.updateById(any(DictType.class))).thenReturn(1);

        testDictTypeDTO.setId(1L);
        testDictTypeDTO.setName("更新后的性别");
        boolean result = dictManageService.updateDictType(testDictTypeDTO);

        assertTrue(result);
        verify(dictTypeMapper).updateById(any(DictType.class));
    }

    @Test
    @DisplayName("更新字典类型 - ID为空")
    void testUpdateDictType_NullId() {
        assertThrows(RuntimeException.class, () -> dictManageService.updateDictType(testDictTypeDTO));
    }

    @Test
    @DisplayName("更新字典类型 - 字典类型不存在")
    void testUpdateDictType_NotFound() {
        when(dictTypeMapper.selectById(1L)).thenReturn(null);

        testDictTypeDTO.setId(1L);
        assertThrows(RuntimeException.class, () -> dictManageService.updateDictType(testDictTypeDTO));
    }

    @Test
    @DisplayName("删除字典类型 - 成功")
    void testDeleteDictType_Success() {
        when(dictTypeMapper.selectById(1L)).thenReturn(testDictType);
        when(dictDataMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(dictTypeMapper.deleteById(1L)).thenReturn(1);

        boolean result = dictManageService.deleteDictType(1L);

        assertTrue(result);
        verify(dictTypeMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除字典类型 - ID为空")
    void testDeleteDictType_NullId() {
        assertThrows(RuntimeException.class, () -> dictManageService.deleteDictType(null));
    }

    @Test
    @DisplayName("删除字典类型 - 存在关联字典数据")
    void testDeleteDictType_HasDictData() {
        when(dictTypeMapper.selectById(1L)).thenReturn(testDictType);
        when(dictDataMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);

        assertThrows(RuntimeException.class, () -> dictManageService.deleteDictType(1L));
    }

    @Test
    @DisplayName("删除字典类型 - 字典类型不存在")
    void testDeleteDictType_NotFound() {
        when(dictTypeMapper.selectById(999L)).thenReturn(null);
        // removeById returns false when entity doesn't exist, doesn't throw
        when(dictTypeMapper.deleteById(999L)).thenReturn(0);

        boolean result = dictManageService.deleteDictType(999L);

        assertFalse(result);
    }

    // ==================== 字典数据测试 ====================

    @Test
    @DisplayName("分页查询字典数据 - 成功")
    void testPageDictData_Success() {
        Page<DictData> pageResult = new Page<>(1, 10);
        pageResult.setRecords(Arrays.asList(testDictData));
        pageResult.setTotal(1);
        when(dictDataMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(pageResult);

        PageResult<DictData> result = dictManageService.pageDictData(1, 10, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("分页查询字典数据 - 按类型ID筛选")
    void testPageDictData_ByTypeId() {
        Page<DictData> pageResult = new Page<>(1, 10);
        pageResult.setRecords(Arrays.asList(testDictData));
        pageResult.setTotal(1);
        when(dictDataMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(pageResult);

        PageResult<DictData> result = dictManageService.pageDictData(1, 10, 1L, null);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("分页查询字典数据 - 按标签模糊查询")
    void testPageDictData_ByLabel() {
        Page<DictData> pageResult = new Page<>(1, 10);
        pageResult.setRecords(Arrays.asList(testDictData));
        pageResult.setTotal(1);
        when(dictDataMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(pageResult);

        PageResult<DictData> result = dictManageService.pageDictData(1, 10, null, "男");

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("根据字典类型ID获取字典数据列表")
    void testListDictDataByTypeId() {
        when(dictDataMapper.selectByDictTypeId(1L)).thenReturn(Arrays.asList(testDictData));

        List<DictData> result = dictManageService.listDictDataByTypeId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("男", result.get(0).getLabel());
    }

    @Test
    @DisplayName("根据字典类型ID获取字典数据列表 - 无数据")
    void testListDictDataByTypeId_Empty() {
        when(dictDataMapper.selectByDictTypeId(999L)).thenReturn(null);

        List<DictData> result = dictManageService.listDictDataByTypeId(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("根据字典编码获取字典数据列表")
    void testListDictDataByCode() {
        when(dictDataMapper.selectByDictCode("sex")).thenReturn(Arrays.asList(testDictData));

        List<DictData> result = dictManageService.listDictDataByCode("sex");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("男", result.get(0).getLabel());
    }

    @Test
    @DisplayName("根据字典编码获取字典数据列表 - 无数据")
    void testListDictDataByCode_Empty() {
        when(dictDataMapper.selectByDictCode("nonexistent")).thenReturn(null);

        List<DictData> result = dictManageService.listDictDataByCode("nonexistent");

        assertNull(result);
    }

    @Test
    @DisplayName("根据ID获取字典数据详情")
    void testGetDictDataById() {
        when(dictDataMapper.selectById(1L)).thenReturn(testDictData);

        DictData result = dictManageService.getDictDataById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("男", result.getLabel());
    }

    @Test
    @DisplayName("根据ID获取字典数据详情 - 不存在")
    void testGetDictDataById_NotFound() {
        when(dictDataMapper.selectById(999L)).thenReturn(null);

        DictData result = dictManageService.getDictDataById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("创建字典数据 - 成功")
    void testCreateDictData_Success() {
        when(dictDataMapper.insert(any(DictData.class))).thenReturn(1);

        boolean result = dictManageService.createDictData(testDictDataDTO);

        assertTrue(result);
        verify(dictDataMapper).insert(any(DictData.class));
    }

    @Test
    @DisplayName("创建字典数据 - 使用默认值")
    void testCreateDictData_DefaultValues() {
        testDictDataDTO.setSort(null);
        testDictDataDTO.setStatus(null);
        when(dictDataMapper.insert(any(DictData.class))).thenReturn(1);

        boolean result = dictManageService.createDictData(testDictDataDTO);

        assertTrue(result);
        verify(dictDataMapper).insert(any(DictData.class));
    }

    @Test
    @DisplayName("更新字典数据 - 成功")
    void testUpdateDictData_Success() {
        when(dictDataMapper.selectById(1L)).thenReturn(testDictData);
        when(dictDataMapper.updateById(any(DictData.class))).thenReturn(1);

        testDictDataDTO.setId(1L);
        testDictDataDTO.setLabel("女");
        boolean result = dictManageService.updateDictData(testDictDataDTO);

        assertTrue(result);
        verify(dictDataMapper).updateById(any(DictData.class));
    }

    @Test
    @DisplayName("更新字典数据 - ID为空")
    void testUpdateDictData_NullId() {
        assertThrows(RuntimeException.class, () -> dictManageService.updateDictData(testDictDataDTO));
    }

    @Test
    @DisplayName("更新字典数据 - 字典数据不存在")
    void testUpdateDictData_NotFound() {
        when(dictDataMapper.selectById(1L)).thenReturn(null);

        testDictDataDTO.setId(1L);
        assertThrows(RuntimeException.class, () -> dictManageService.updateDictData(testDictDataDTO));
    }

    @Test
    @DisplayName("删除字典数据 - 成功")
    void testDeleteDictData_Success() {
        when(dictDataMapper.deleteById(1L)).thenReturn(1);

        boolean result = dictManageService.deleteDictData(1L);

        assertTrue(result);
        verify(dictDataMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除字典数据 - 返回0")
    void testDeleteDictData_Failed() {
        when(dictDataMapper.deleteById(999L)).thenReturn(0);

        boolean result = dictManageService.deleteDictData(999L);

        assertFalse(result);
    }

    @Test
    @DisplayName("多条字典数据场景")
    void testMultipleDictData() {
        DictData data1 = new DictData();
        data1.setId(1L);
        data1.setLabel("男");
        data1.setValue("1");

        DictData data2 = new DictData();
        data2.setId(2L);
        data2.setLabel("女");
        data2.setValue("2");

        DictData data3 = new DictData();
        data3.setId(3L);
        data3.setLabel("未知");
        data3.setValue("0");

        when(dictDataMapper.selectByDictTypeId(1L)).thenReturn(Arrays.asList(data1, data2, data3));

        List<DictData> result = dictManageService.listDictDataByTypeId(1L);

        assertNotNull(result);
        assertEquals(3, result.size());
    }
}
