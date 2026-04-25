package com.josp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统角色实体。
 *
 * <p>表示 JOSP RBAC 系统中的安全角色。
 * 用户通过 {@code account_role} 关联表分配角色，
 * 每个角色通过 {@code sys_role_menu} 授予菜单权限。
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@TableName("sys_role")
public class Role {

    /**
     * 主键，使用自动分配的雪花ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 角色显示名称（例如："系统管理员"）
     */
    private String name;

    /**
     * 角色唯一编码，用于权限校验（例如："ROLE_ADMIN"）
     */
    private String code;

    /**
     * 显示顺序，下拉列表中值越小越靠前
     */
    private Integer sort;

    /**
     * 启用状态：1 = 启用，0 = 禁用
     */
    private Integer status;

    /**
     * 角色用途的可选描述
     */
    private String remark;

    /**
     * 记录创建时间
     */
    private LocalDateTime createTime;

    /**
     * 记录最后更新时间
     */
    private LocalDateTime updateTime;
}
