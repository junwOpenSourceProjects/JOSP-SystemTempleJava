-- ===============================================
-- JOSP-SystemTempleJava 数据库初始化脚本
-- 数据库名: postgraduate
-- ===============================================

-- 1. 登录用户表
CREATE TABLE IF NOT EXISTS `login_user` (
    `id`            BIGINT        NOT NULL COMMENT '主键',
    `name`          VARCHAR(50)   DEFAULT NULL COMMENT '姓名',
    `username`      VARCHAR(50)   NOT NULL COMMENT '用户名',
    `password`      VARCHAR(200)  NOT NULL COMMENT '密码',
    `phone`         VARCHAR(20)   DEFAULT NULL COMMENT '手机号',
    `sex`           VARCHAR(10)   DEFAULT NULL COMMENT '性别',
    `id_number`     VARCHAR(20)   DEFAULT NULL COMMENT '身份证号',
    `status`        INT           DEFAULT 1 COMMENT '状态 0:禁用，1:正常',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user`   BIGINT        DEFAULT NULL COMMENT '创建人',
    `update_user`   BIGINT        DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录用户表';

-- 2. 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id`            BIGINT        NOT NULL COMMENT '主键',
    `name`          VARCHAR(50)   NOT NULL COMMENT '角色名称',
    `code`          VARCHAR(50)   NOT NULL COMMENT '角色编码',
    `sort`          INT           DEFAULT 0 COMMENT '排序',
    `status`        INT           DEFAULT 1 COMMENT '状态 0:禁用，1:正常',
    `remark`        VARCHAR(200)  DEFAULT NULL COMMENT '备注',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 3. 用户角色关联表 (关键缺表)
CREATE TABLE IF NOT EXISTS `account_role` (
    `id`            BIGINT        NOT NULL COMMENT '主键',
    `user_id`       BIGINT        NOT NULL COMMENT '用户ID',
    `role_id`       BIGINT        NOT NULL COMMENT '角色ID',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 4. 菜单表
CREATE TABLE IF NOT EXISTS `sys_menu` (
    `id`            BIGINT        NOT NULL COMMENT '主键',
    `parent_id`     BIGINT        DEFAULT 0 COMMENT '父菜单ID',
    `name`          VARCHAR(50)   NOT NULL COMMENT '菜单名称',
    `type`          INT           NOT NULL COMMENT '菜单类型 1:目录 2:菜单 3:按钮',
    `path`          VARCHAR(200)  DEFAULT NULL COMMENT '路由地址',
    `component`     VARCHAR(255)  DEFAULT NULL COMMENT '组件路径',
    `icon`          VARCHAR(50)   DEFAULT NULL COMMENT '菜单图标',
    `sort`          INT           DEFAULT 0 COMMENT '排序',
    `visible`       INT           DEFAULT 1 COMMENT '是否显示 0:隐藏 1:显示',
    `redirect`      VARCHAR(255)  DEFAULT NULL COMMENT '重定向地址',
    `perm`          VARCHAR(100)  DEFAULT NULL COMMENT '权限标识',
    `keep_alive`    INT           DEFAULT 1 COMMENT '是否缓存 0:否 1:是',
    `always_show`   INT           DEFAULT 1 COMMENT '是否总是显示 0:否 1:是',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 5. 角色菜单关联表
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `id`            BIGINT        NOT NULL COMMENT '主键',
    `role_id`       BIGINT        NOT NULL COMMENT '角色ID',
    `menu_id`       BIGINT        NOT NULL COMMENT '菜单ID',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 6. 部门表
CREATE TABLE IF NOT EXISTS `sys_dept` (
    `id`            BIGINT        NOT NULL COMMENT '主键',
    `name`          VARCHAR(50)   NOT NULL COMMENT '部门名称',
    `parent_id`     BIGINT        DEFAULT 0 COMMENT '父部门ID',
    `sort`          INT           DEFAULT 0 COMMENT '排序',
    `status`        INT           DEFAULT 1 COMMENT '状态 0:禁用 1:正常',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 7. 字典类型表
CREATE TABLE IF NOT EXISTS `dict_type` (
    `id`            BIGINT        NOT NULL COMMENT '主键',
    `name`          VARCHAR(50)   NOT NULL COMMENT '字典名称',
    `code`          VARCHAR(50)   NOT NULL COMMENT '字典编码',
    `status`        INT           DEFAULT 1 COMMENT '状态 0:禁用 1:正常',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- 8. 字典数据表
CREATE TABLE IF NOT EXISTS `dict_data` (
    `id`            BIGINT        NOT NULL COMMENT '主键',
    `dict_type_id`  BIGINT        NOT NULL COMMENT '字典类型ID',
    `label`         VARCHAR(100)  NOT NULL COMMENT '字典标签',
    `value`         VARCHAR(100)  NOT NULL COMMENT '字典值',
    `sort`          INT           DEFAULT 0 COMMENT '排序',
    `status`        INT           DEFAULT 1 COMMENT '状态 0:禁用 1:正常',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- 9. 操作日志表
CREATE TABLE IF NOT EXISTS `oper_log` (
    `id`            BIGINT        NOT NULL COMMENT '主键',
    `title`         VARCHAR(200)  DEFAULT NULL COMMENT '操作标题',
    `business_type` INT           DEFAULT 0 COMMENT '业务类型',
    `method`        VARCHAR(200)  DEFAULT NULL COMMENT '方法名',
    `request_type`  VARCHAR(20)   DEFAULT NULL COMMENT '请求方式',
    `operator_type` INT           DEFAULT 0 COMMENT '操作类型',
    `operator_name` VARCHAR(50)  DEFAULT NULL COMMENT '操作人',
    `url`           VARCHAR(255)  DEFAULT NULL COMMENT '请求URL',
    `ip`            VARCHAR(50)   DEFAULT NULL COMMENT 'IP地址',
    `location`      VARCHAR(200)  DEFAULT NULL COMMENT '操作地点',
    `param`         TEXT          DEFAULT NULL COMMENT '请求参数',
    `result`        TEXT          DEFAULT NULL COMMENT '返回结果',
    `status`        INT           DEFAULT 0 COMMENT '操作状态 0:异常 1:正常',
    `error_msg`     TEXT          DEFAULT NULL COMMENT '错误消息',
    `oper_time`     DATETIME      DEFAULT NULL COMMENT '操作时间',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 10. 登录日志表
CREATE TABLE IF NOT EXISTS `login_log` (
    `id`            BIGINT        NOT NULL COMMENT '主键',
    `username`      VARCHAR(50)   DEFAULT NULL COMMENT '用户名',
    `ip`            VARCHAR(50)   DEFAULT NULL COMMENT 'IP地址',
    `address`       VARCHAR(200)  DEFAULT NULL COMMENT '登录地点',
    `browser`       VARCHAR(100)  DEFAULT NULL COMMENT '浏览器',
    `os`            VARCHAR(100)  DEFAULT NULL COMMENT '操作系统',
    `status`        INT           DEFAULT 1 COMMENT '登录状态 0:失败 1:成功',
    `msg`           VARCHAR(200)  DEFAULT NULL COMMENT '提示消息',
    `login_time`    DATETIME      DEFAULT NULL COMMENT '登录时间',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- 11. 通知公告表
CREATE TABLE IF NOT EXISTS `notice` (
    `id`            BIGINT        NOT NULL COMMENT '主键',
    `title`         VARCHAR(200)  NOT NULL COMMENT '公告标题',
    `content`       TEXT          DEFAULT NULL COMMENT '公告内容',
    `type`          INT           DEFAULT 1 COMMENT '公告类型 1:通知 2:公告',
    `status`        INT           DEFAULT 1 COMMENT '状态 0:禁用 1:正常',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- ===============================================
-- 初始化测试数据
-- ===============================================

-- 插入超级管理员角色 (密码: 123456，BCrypt加密)
INSERT INTO `sys_role` (`id`, `name`, `code`, `sort`, `status`, `remark`, `create_time`) VALUES
(1, '超级管理员', 'admin', 1, 1, '系统超级管理员', NOW()),
(2, '普通用户', 'user', 2, 1, '普通用户角色', NOW());

-- 插入测试用户 (用户名: admin, 密码: 123456)
INSERT INTO `login_user` (`id`, `name`, `username`, `password`, `phone`, `sex`, `status`, `create_time`) VALUES
(1, '系统管理员', 'admin', '$2a$10$hGVg6EGqVQOd5KTH5f/14OIC8QWmMzXuw4TFz7BCm5DpXF2yxorUy', '13800138000', '男', 1, NOW());

-- 关联用户和角色
INSERT INTO `account_role` (`id`, `user_id`, `role_id`, `create_time`) VALUES
(1, 1, 1, NOW());

-- 插入基础菜单
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `perm`, `keep_alive`, `always_show`, `create_time`) VALUES
(1, 0, '系统管理', 1, '/system', NULL, 'Setting', 1, 1, NULL, 0, 1, NOW()),
(2, 1, '用户管理', 2, '/system/users', 'system/users/index', 'User', 1, 1, 'system:users:list', 1, 1, NOW()),
(3, 1, '角色管理', 2, '/system/roles', 'system/roles/index', 'Role', 2, 1, 'system:roles:list', 1, 1, NOW()),
(4, 1, '菜单管理', 2, '/system/menus', 'system/menus/index', 'Menu', 3, 1, 'system:menus:list', 1, 1, NOW()),
(5, 0, '首页', 1, '/dashboard', 'dashboard/index', 'HomeFilled', 0, 1, NULL, 0, 0, NOW());

-- 角色菜单关联
INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`, `create_time`) VALUES
(1, 1, 1, NOW()),
(2, 1, 2, NOW()),
(3, 1, 3, NOW()),
(4, 1, 4, NOW()),
(5, 1, 5, NOW());
