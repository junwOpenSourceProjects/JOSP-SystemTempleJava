package com.josp.system.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 字典类型DTO
 */
@Data
public class DictTypeDTO implements Serializable {

    private Long id;

    /**
     * 字典类型名称
     */
    private String name;

    /**
     * 字典类型编码
     */
    private String code;

    /**
     * 状态(0:禁用,1:正常)
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
