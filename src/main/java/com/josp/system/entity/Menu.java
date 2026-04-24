package com.josp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * System Menu / Permission Entity.
 *
 * <p>Models both Vue Router routes (type=1/2) and SPA component-permissions (type=3).
 * Menu records form a tree via parentId self-reference.
 * Role-to-menu many-to-many mapping is stored in {@code sys_role_menu}.
 *
 * <p>Type values (MenuType):
 * <ul>
 *   <li>1 = Directory (parent node, no component)</li>
 *   <li>2 = Menu (actual route with component)</li>
 *   <li>3 = Button/Operation permission (invisible in sidebar, used for perms check)</li>
 * </ul>
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@TableName("sys_menu")
public class Menu implements Serializable {

    /** Primary key, snowflake ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Parent menu ID.
     * 0 = root node (top-level directory or standalone route).
     */
    private Long parentId;

    /** Display title shown in the sidebar */
    private String name;

    /**
     * Menu type: 1=Directory, 2=Menu, 3=Button/Operation.
     * Type 3 items are not rendered in the sidebar but grant perms to roles.
     */
    private Integer type;

    /**
     * Vue Router path segment.
     * Absolute paths start with "/" and are appended to the base path;
     * relative paths are appended to the parent's path.
     */
    private String path;

    /**
     * Vue component file path (e.g. "system/user/index").
     * null for type=1 directories; type=3 buttons also leave this null.
     */
    private String component;

    /** Element Plus icon name shown in the sidebar (e.g. "user", "setting") */
    private String icon;

    /** Sort order among siblings; lower values appear first */
    private Integer sort;

    /**
     * Visibility flag: 1 = visible in sidebar, 0 = hidden.
     * Hidden menus (type=2) are still accessible via direct URL.
     */
    private Integer visible;

    /**
     * Redirect target for type=1 directories when user navigates to the parent path.
     * Absent or empty for non-directory menus.
     */
    private String redirect;

    /**
     * Permission string for type=3 buttons (e.g. "sys:user:add").
     * Checked via {@code @PreAuthorize("hasAuthority('...')")} in back-end.
     */
    private String perm;

    /**
     * Whether this route's component should be kept alive via {@code <keep-alive>}.
     * 1 = keep alive, 0 = do not keep alive.
     */
    private Integer keepAlive;

    /**
     * For type=1 directories: whether to always show the wrapper div even if only
     * one child is present (to avoid redirect loop in some layouts). 1=yes, 0=no.
     */
    private Integer alwaysShow;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
