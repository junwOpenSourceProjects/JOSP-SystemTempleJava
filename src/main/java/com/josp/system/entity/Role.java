package com.josp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * System Role Entity.
 *
 * <p>Represents a security role in the JOSP RBAC system.
 * Users are assigned roles via the {@code account_role} join table,
 * and each role is granted menu permissions via {@code sys_role_menu}.
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@TableName("sys_role")
public class Role {
    /** Primary key, auto-assigned snowflake ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** Display name of the role (e.g. "System Administrator") */
    private String name;

    /** Unique role code used in permission checks (e.g. "ROLE_ADMIN") */
    private String code;

    /** Display order, smaller values appear first in dropdown lists */
    private Integer sort;

    /** Active status: 1 = enabled, 0 = disabled */
    private Integer status;

    /** Optional description of the role's purpose */
    private String remark;

    /** Record creation timestamp */
    private LocalDateTime createTime;

    /** Record last-update timestamp */
    private LocalDateTime updateTime;
}
