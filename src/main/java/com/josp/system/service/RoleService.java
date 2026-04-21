package com.josp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.josp.system.dto.RoleDTO;
import com.josp.system.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<Role> {

    /**
     * 获取所有角色列表
     */
    List<Role> listAllRoles();

    /**
     * 根据ID获取角色详情
     */
    Role getRoleById(Long id);

    /**
     * 创建角色
     */
    boolean createRole(RoleDTO roleDTO);

    /**
     * 更新角色
     */
    boolean updateRole(RoleDTO roleDTO);

    /**
     * 删除角色
     */
    boolean deleteRole(Long id);

    /**
     * 根据角色ID获取菜单列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);

    /**
     * 分配菜单权限
     */
    boolean assignMenus(Long roleId, List<Long> menuIds);
}
