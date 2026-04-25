package com.josp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.josp.system.common.api.PageResult;
import com.josp.system.common.utils.PageUtils;
import com.josp.system.dao.DictDataMapper;
import com.josp.system.dao.DictTypeMapper;
import com.josp.system.dto.DictDataDTO;
import com.josp.system.dto.DictTypeDTO;
import com.josp.system.entity.DictData;
import com.josp.system.entity.DictType;
import com.josp.system.service.DictManageService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 字典管理服务实现类
 */
@Service
public class DictManageServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements DictManageService {

    private final DictDataMapper dictDataMapper;

    public DictManageServiceImpl(DictDataMapper dictDataMapper) {
        this.dictDataMapper = dictDataMapper;
    }

    // ==================== 字典类型管理 ====================

    @Override
    public PageResult<DictType> pageDictTypes(int pageNum, int pageSize, String name, String code) {
        Page<DictType> page = PageUtils.buildPage(pageNum, pageSize);
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(DictType::getCreateTime);
        if (StringUtils.hasText(name)) {
            wrapper.like(DictType::getName, name);
        }
        if (StringUtils.hasText(code)) {
            wrapper.like(DictType::getCode, code);
        }
        Page<DictType> resultPage = page(page, wrapper);
        return PageUtils.buildPageResult(resultPage);
    }

    @Override
    public List<DictType> listAllDictTypes() {
        return baseMapper.selectList(null);
    }

    @Override
    public DictType getDictTypeById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", allEntries = true)
    public boolean createDictType(DictTypeDTO dto) {
        DictType dictType = convertToDictTypeEntity(dto);
        boolean result = save(dictType);
        if (result) {
            dto.setId(dictType.getId());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", allEntries = true)
    public boolean updateDictType(DictTypeDTO dto) {
        if (dto.getId() == null) {
            throw new RuntimeException("字典类型ID不能为空");
        }
        DictType existType = getById(dto.getId());
        if (existType == null) {
            throw new RuntimeException("字典类型不存在");
        }
        DictType dictType = convertToDictTypeEntity(dto);
        dictType.setId(dto.getId());
        return updateById(dictType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", allEntries = true)
    public boolean deleteDictType(Long id) {
        if (id == null) {
            throw new RuntimeException("字典类型ID不能为空");
        }
        // 检查是否有字典数据
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictTypeId, id);
        if (dictDataMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("请先删除该类型下的字典数据");
        }
        return removeById(id);
    }

    // ==================== 字典数据管理 ====================

    @Override
    public PageResult<DictData> pageDictData(int pageNum, int pageSize, Long dictTypeId, String label) {
        Page<DictData> page = PageUtils.buildPage(pageNum, pageSize);
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(DictData::getSort).orderByDesc(DictData::getCreateTime);
        if (dictTypeId != null) {
            wrapper.eq(DictData::getDictTypeId, dictTypeId);
        }
        if (StringUtils.hasText(label)) {
            wrapper.like(DictData::getLabel, label);
        }
        Page<DictData> resultPage = dictDataMapper.selectPage(page, wrapper);
        return PageUtils.buildPageResult(resultPage);
    }

    @Override
    public List<DictData> listDictDataByTypeId(Long dictTypeId) {
        return dictDataMapper.selectByDictTypeId(dictTypeId);
    }

    @Override
    @Cacheable(value = "dict", key = "'data:' + #dictCode")
    public List<DictData> listDictDataByCode(String dictCode) {
        return dictDataMapper.selectByDictCode(dictCode);
    }

    @Override
    public DictData getDictDataById(Long id) {
        return dictDataMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", allEntries = true)
    public boolean createDictData(DictDataDTO dto) {
        DictData dictData = convertToDictDataEntity(dto);
        boolean result = dictDataMapper.insert(dictData) > 0;
        if (result) {
            dto.setId(dictData.getId());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", allEntries = true)
    public boolean updateDictData(DictDataDTO dto) {
        if (dto.getId() == null) {
            throw new RuntimeException("字典数据ID不能为空");
        }
        DictData existData = dictDataMapper.selectById(dto.getId());
        if (existData == null) {
            throw new RuntimeException("字典数据不存在");
        }
        DictData dictData = convertToDictDataEntity(dto);
        dictData.setId(dto.getId());
        return dictDataMapper.updateById(dictData) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", allEntries = true)
    public boolean deleteDictData(Long id) {
        return dictDataMapper.deleteById(id) > 0;
    }

    // ==================== 私有转换方法 ====================

    private DictType convertToDictTypeEntity(DictTypeDTO dto) {
        DictType dictType = new DictType();
        dictType.setName(dto.getName());
        dictType.setCode(dto.getCode());
        dictType.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        dictType.setRemark(dto.getRemark());
        return dictType;
    }

    private DictData convertToDictDataEntity(DictDataDTO dto) {
        DictData dictData = new DictData();
        dictData.setDictTypeId(dto.getDictTypeId());
        dictData.setLabel(dto.getLabel());
        dictData.setValue(dto.getValue());
        dictData.setSort(dto.getSort() != null ? dto.getSort() : 0);
        dictData.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        dictData.setRemark(dto.getRemark());
        return dictData;
    }
}
