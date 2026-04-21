package com.josp.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.josp.system.entity.Dept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 部门 Mapper 接口
 */
@Mapper
public interface DeptMapper extends BaseMapper<Dept> {

    /**
     * 获取所有部门列表
     */
    List<Dept> listAllDepts();
}
