package com.josp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.josp.system.dao.RoleMapper;
import com.josp.system.dao.RoleMenuMapper;
import com.josp.system.dto.RoleDTO;
import com.josp.system.entity.Role;
import com.josp.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMenuMapper roleMenuMapper;

    @Override
    public List<Role> listAllRoles() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Role::getSort).orderByDesc(Role::getCreateTime);
        return list(wrapper);
    }

    @Override
    public Role getRoleById(Long id) {
        return getById(id);
    }

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

        // 分配菜单权限
        if (result && roleDTO.getMenuIds() != null && !roleDTO.getMenuIds().isEmpty()) {
            roleMenuMapper.deleteByRoleId(role.getId());
            roleMenuMapper.batchInsert(role.getId(), roleDTO.getMenuIds());
        }

        return result;
    }

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
                roleMenuMapper.batchInsert(role.getId(), roleDTO.getMenuIds());
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long id) {
        if (id == null) {
            throw new RuntimeException("角色ID不能为空");
        }

        // 删除角色菜单关联
        roleMenuMapper.deleteByRoleId(id);

        // 删除角色
        return removeById(id);
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        List<Long> menuIds = roleMenuMapper.selectMenuIdsByRoleId(roleId);
        return menuIds != null ? menuIds : new ArrayList<>();
    }

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
            roleMenuMapper.batchInsert(roleId, menuIds);
        }

        return true;
    }

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
