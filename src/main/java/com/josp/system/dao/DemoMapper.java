package com.josp.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.josp.system.entity.Demo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 示例表格 Mapper
 */
@Mapper
public interface DemoMapper extends BaseMapper<Demo> {
}
