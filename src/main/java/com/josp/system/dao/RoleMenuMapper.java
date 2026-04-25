package com.josp.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.josp.system.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    /**
     * 根据角色ID查询菜单ID列表
     */
    @Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色ID删除角色菜单关联
     */
    void deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色菜单关联
     */
    void batchInsert(@Param("list") List<RoleMenu> list);
}
