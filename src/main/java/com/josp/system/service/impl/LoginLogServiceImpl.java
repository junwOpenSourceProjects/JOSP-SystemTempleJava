package com.josp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.josp.system.common.api.PageResult;
import com.josp.system.common.utils.PageUtils;
import com.josp.system.dao.LoginLogMapper;
import com.josp.system.entity.LoginLog;
import com.josp.system.service.LoginLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录日志服务实现类
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    @Override
    public PageResult<LoginLog> pageLoginLogs(int pageNum, int pageSize, String username, String status) {
        Page<LoginLog> page = PageUtils.buildPage(pageNum, pageSize);
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(LoginLog::getLoginTime);
        if (StringUtils.hasText(username)) {
            wrapper.like(LoginLog::getUsername, username);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(LoginLog::getStatus, status);
        }
        Page<LoginLog> resultPage = page(page, wrapper);
        return PageUtils.buildPageResult(resultPage);
    }

    @Override
    public List<LoginLog> getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    @Override
    public List<LoginLog> getByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public int cleanLoginLogs(int days) {
        LocalDateTime clearTime = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(LoginLog::getLoginTime, clearTime);
        return baseMapper.delete(wrapper);
    }

    @Override
    public List<LoginLog> listLoginLogs(String username, String status) {
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(LoginLog::getLoginTime);
        if (StringUtils.hasText(username)) {
            wrapper.like(LoginLog::getUsername, username);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(LoginLog::getStatus, status);
        }
        return list(wrapper);
    }
}
