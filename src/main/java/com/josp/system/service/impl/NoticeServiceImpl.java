package com.josp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.josp.system.common.api.PageResult;
import com.josp.system.common.utils.PageUtils;
import com.josp.system.dao.NoticeMapper;
import com.josp.system.entity.Notice;
import com.josp.system.service.NoticeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 通知公告服务实现类
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public PageResult<Notice> pageNotices(int pageNum, int pageSize, String title, Integer type, Integer status) {
        Page<Notice> page = PageUtils.buildPage(pageNum, pageSize);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Notice::getIsTop)
               .orderByDesc(Notice::getPublishTime);
        if (StringUtils.hasText(title)) {
            wrapper.like(Notice::getTitle, title);
        }
        if (type != null) {
            wrapper.eq(Notice::getType, type);
        }
        if (status != null) {
            wrapper.eq(Notice::getStatus, status);
        }
        Page<Notice> resultPage = page(page, wrapper);
        return PageUtils.buildPageResult(resultPage);
    }

    @Override
    public Notice getNoticeById(Long id) {
        return getById(id);
    }

    @Override
    public boolean createNotice(Notice notice) {
        notice.setCreateTime(LocalDateTime.now());
        notice.setUpdateTime(LocalDateTime.now());
        if (notice.getStatus() == null) {
            notice.setStatus(0); // 草稿
        }
        if (notice.getViewCount() == null) {
            notice.setViewCount(0);
        }
        return save(notice);
    }

    @Override
    public boolean updateNotice(Notice notice) {
        notice.setUpdateTime(LocalDateTime.now());
        return updateById(notice);
    }

    @Override
    public boolean deleteNotice(Long id) {
        return removeById(id);
    }

    @Override
    public boolean publishNotice(Long id) {
        Notice notice = getById(id);
        if (notice == null) {
            return false;
        }
        notice.setStatus(1);
        notice.setPublishTime(LocalDateTime.now());
        notice.setUpdateTime(LocalDateTime.now());
        return updateById(notice);
    }

    @Override
    public boolean withdrawNotice(Long id) {
        Notice notice = getById(id);
        if (notice == null) {
            return false;
        }
        notice.setStatus(2);
        notice.setUpdateTime(LocalDateTime.now());
        return updateById(notice);
    }

    @Override
    public boolean toggleTop(Long id) {
        Notice notice = getById(id);
        if (notice == null) {
            return false;
        }
        notice.setIsTop(notice.getIsTop() == 1 ? 0 : 1);
        notice.setUpdateTime(LocalDateTime.now());
        return updateById(notice);
    }

    @Override
    public boolean incrementViewCount(Long id) {
        return baseMapper.incrementViewCount(id) > 0;
    }
}
