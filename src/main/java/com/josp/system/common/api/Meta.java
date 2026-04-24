package com.josp.system.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Vue Router "meta" field for a dynamic route.
 *
 * <p>Attached to each RouteVO to carry sidebar rendering instructions
 * and access-control hints for the front-end.
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meta implements Serializable {

    /** Sidebar display title */
    private String title;

    /** Element Plus icon component name */
    private String icon;

    /** If true, hide this route's entry from the sidebar (route is still accessible via URL) */
    private Boolean hidden;

    /** If true, wrap the component in {@code <keep-alive>} to cache its state */
    private Boolean keepAlive;

    /**
     * If true, always render a wrapper div for single-child directories
     * (avoids redirect loops in some layout configurations). Type-1 directories only.
     */
    private Boolean alwaysShow;

    /** (Optional) Roles permitted to access this route; overrides controller-level @PreAuthorize */
    private List<String> roles;
}
