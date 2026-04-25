package com.josp.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统配置实体。
 *
 * <p>存储系统运行参数配置，如分页大小、密码策略、业务开关等。
 * 配置键（configKey）全局唯一，用于程序中读取和缓存配置值。
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@TableName("sys_config")
@Schema(description = "系统配置")
public class Config implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，雪花算法生成
     */
    @Schema(description = "主键ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 配置名称，用于前端展示（例如："分页大小"）
     */
    @Schema(description = "配置名称")
    private String name;

    /**
     * 配置键名，唯一标识（程序中使用此键读取配置）
     */
    @Schema(description = "配置键名")
    @TableField("config_key")
    private String configKey;

    /**
     * 配置值，存储实际配置内容
     */
    @Schema(description = "配置值")
    private String value;

    /**
     * 配置类型：string, number, boolean, json
     */
    @Schema(description = "配置类型：string, number, boolean, json")
    private String type;

    /**
     * 状态：0-禁用，1-正常
     */
    @Schema(description = "状态：0-禁用，1-正常")
    private Integer status;

    /**
     * 备注说明
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    /**
     * 修改人ID
     */
    @Schema(description = "修改人ID")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
