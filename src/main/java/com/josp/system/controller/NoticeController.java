package com.josp.system.controller;

import com.josp.system.common.api.PageResult;
import com.josp.system.common.api.Result;
import com.josp.system.entity.Notice;
import com.josp.system.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知公告控制器
 */
@Tag(name = "通知公告接口")
@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "分页查询通知公告")
    @GetMapping("/page")
    public Result<PageResult<Notice>> pageNotices(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        return Result.success(noticeService.pageNotices(pageNum, pageSize, title, type, status));
    }

    @Operation(summary = "根据ID获取通知公告详情")
    @GetMapping("/{id}")
    public Result<Notice> getNoticeById(@PathVariable Long id) {
        Notice notice = noticeService.getNoticeById(id);
        if (notice != null) {
            noticeService.incrementViewCount(id);
        }
        return Result.success(notice);
    }

    @Operation(summary = "创建通知公告")
    @PostMapping
    public Result<Boolean> createNotice(@RequestBody Notice notice) {
        // 设置发布者信息
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        notice.setPublisherName(username);
        return Result.success(noticeService.createNotice(notice));
    }

    @Operation(summary = "更新通知公告")
    @PutMapping("/{id}")
    public Result<Boolean> updateNotice(@PathVariable Long id, @RequestBody Notice notice) {
        notice.setId(id);
        return Result.success(noticeService.updateNotice(notice));
    }

    @Operation(summary = "删除通知公告")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteNotice(@PathVariable Long id) {
        return Result.success(noticeService.deleteNotice(id));
    }

    @Operation(summary = "批量删除通知公告")
    @DeleteMapping("/batch")
    public Result<Boolean> deleteNotices(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            noticeService.deleteNotice(id);
        }
        return Result.success(true);
    }

    @Operation(summary = "发布通知公告")
    @PutMapping("/{id}/publish")
    public Result<Boolean> publishNotice(@PathVariable Long id) {
        return Result.success(noticeService.publishNotice(id));
    }

    @Operation(summary = "撤回通知公告")
    @PutMapping("/{id}/withdraw")
    public Result<Boolean> withdrawNotice(@PathVariable Long id) {
        return Result.success(noticeService.withdrawNotice(id));
    }

    @Operation(summary = "置顶/取消置顶通知公告")
    @PutMapping("/{id}/toggle-top")
    public Result<Boolean> toggleTop(@PathVariable Long id) {
        return Result.success(noticeService.toggleTop(id));
    }

    @Operation(summary = "获取已发布的通知公告列表")
    @GetMapping("/published")
    public Result<List<Notice>> getPublishedNotices() {
        List<Notice> notices = noticeService.lambdaQuery()
                .eq(Notice::getStatus, 1)
                .orderByDesc(Notice::getIsTop)
                .orderByDesc(Notice::getPublishTime)
                .list();
        return Result.success(notices);
    }
}
