package com.josp.system.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Vue Router route descriptor sent to the front-end for dynamic route registration.
 *
 * <p>Corresponds to the element-plus sidebar menu router structure.
 * The front-end's {@code src/plugins/permission.ts} consumes this
 * via {@code router.addRoute(...)} to inject routes at runtime.
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteVO implements Serializable {

    /**
     * Route name (must match the component's "name" option for keep-alive to work).
     * Also used as the unique key when adding routes dynamically.
     */
    private String name;

    /**
     * URL path segment.
     * If it starts with "/", it is absolute; otherwise it is appended to the parent's path.
     */
    private String path;

    /**
     * Vue component path, e.g. "system/user/index".
     * Corresponds to the file under {@code src/views/}.
     */
    private String component;

    /**
     * Redirect target when the user visits this path directly.
     * Only used for type=1 (directory) menus.
     */
    private String redirect;

    /** Sidebar/meta rendering instructions */
    private Meta meta;

    /**
     * Child routes. Only populated for type=1 (directory) menus.
     * An empty list means this is a leaf route.
     */
    private List<RouteVO> children = new ArrayList<>();
}
