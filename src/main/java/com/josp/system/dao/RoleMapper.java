package com.josp.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.josp.system.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis Mapper for {@link Role} entity.
 * Inherited CRUD methods: selectById, insert, updateById, deleteById, etc.
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}
