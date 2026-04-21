package com.josp.system.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 部门DTO
 */
@Data
public class DeptDTO implements Serializable {

    private Long id;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 部门编码
     */
    private String code;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态(0:禁用,1:正常)
     */
    private Integer status;
}
