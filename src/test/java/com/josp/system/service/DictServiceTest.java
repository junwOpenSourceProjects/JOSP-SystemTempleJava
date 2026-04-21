package com.josp.system.service;

import com.josp.system.dao.DictDataMapper;
import com.josp.system.dao.DictTypeMapper;
import com.josp.system.entity.DictData;
import com.josp.system.entity.DictType;
import com.josp.system.service.impl.DictServiceImpl;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DictService 单元测试")
class DictServiceTest {

    @Mock
    private DictTypeMapper dictTypeMapper;

    @Mock
    private DictDataMapper dictDataMapper;

    @InjectMocks
    private DictServiceImpl dictService;

    private DictType testDictType;
    private DictData testDictData;

    @BeforeEach
    void setUp() {
        testDictType = new DictType();
        testDictType.setId(1L);
        testDictType.setName("性别");
        testDictType.setCode("sex");
        testDictType.setStatus(1);
        testDictType.setCreateTime(LocalDateTime.now());

        testDictData = new DictData();
        testDictData.setId(1L);
        testDictData.setDictTypeId(1L);
        testDictData.setLabel("男");
        testDictData.setValue("1");
        testDictData.setSort(1);
        testDictData.setStatus(1);
    }

    @Test
    @DisplayName("获取所有字典类型")
    void testListAllDictTypes() {
        List<DictType> dictTypes = Arrays.asList(testDictType);
        when(dictTypeMapper.selectList(any())).thenReturn(dictTypes);

        List<DictType> result = dictService.listAllDictTypes();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("sex", result.get(0).getCode());
    }

    @Test
    @DisplayName("根据字典编码获取字典数据")
    void testGetDictDataByCode() {
        List<DictData> dictDataList = Arrays.asList(testDictData);
        when(dictDataMapper.selectByDictCode("sex")).thenReturn(dictDataList);

        List<DictData> result = dictService.getDictDataByCode("sex");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("男", result.get(0).getLabel());
    }

    @Test
    @DisplayName("根据字典编码获取字典数据 - 空结果")
    void testGetDictDataByCode_Empty() {
        when(dictDataMapper.selectByDictCode("nonexistent")).thenReturn(null);

        List<DictData> result = dictService.getDictDataByCode("nonexistent");

        assertNull(result);
    }

    @Test
    @DisplayName("根据字典类型ID获取字典数据")
    void testGetDictDataByTypeId() {
        List<DictData> dictDataList = Arrays.asList(testDictData);
        when(dictDataMapper.selectByDictTypeId(1L)).thenReturn(dictDataList);

        List<DictData> result = dictService.getDictDataByTypeId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取所有字典数据（按类型分组）")
    void testGetAllDictDataGrouped() {
        List<DictType> dictTypes = Arrays.asList(testDictType);
        List<DictData> dictDataList = Arrays.asList(testDictData);

        when(dictTypeMapper.selectList(any())).thenReturn(dictTypes);
        when(dictDataMapper.selectByDictCode("sex")).thenReturn(dictDataList);

        Map<String, List<DictData>> result = dictService.getAllDictDataGrouped();

        assertNotNull(result);
        assertTrue(result.containsKey("sex"));
        assertEquals(1, result.get("sex").size());
    }

    @Test
    @DisplayName("获取所有字典数据 - 无数据")
    void testGetAllDictDataGrouped_Empty() {
        when(dictTypeMapper.selectList(any())).thenReturn(new java.util.ArrayList<>());

        Map<String, List<DictData>> result = dictService.getAllDictDataGrouped();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("根据字典编码获取字典数据 - 多条数据")
    void testGetDictDataByCode_MultipleData() {
        DictData data1 = new DictData();
        data1.setId(1L);
        data1.setLabel("男");
        data1.setValue("1");

        DictData data2 = new DictData();
        data2.setId(2L);
        data2.setLabel("女");
        data2.setValue("2");

        List<DictData> dictDataList = Arrays.asList(data1, data2);
        when(dictDataMapper.selectByDictCode("sex")).thenReturn(dictDataList);

        List<DictData> result = dictService.getDictDataByCode("sex");

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}