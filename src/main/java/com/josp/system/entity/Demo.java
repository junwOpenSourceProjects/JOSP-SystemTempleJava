package com.josp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 示例表格实体
 */
@Data
@TableName("demo")
public class Demo {

    /**
     * 主键 - 使用雪花算法生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 阅读量
     */
    private Integer pageviews;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDate timestamp;
}
