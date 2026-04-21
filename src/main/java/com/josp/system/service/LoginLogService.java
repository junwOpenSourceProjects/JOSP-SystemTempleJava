package com.josp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.josp.system.common.api.PageResult;
import com.josp.system.entity.LoginLog;
import org.springframework.stereotype.Service;

/**
 * 登录日志服务接口
 */
public interface LoginLogService extends IService<LoginLog> {

    /**
     * 分页查询登录日志
     */
    PageResult<LoginLog> pageLoginLogs(int pageNum, int pageSize, String username);

    /**
     * 根据用户名查询登录日志
     */
    java.util.List<LoginLog> getByUsername(String username);

    /**
     * 根据用户ID查询登录日志
     */
    java.util.List<LoginLog> getByUserId(Long userId);

    /**
     * 清理登录日志
     */
    int cleanLoginLogs(int days);
}
