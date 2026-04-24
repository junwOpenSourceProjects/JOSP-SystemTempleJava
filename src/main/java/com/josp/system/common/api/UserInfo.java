package com.josp.system.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Authenticated User Information returned by /getUserInfo.
 *
 * <p>Contains the user's identity and all granted permissions
 * so the front-end can drive role-based UI visibility.
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {

    /** Internal user ID (matches login_user.id) */
    private Long userId;

    /** Login username (unique) */
    private String username;

    /** Display name / nickname */
    private String nickname;

    /** URL of the user's avatar image */
    private String avatar;

    /** List of role codes granted to this user (e.g. ["admin", "editor"]) */
    private List<String> roles;

    /**
     * List of permission strings granted via menus of type=3.
     * e.g. ["sys:user:add", "sys:user:edit"]
     */
    private List<String> perms;
}
