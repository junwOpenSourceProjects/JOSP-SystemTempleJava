package com.josp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.josp.system.entity.DictData;
import com.josp.system.entity.DictType;

import java.util.List;
import java.util.Map;

/**
 * 字典服务接口
 */
public interface DictService extends IService<DictType> {

    /**
     * 获取所有字典类型
     */
    List<DictType> listAllDictTypes();

    /**
     * 根据字典编码获取字典数据（带缓存）
     */
    List<DictData> getDictDataByCode(String dictCode);

    /**
     * 根据字典类型ID获取字典数据
     */
    List<DictData> getDictDataByTypeId(Long dictTypeId);

    /**
     * 获取所有字典数据（按类型分组）
     */
    Map<String, List<DictData>> getAllDictDataGrouped();
}
