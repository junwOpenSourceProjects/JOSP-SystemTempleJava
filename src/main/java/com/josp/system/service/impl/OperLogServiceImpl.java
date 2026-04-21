package com.josp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.josp.system.common.api.PageResult;
import com.josp.system.common.utils.PageUtils;
import com.josp.system.dao.OperLogMapper;
import com.josp.system.entity.OperLog;
import com.josp.system.service.OperLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志服务实现类
 */
@Service
public class OperLogServiceImpl extends ServiceImpl<OperLogMapper, OperLog> implements OperLogService {

    @Override
    public PageResult<OperLog> pageOperLogs(int pageNum, int pageSize, String title, String operName, String status) {
        Page<OperLog> page = PageUtils.buildPage(pageNum, pageSize);
        LambdaQueryWrapper<OperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(OperLog::getOperTime);
        if (StringUtils.hasText(title)) {
            wrapper.like(OperLog::getTitle, title);
        }
        if (StringUtils.hasText(operName)) {
            wrapper.like(OperLog::getOperName, operName);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(OperLog::getStatus, status);
        }
        Page<OperLog> resultPage = page(page, wrapper);
        return PageUtils.buildPageResult(resultPage);
    }

    @Override
    public OperLog getOperLogById(Long id) {
        return getById(id);
    }

    @Override
    public boolean deleteOperLog(Long id) {
        return removeById(id);
    }

    @Override
    public boolean deleteOperLogs(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    public int cleanOperLogs(int days) {
        LocalDateTime clearTime = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<OperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(OperLog::getOperTime, clearTime);
        return baseMapper.delete(wrapper);
    }

    @Override
    public List<OperLog> listOperLogs(String title, String operName, String status) {
        LambdaQueryWrapper<OperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(OperLog::getOperTime);
        if (StringUtils.hasText(title)) {
            wrapper.like(OperLog::getTitle, title);
        }
        if (StringUtils.hasText(operName)) {
            wrapper.like(OperLog::getOperName, operName);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(OperLog::getStatus, status);
        }
        return list(wrapper);
    }
}
