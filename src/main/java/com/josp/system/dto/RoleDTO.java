package com.josp.system.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色DTO
 */
@Data
public class RoleDTO implements Serializable {

    private Long id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 菜单ID列表
     */
    private java.util.List<Long> menuIds;
}
