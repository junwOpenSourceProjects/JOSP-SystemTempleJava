package com.josp.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.josp.system.dao.AccountRoleMapper;
import com.josp.system.dao.LoginUserMapper;
import com.josp.system.entity.AccountRole;
import com.josp.system.entity.LoginUser;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录用户服务
 */
@Service
@RequiredArgsConstructor
public class LoginUserService extends ServiceImpl<LoginUserMapper, LoginUser> {

    private final AccountRoleMapper accountRoleMapper;

    /**
     * 根据用户名获取用户
     */
    public LoginUser getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<LoginUser>().eq(LoginUser::getUsername, username));
    }

    /**
     * 分页查询
     */
    public IPage<LoginUser> getPage(Page<LoginUser> page, LambdaQueryWrapper<LoginUser> wrapper) {
        return page(page, wrapper);
    }

    /**
     * 批量更新
     */
    public int updateBatch(List<LoginUser> list) {
        return baseMapper.updateBatch(list);
    }

    /**
     * 批量更新（选择性）
     */
    public int updateBatchSelective(List<LoginUser> list) {
        return baseMapper.updateBatchSelective(list);
    }

    /**
     * 批量插入
     */
    public int batchInsert(List<LoginUser> list) {
        return baseMapper.batchInsert(list);
    }

    /**
     * 批量插入（XML中用 #{list} 引用）
     */
    public int batchInsertWithParam(List<LoginUser> list) {
        return baseMapper.batchInsert(list);
    }

    /**
     * 插入或更新
     */
    public int insertOrUpdate(LoginUser record) {
        return baseMapper.insertOrUpdate(record) ? 1 : 0;
    }

    /**
     * 选择性插入或更新
     */
    public int insertOrUpdateSelective(LoginUser record) {
        return baseMapper.insertOrUpdateSelective(record);
    }

    /**
     * 获取用户的所有角色ID
     */
    public List<Long> getRoleIdsByUserId(Long userId) {
        return accountRoleMapper.selectRoleIdsByUserId(userId);
    }

    /**
     * 绑定用户角色（先删后增，保持幂等）
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindRoles(Long userId, List<Long> roleIds) {
        // 删除旧的角色关联
        LambdaQueryWrapper<AccountRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountRole::getUserId, userId);
        accountRoleMapper.delete(wrapper);

        // 新增新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            List<AccountRole> relations = new ArrayList<>();
            for (Long roleId : roleIds) {
                AccountRole ar = AccountRole.builder()
                        .userId(userId)
                        .roleId(roleId)
                        .build();
                relations.add(ar);
            }
            accountRoleMapper.insertBatch(relations);
        }
    }

    /**
     * 删除用户时清理角色关联
     */
    @Transactional(rollbackFor = Exception.class)
    public void unbindRoles(Long userId) {
        LambdaQueryWrapper<AccountRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountRole::getUserId, userId);
        accountRoleMapper.delete(wrapper);
    }

    /**
     * 批量删除用户时清理角色关联
     */
    @Transactional(rollbackFor = Exception.class)
    public void unbindRoles(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<AccountRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AccountRole::getUserId, userIds);
        accountRoleMapper.delete(wrapper);
    }
}
