package com.josp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统菜单/权限实体。
 *
 * <p>同时建模 Vue Router 路由（type=1/2）和 SPA 组件权限（type=3）。
 * Menu 记录通过 parentId 自引用形成树形结构。
 * 角色到菜单的多对多映射存储在 {@code sys_role_menu} 表中。
 *
 * <p>Type 类型值：
 * <ul>
 *   <li>1 = 目录（父节点，无组件）</li>
 *   <li>2 = 菜单（带组件的实际路由）</li>
 *   <li>3 = 按钮/操作权限（侧边栏不显示，用于权限校验）</li>
 * </ul>
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@TableName("sys_menu")
public class Menu implements Serializable {

    /**
     * 主键，使用雪花ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 父菜单ID。
     * 0 = 根节点（顶级目录或独立路由）。
     */
    private Long parentId;

    /**
     * 侧边栏显示的标题
     */
    private String name;

    /**
     * 菜单类型：1=目录，2=菜单，3=按钮/操作。
     * 类型3的项不在侧边栏渲染，但会授予角色权限。
     */
    private Integer type;

    /**
     * Vue Router 路径段。
     * 绝对路径以"/"开头，拼接在基础路径之后；
     * 相对路径拼接在父路径之后。
     */
    private String path;

    /**
     * Vue 组件文件路径（例如："system/user/index"）。
     * type=1 目录为 null；type=3 按钮也为 null。
     */
    private String component;

    /**
     * Element Plus 图标名称（例如："user", "setting"）
     */
    private String icon;

    /**
     * 兄弟节点间的排序顺序；值越小越靠前
     */
    private Integer sort;

    /**
     * 可见性标志：1 = 在侧边栏显示，0 = 隐藏。
     * 隐藏的菜单（type=2）仍可通过直接 URL 访问。
     */
    private Integer visible;

    /**
     * type=1 目录时：用户导航到父路径时的重定向目标。
     * 非目录菜单此字段为空或不存在。
     */
    private String redirect;

    /**
     * type=3 按钮的权限字符串（例如："sys:user:add"）。
     * 后端通过 {@code @PreAuthorize("hasAuthority('...')"} 校验。
     */
    private String perm;

    /**
     * 该路由的组件是否应通过 {@code <keep-alive>} 保持存活。
     * 1 = 保持存活，0 = 不保持存活。
     */
    private Integer keepAlive;

    /**
     * type=1 目录：即使只有一个子节点是否也始终显示外层包装 div（避免某些布局中的重定向循环）。1=是，0=否。
     */
    private Integer alwaysShow;

    /**
     * 记录创建时间
     */
    private LocalDateTime createTime;

    /**
     * 记录最后更新时间
     */
    private LocalDateTime updateTime;
}
