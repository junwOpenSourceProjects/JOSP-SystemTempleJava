package com.josp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.josp.system.dto.DictTypeDTO;
import com.josp.system.dto.DictDataDTO;
import com.josp.system.entity.DictType;
import com.josp.system.entity.DictData;
import com.josp.system.common.api.PageResult;

import java.util.List;

/**
 * 字典管理服务接口
 */
public interface DictManageService extends IService<DictType> {

    // ==================== 字典类型管理 ====================

    /**
     * 分页查询字典类型
     */
    PageResult<DictType> pageDictTypes(int pageNum, int pageSize, String name, String code);

    /**
     * 获取所有字典类型
     */
    List<DictType> listAllDictTypes();

    /**
     * 根据ID获取字典类型详情
     */
    DictType getDictTypeById(Long id);

    /**
     * 创建字典类型
     */
    boolean createDictType(DictTypeDTO dto);

    /**
     * 更新字典类型
     */
    boolean updateDictType(DictTypeDTO dto);

    /**
     * 删除字典类型
     */
    boolean deleteDictType(Long id);

    // ==================== 字典数据管理 ====================

    /**
     * 分页查询字典数据
     */
    PageResult<DictData> pageDictData(int pageNum, int pageSize, Long dictTypeId, String label);

    /**
     * 根据字典类型ID获取字典数据列表
     */
    List<DictData> listDictDataByTypeId(Long dictTypeId);

    /**
     * 根据字典类型编码获取字典数据列表
     */
    List<DictData> listDictDataByCode(String dictCode);

    /**
     * 根据ID获取字典数据详情
     */
    DictData getDictDataById(Long id);

    /**
     * 创建字典数据
     */
    boolean createDictData(DictDataDTO dto);

    /**
     * 更新字典数据
     */
    boolean updateDictData(DictDataDTO dto);

    /**
     * 删除字典数据
     */
    boolean deleteDictData(Long id);
}
