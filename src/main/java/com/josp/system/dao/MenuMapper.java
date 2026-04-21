package com.josp.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.josp.system.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    
    /**
     * 获取所有可用菜单列表
     */
    List<Menu> listAllVisibleMenus();

    /**
     * 根据用户ID获取菜单列表（用于动态路由）
     */
    @Select("SELECT DISTINCT m.* FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN account_role ar ON rm.role_id = ar.role_id " +
            "WHERE ar.user_id = #{userId} AND m.visible = 1 " +
            "ORDER BY m.sort ASC, m.create_time DESC")
    List<Menu> listMenusByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID获取菜单ID列表
     */
    @Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID获取权限标识列表（按钮级别权限）
     */
    @Select("SELECT DISTINCT m.perm FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN account_role ar ON rm.role_id = ar.role_id " +
            "WHERE ar.user_id = #{userId} AND m.perm IS NOT NULL AND m.perm != '' AND m.type = 2")
    List<String> selectMenuPermsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID获取菜单列表
     */
    @Select("SELECT m.* FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "WHERE rm.role_id = #{roleId} AND m.visible = 1 " +
            "ORDER BY m.sort ASC")
    List<Menu> listMenusByRoleId(@Param("roleId") Long roleId);
}
