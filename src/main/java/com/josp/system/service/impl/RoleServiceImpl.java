package com.josp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.josp.system.dao.AccountRoleMapper;
import com.josp.system.dao.RoleMapper;
import com.josp.system.dao.RoleMenuMapper;
import com.josp.system.dto.RoleDTO;
import com.josp.system.entity.AccountRole;
import com.josp.system.entity.Role;
import com.josp.system.entity.RoleMenu;
import com.josp.system.service.RoleService;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色服务实现类。
 *
 * <p>处理系统角色的 CRUD 操作，并通过 {@code sys_role_menu} 管理角色到菜单的权限分配。
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMenuMapper roleMenuMapper;
    private final AccountRoleMapper accountRoleMapper;

    /**
     * 查询所有角色，按排序升序和创建时间降序排列。
     *
     * @return 角色列表
     */
    @Override
    public List<Role> listAllRoles() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Role::getSort).orderByDesc(Role::getCreateTime);
        return list(wrapper);
    }

    /**
     * 根据ID查询角色详情。
     *
     * @param id 角色ID
     * @return 角色实体
     */
    @Override
    public Role getRoleById(Long id) {
        return getById(id);
    }

    /**
     * 创建新角色。
     *
     * <p>创建前检查角色编码唯一性，创建后根据 menuIds 分配菜单权限。
     *
     * @param roleDTO 角色数据传输对象
     * @return 是否创建成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(RoleDTO roleDTO) {
        // 检查编码是否已存在
        if (isCodeExists(roleDTO.getCode(), null)) {
            throw new RuntimeException("角色编码已存在");
        }

        Role role = new Role();
        role.setName(roleDTO.getName());
        role.setCode(roleDTO.getCode());
        role.setSort(roleDTO.getSort() != null ? roleDTO.getSort() : 0);
        role.setStatus(roleDTO.getStatus() != null ? roleDTO.getStatus() : 1);
        role.setRemark(roleDTO.getRemark());

        boolean result = save(role);
        if (result) {
            roleDTO.setId(role.getId());
        }

        // 分配菜单权限
        if (result && roleDTO.getMenuIds() != null && !roleDTO.getMenuIds().isEmpty()) {
            roleMenuMapper.deleteByRoleId(role.getId());
            List<RoleMenu> roleMenus = roleDTO.getMenuIds().stream().map(menuId -> {
                RoleMenu rm = new RoleMenu();
                rm.setId(IdWorker.getId());
                rm.setRoleId(role.getId());
                rm.setMenuId(menuId);
                return rm;
            }).collect(Collectors.toList());
            roleMenuMapper.batchInsert(roleMenus);
        }

        return result;
    }

    /**
     * 更新角色信息。
     *
     * <p>更新前检查角色编码唯一性（排除自身），更新后重新分配菜单权限。
     *
     * @param roleDTO 角色数据传输对象
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(RoleDTO roleDTO) {
        if (roleDTO.getId() == null) {
            throw new RuntimeException("角色ID不能为空");
        }

        // 检查编码是否已存在
        if (isCodeExists(roleDTO.getCode(), roleDTO.getId())) {
            throw new RuntimeException("角色编码已存在");
        }

        Role role = getById(roleDTO.getId());
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }

        role.setName(roleDTO.getName());
        role.setCode(roleDTO.getCode());
        role.setSort(roleDTO.getSort() != null ? roleDTO.getSort() : 0);
        role.setStatus(roleDTO.getStatus() != null ? roleDTO.getStatus() : 1);
        role.setRemark(roleDTO.getRemark());

        boolean result = updateById(role);

        // 更新菜单权限
        if (result) {
            roleMenuMapper.deleteByRoleId(role.getId());
            if (roleDTO.getMenuIds() != null && !roleDTO.getMenuIds().isEmpty()) {
                List<RoleMenu> roleMenus = roleDTO.getMenuIds().stream().map(menuId -> {
                    RoleMenu rm = new RoleMenu();
                    rm.setId(IdWorker.getId());
                    rm.setRoleId(role.getId());
                    rm.setMenuId(menuId);
                    return rm;
                }).collect(Collectors.toList());
                roleMenuMapper.batchInsert(roleMenus);
            }
        }

        return result;
    }

    /**
     * 删除角色。
     *
     * <p>删除前检查是否已分配给用户，如已分配则拒绝删除。
     * 删除时会同时清理该角色的菜单关联。
     *
     * @param id 角色ID
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long id) {
        if (id == null) {
            throw new RuntimeException("角色ID不能为空");
        }
        Role existRole = getById(id);
        if (existRole == null) {
            throw new RuntimeException("角色不存在");
        }

        // 检查是否有关联用户
        LambdaQueryWrapper<AccountRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountRole::getRoleId, id);
        if (accountRoleMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("该角色已分配给用户，无法删除");
        }

        // 删除角色菜单关联
        roleMenuMapper.deleteByRoleId(id);

        // 删除角色
        return removeById(id);
    }

    /**
     * 根据角色ID查询已分配的菜单ID列表。
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        List<Long> menuIds = roleMenuMapper.selectMenuIdsByRoleId(roleId);
        return menuIds != null ? menuIds : new ArrayList<>();
    }

    /**
     * 为角色分配菜单权限。
     *
     * <p>会先删除角色原有的菜单权限，再插入新的菜单权限。
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     * @return 是否分配成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignMenus(Long roleId, List<Long> menuIds) {
        if (roleId == null) {
            throw new RuntimeException("角色ID不能为空");
        }

        // 先删除原有菜单权限
        roleMenuMapper.deleteByRoleId(roleId);

        // 分配新菜单权限
        if (menuIds != null && !menuIds.isEmpty()) {
            List<RoleMenu> roleMenus = menuIds.stream().map(menuId -> {
                RoleMenu rm = new RoleMenu();
                rm.setId(IdWorker.getId());
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                return rm;
            }).collect(Collectors.toList());
            roleMenuMapper.batchInsert(roleMenus);
        }

        return true;
    }

    /**
     * 检查角色编码是否已存在。
     *
     * <p>可用于允许将角色更新为其自身的编码而不触发重复错误。
     *
     * @param code      要检查的角色编码
     * @param excludeId 要忽略的角色ID（传入 null 以检查所有记录）
     * @return 如果编码已在其他角色中存在则返回 true
     */
    private boolean isCodeExists(String code, Long excludeId) {
        if (!StringUtils.hasText(code)) {
            return false;
        }

        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getCode, code);

        if (excludeId != null) {
            wrapper.ne(Role::getId, excludeId);
        }

        return count(wrapper) > 0;
    }
}
