package com.josp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.josp.system.common.api.PageResult;
import com.josp.system.entity.OperLog;

/**
 * 操作日志服务接口
 */
public interface OperLogService extends IService<OperLog> {

    /**
     * 分页查询操作日志
     */
    PageResult<OperLog> pageOperLogs(int pageNum, int pageSize, String title, String operName, String status);

    /**
     * 根据ID获取操作日志详情
     */
    OperLog getOperLogById(Long id);

    /**
     * 删除操作日志
     */
    boolean deleteOperLog(Long id);

    /**
     * 批量删除操作日志
     */
    boolean deleteOperLogs(java.util.List<Long> ids);

    /**
     * 清理操作日志
     */
    int cleanOperLogs(int days);
}
