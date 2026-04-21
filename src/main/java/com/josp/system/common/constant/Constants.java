package com.josp.system.common.constant;

/**
 * 系统常量类
 */
public class Constants {

    /**
     * 成功状态码
     */
    public static final int SUCCESS_CODE = 200;

    /**
     * 错误状态码
     */
    public static final int ERROR_CODE = 500;

    /**
     * 请求ID
     */
    public static final String REQUEST_ID = "X-Request-Id";

    /**
     * 当前用户
     */
    public static final String CURRENT_USER = "current_user";

    /**
     * JWT Token
     */
    public static final String JWT_TOKEN = "Authorization";

    /**
     * JWT Token前缀
     */
    public static final String JWT_TOKEN_PREFIX = "Bearer ";

    /**
     * 分页参数
     */
    public static final String PAGE_NUM = "pageNum";
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 默认分页页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大分页大小
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 字符编码
     */
    public static final String UTF8 = "UTF-8";

    /**
     * JSON媒体类型
     */
    public static final String APPLICATION_JSON = "application/json";

    /**
     * 超级管理员角色ID
     */
    public static final Long SUPER_ADMIN_ROLE_ID = 1L;

    /**
     * 超级管理员用户ID
     */
    public static final Long SUPER_ADMIN_USER_ID = 1L;

    /**
     * 缓存前缀
     */
    public static final String CACHE_PREFIX = "josp:";

    /**
     * 用户缓存前缀
     */
    public static final String USER_CACHE_PREFIX = CACHE_PREFIX + "user:";

    /**
     * 菜单缓存前缀
     */
    public static final String MENU_CACHE_PREFIX = CACHE_PREFIX + "menu:";

    /**
     * 角色缓存前缀
     */
    public static final String ROLE_CACHE_PREFIX = CACHE_PREFIX + "role:";

    /**
     * 权限缓存前缀
     */
    public static final String PERMISSION_CACHE_PREFIX = CACHE_PREFIX + "permission:";

    /**
     * Token过期时间（毫秒）
     */
    public static final long TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;

    /**
     * RefreshToken过期时间（毫秒）
     */
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

    private Constants() {
        // 私有构造函数，防止实例化
    }
}
