package com.josp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.josp.system.common.api.PageResult;
import com.josp.system.entity.Notice;

/**
 * 通知公告服务接口
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 分页查询通知公告
     */
    PageResult<Notice> pageNotices(int pageNum, int pageSize, String title, Integer type, Integer status);

    /**
     * 根据ID获取通知公告详情
     */
    Notice getNoticeById(Long id);

    /**
     * 创建通知公告
     */
    boolean createNotice(Notice notice);

    /**
     * 更新通知公告
     */
    boolean updateNotice(Notice notice);

    /**
     * 删除通知公告
     */
    boolean deleteNotice(Long id);

    /**
     * 发布通知公告
     */
    boolean publishNotice(Long id);

    /**
     * 撤回通知公告
     */
    boolean withdrawNotice(Long id);

    /**
     * 置顶/取消置顶通知公告
     */
    boolean toggleTop(Long id);

    /**
     * 增加浏览次数
     */
    boolean incrementViewCount(Long id);
}
