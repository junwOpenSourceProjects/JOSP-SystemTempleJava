package com.josp.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门表实体。
 *
 * <p>存储组织架构中的部门信息，支持树形层级结构。
 * 通过 parentId 自引用形成部门树，用于权限隔离和数据归属。
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@TableName("sys_dept")
public class Dept implements Serializable {

    /**
     * 主键，雪花算法生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 父部门ID。
     * 0 表示顶级部门（根节点）。
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 部门编码，用于系统间对接
     */
    private String code;

    /**
     * 排序号，同级部门按此字段升序排列
     */
    private Integer sort;

    /**
     * 部门负责人姓名
     */
    private String leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    /**
     * 修改人ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    /**
     * 子部门列表（用于树形结构展示）。
     * <p>注意：此字段不对应数据库列，仅用于前端组件树形展示。
     */
    @TableField(exist = false)
    private List<Dept> children;
}
