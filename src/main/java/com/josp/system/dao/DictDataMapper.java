package com.josp.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.josp.system.entity.DictData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 字典数据 Mapper 接口
 */
@Mapper
public interface DictDataMapper extends BaseMapper<DictData> {

    /**
     * 根据字典类型编码查询字典数据
     */
    @Select("SELECT d.* FROM sys_dict_data d " +
            "INNER JOIN sys_dict_type t ON d.dict_type_id = t.id " +
            "WHERE t.code = #{dictCode} AND d.status = 1 AND t.status = 1 " +
            "ORDER BY d.sort")
    List<DictData> selectByDictCode(@Param("dictCode") String dictCode);

    /**
     * 根据字典类型ID查询字典数据
     */
    @Select("SELECT * FROM sys_dict_data WHERE dict_type_id = #{dictTypeId} AND status = 1 ORDER BY sort")
    List<DictData> selectByDictTypeId(@Param("dictTypeId") Long dictTypeId);
}
