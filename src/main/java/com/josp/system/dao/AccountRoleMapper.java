package com.josp.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.josp.system.entity.AccountRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AccountRoleMapper extends BaseMapper<AccountRole> {

    /**
     * 根据用户ID查询角色ID列表
     */
    @Select("SELECT role_id FROM account_role WHERE user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询角色编码列表
     */
    @Select("SELECT r.code FROM sys_role r " +
            "INNER JOIN account_role ar ON r.id = ar.role_id " +
            "WHERE ar.user_id = #{userId} AND r.status = 1")
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}
