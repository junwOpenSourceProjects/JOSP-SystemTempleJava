package com.josp.system.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.josp.system.common.api.PageResult;

import java.util.List;

/**
 * 分页工具类
 */
public class PageUtils {

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> buildPageResult(List<T> records, long total) {
        return new PageResult<>(records, total);
    }

    /**
     * 构建分页结果
     */
    @SuppressWarnings("unchecked")
    public static <T> PageResult<T> buildPageResult(IPage<?> page) {
        return new PageResult<>((List<T>) page.getRecords(), page.getTotal());
    }

    /**
     * 构建分页结果
     */
    @SuppressWarnings("unchecked")
    public static <T> PageResult<T> buildPageResult(IPage<?> page, Class<T> clazz) {
        return new PageResult<>((List<T>) page.getRecords(), page.getTotal());
    }

    /**
     * 构建分页结果（无数据）
     */
    public static <T> PageResult<T> emptyPageResult() {
        return new PageResult<>(List.of(), 0);
    }

    /**
     * 构建分页结果（无数据）
     */
    public static <T> PageResult<T> emptyPageResult(long total) {
        return new PageResult<>(List.of(), total);
    }

    /**
     * 构建MyBatis-Plus分页对象
     */
    public static <T> Page<T> buildPage(long pageNum, long pageSize) {
        return new Page<>(pageNum, pageSize);
    }

    /**
     * 构建MyBatis-Plus分页对象（带排序）
     * 注意：排序需要在查询时通过 QueryWrapper 设置
     */
    public static <T> Page<T> buildPage(long pageNum, long pageSize, String orderByField, boolean isAsc) {
        return new Page<>(pageNum, pageSize);
    }

    /**
     * 计算总页数
     */
    public static long calculateTotalPages(long total, long pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        return (total + pageSize - 1) / pageSize;
    }

    /**
     * 判断是否有下一页
     */
    public static boolean hasNextPage(long currentPage, long pageSize, long total) {
        return currentPage * pageSize < total;
    }

    /**
     * 判断是否有上一页
     */
    public static boolean hasPreviousPage(long currentPage) {
        return currentPage > 1;
    }
}
