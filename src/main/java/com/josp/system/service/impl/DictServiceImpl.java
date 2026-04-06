package com.josp.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.josp.system.dao.DictDataMapper;
import com.josp.system.dao.DictTypeMapper;
import com.josp.system.entity.DictData;
import com.josp.system.entity.DictType;
import com.josp.system.service.DictService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典服务实现类
 */
@Service
@RequiredArgsConstructor
public class DictServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements DictService {

    private final DictDataMapper dictDataMapper;

    @Override
    public List<DictType> listAllDictTypes() {
        return baseMapper.selectList(null);
    }

    @Override
    @Cacheable(value = "dict", key = "'data:' + #dictCode")
    public List<DictData> getDictDataByCode(String dictCode) {
        return dictDataMapper.selectByDictCode(dictCode);
    }

    @Override
    public List<DictData> getDictDataByTypeId(Long dictTypeId) {
        return dictDataMapper.selectByDictTypeId(dictTypeId);
    }

    @Override
    public Map<String, List<DictData>> getAllDictDataGrouped() {
        Map<String, List<DictData>> result = new HashMap<>();

        // 获取所有字典类型
        List<DictType> dictTypes = baseMapper.selectList(null);

        // 按类型编码分组获取字典数据
        for (DictType dictType : dictTypes) {
            List<DictData> dataList = dictDataMapper.selectByDictCode(dictType.getCode());
            result.put(dictType.getCode(), dataList);
        }

        return result;
    }
}
