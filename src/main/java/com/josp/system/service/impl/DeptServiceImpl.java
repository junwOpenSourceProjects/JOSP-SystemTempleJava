package com.josp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.josp.system.dao.DeptMapper;
import com.josp.system.entity.Dept;
import com.josp.system.service.DeptService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    @Override
    public List<Dept> listAllDepts() {
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Dept::getSort).orderByAsc(Dept::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<Dept> getDeptTree() {
        List<Dept> allDepts = listAllDepts();
        return buildDeptTree(allDepts, 0L);
    }

    @Override
    public Dept getDeptById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDept(Dept dept) {
        if (dept.getParentId() == null) {
            dept.setParentId(0L);
        }
        return save(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDept(Dept dept) {
        if (dept.getId() == null) {
            throw new RuntimeException("部门ID不能为空");
        }
        Dept existDept = getById(dept.getId());
        if (existDept == null) {
            throw new RuntimeException("部门不存在");
        }
        // 检查是否循环依赖
        if (dept.getParentId() != null && dept.getParentId().equals(dept.getId())) {
            throw new RuntimeException("不能将自己设为父部门");
        }
        return updateById(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDept(Long id) {
        if (id == null) {
            throw new RuntimeException("部门ID不能为空");
        }
        // 检查是否有子部门
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getParentId, id);
        if (count(wrapper) > 0) {
            throw new RuntimeException("请先删除子部门");
        }
        return removeById(id);
    }

    @Override
    public List<Dept> getDeptTreeSelect() {
        List<Dept> allDepts = listAllDepts();
        return buildDeptTree(allDepts, 0L);
    }

    /**
     * 构建部门树形结构
     */
    private List<Dept> buildDeptTree(List<Dept> depts, Long parentId) {
        if (CollectionUtils.isEmpty(depts)) {
            return new ArrayList<>();
        }
        return depts.stream()
                .filter(dept -> Objects.equals(dept.getParentId(), parentId))
                .map(dept -> {
                    List<Dept> children = buildDeptTree(depts, dept.getId());
                    if (!CollectionUtils.isEmpty(children)) {
                        dept.setChildren(children);
                    }
                    return dept;
                })
                .collect(Collectors.toList());
    }
}
