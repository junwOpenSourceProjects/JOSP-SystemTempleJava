package com.josp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.josp.system.common.api.PageResult;
import com.josp.system.entity.Dept;

import java.util.List;
import java.util.Map;

/**
 * 部门服务接口
 */
public interface DeptService extends IService<Dept> {

    /**
     * 获取所有部门列表
     */
    List<Dept> listAllDepts();

    /**
     * 获取部门树形结构
     */
    List<Dept> getDeptTree();

    /**
     * 根据ID获取部门详情
     */
    Dept getDeptById(Long id);

    /**
     * 创建部门
     */
    boolean createDept(Dept dept);

    /**
     * 更新部门
     */
    boolean updateDept(Dept dept);

    /**
     * 删除部门
     */
    boolean deleteDept(Long id);

    /**
     * 获取部门树（用于前端选择）
     */
    List<Dept> getDeptTreeSelect();

    /**
     * 分页查询部门列表
     */
    PageResult<Map<String, Object>> getDeptPage(Integer page, Integer limit, String name, Integer status);
}
