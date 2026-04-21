package com.josp.system.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 菜单DTO
 */
@Data
public class MenuDTO implements Serializable {

    private Long id;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 类型：0-目录，1-菜单，2-按钮
     */
    private Integer type;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否显示：0-隐藏，1-显示
     */
    private Integer visible;

    /**
     * 重定向地址
     */
    private String redirect;

    /**
     * 权限标识
     */
    private String perm;

    /**
     * 是否缓存：0-否，1-是
     */
    private Integer keepAlive;

    /**
     * 是否总是显示：0-否，1-是
     */
    private Integer alwaysShow;
}
