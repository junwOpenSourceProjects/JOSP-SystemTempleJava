package com.josp.system.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 字典数据DTO
 */
@Data
public class DictDataDTO implements Serializable {

    private Long id;

    /**
     * 字典类型ID
     */
    private Long dictTypeId;

    /**
     * 字典标签
     */
    private String label;

    /**
     * 字典值
     */
    private String value;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态(0:禁用,1:正常)
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
