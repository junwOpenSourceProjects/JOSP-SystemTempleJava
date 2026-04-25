-- ============================================
-- 数据库设计文档
-- 项目: JOSP-SystemTempleJava
-- 版本: 1.0.0
-- 更新日期: 2026-04-21
-- ============================================

-- 创建数据库(如果不存在)
#  CREATE DATABASE IF NOT EXISTS josp_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
#  USE josp_system;

-- ============================================
-- 雪花ID生成器（MySQL函数实现）
-- ============================================
DROP FUNCTION IF EXISTS `next_id`;
DELIMITER $$
CREATE FUNCTION `next_id`(bigint_value BIGINT) RETURNS BIGINT(19)
    NO SQL
BEGIN
    DECLARE epoch BIGINT DEFAULT 1704067200000; -- 2024-01-01 00:00:00
    DECLARE worker_id INT DEFAULT 1;
    DECLARE sequence INT DEFAULT 0;
    DECLARE timestamp BIGINT;
    DECLARE result BIGINT;

    SET timestamp = UNIX_TIMESTAMP() * 1000 - epoch;
    SET sequence = FLOOR(RAND() * 1000) % 1024;

    SET result = (timestamp % 2^41) * 2^22 | (worker_id % 32) * 2^17 | sequence;

    RETURN bigint_value + result;
END$$
DELIMITER ;

-- ============================================
-- 1. 登录用户表 (login_user)
-- ============================================
DROP TABLE IF EXISTS `login_user`;
CREATE TABLE `login_user` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `name` VARCHAR(100) DEFAULT NULL COMMENT '姓名',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) DEFAULT NULL COMMENT '密码',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `sex` VARCHAR(10) DEFAULT NULL COMMENT '性别',
    `id_number` VARCHAR(30) DEFAULT NULL COMMENT '身份证号',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` BIGINT(19) DEFAULT NULL COMMENT '创建人ID',
    `update_user` BIGINT(19) DEFAULT NULL COMMENT '修改人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录用户表';

-- ============================================
-- 2. 角色表 (sys_role)
-- ============================================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ============================================
-- 3. 用户角色关联表 (account_role)
-- ============================================
DROP TABLE IF EXISTS `account_role`;
CREATE TABLE `account_role` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `user_id` BIGINT(19) NOT NULL COMMENT '用户ID',
    `role_id` BIGINT(19) NOT NULL COMMENT '角色ID',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ============================================
-- 3.1 角色菜单关联表 (sys_role_menu)
-- ============================================
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `role_id` BIGINT(19) NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT(19) NOT NULL COMMENT '菜单ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_menu_id` (`menu_id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- ============================================
-- 4. 菜单表 (sys_menu)
-- ============================================
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `parent_id` BIGINT(19) DEFAULT 0 COMMENT '父菜单ID',
    `name` VARCHAR(100) NOT NULL COMMENT '菜单名称',
    `type` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '类型：0-目录，1-菜单，2-按钮',
    `path` VARCHAR(255) DEFAULT NULL COMMENT '路由路径',
    `component` VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `visible` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否显示：0-隐藏，1-显示',
    `redirect` VARCHAR(255) DEFAULT NULL COMMENT '重定向地址',
    `perm` VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    `keep_alive` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否缓存：0-否，1-是',
    `always_show` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否总是显示：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_type` (`type`),
    KEY `idx_visible` (`visible`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

-- ============================================
-- 5. 部门表 (sys_dept)
-- ============================================
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `parent_id` BIGINT(19) NOT NULL DEFAULT 0 COMMENT '父部门ID',
    `name` VARCHAR(100) NOT NULL COMMENT '部门名称',
    `code` VARCHAR(50) DEFAULT NULL COMMENT '部门编码',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `leader` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` BIGINT(19) DEFAULT NULL COMMENT '创建人ID',
    `update_user` BIGINT(19) DEFAULT NULL COMMENT '修改人ID',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- ============================================
-- 6. 字典类型表 (sys_dict_type)
-- ============================================
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `name` VARCHAR(100) NOT NULL COMMENT '字典类型名称',
    `code` VARCHAR(100) NOT NULL COMMENT '字典类型编码',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` BIGINT(19) DEFAULT NULL COMMENT '创建人ID',
    `update_user` BIGINT(19) DEFAULT NULL COMMENT '修改人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- ============================================
-- 7. 字典数据表 (sys_dict_data)
-- ============================================
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `dict_type_id` BIGINT(19) NOT NULL COMMENT '字典类型ID',
    `label` VARCHAR(100) NOT NULL COMMENT '字典标签',
    `value` VARCHAR(255) NOT NULL COMMENT '字典值',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` BIGINT(19) DEFAULT NULL COMMENT '创建人ID',
    `update_user` BIGINT(19) DEFAULT NULL COMMENT '修改人ID',
    PRIMARY KEY (`id`),
    KEY `idx_dict_type_id` (`dict_type_id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';

-- ============================================
-- 8. 岗位表 (sys_post)
-- ============================================
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `name` VARCHAR(100) NOT NULL COMMENT '岗位名称',
    `code` VARCHAR(50) NOT NULL COMMENT '岗位编码',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` BIGINT(19) DEFAULT NULL COMMENT '创建人ID',
    `update_user` BIGINT(19) DEFAULT NULL COMMENT '修改人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_code` (`code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位表';

-- ============================================
-- 9. 操作日志表 (sys_oper_log)
-- ============================================
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `title` VARCHAR(255) DEFAULT NULL COMMENT '操作标题',
    `business_type` VARCHAR(50) DEFAULT NULL COMMENT '业务类型',
    `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    `request_method` VARCHAR(10) DEFAULT NULL COMMENT '请求方式',
    `operator_type` VARCHAR(20) DEFAULT NULL COMMENT '操作类别',
    `oper_id` BIGINT(19) DEFAULT NULL COMMENT '操作人ID',
    `oper_name` VARCHAR(100) DEFAULT NULL COMMENT '操作人名称',
    `dept_name` VARCHAR(100) DEFAULT NULL COMMENT '部门名称',
    `oper_url` VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
    `oper_ip` VARCHAR(50) DEFAULT NULL COMMENT '操作地址',
    `oper_location` VARCHAR(255) DEFAULT NULL COMMENT '操作地点',
    `oper_param` TEXT COMMENT '请求参数',
    `json_result` TEXT COMMENT '返回参数',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '操作状态：0-异常，1-正常',
    `error_msg` TEXT COMMENT '错误消息',
    `oper_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `cost_time` BIGINT(20) DEFAULT NULL COMMENT '消耗时间(毫秒)',
    PRIMARY KEY (`id`),
    KEY `idx_oper_time` (`oper_time`),
    KEY `idx_oper_id` (`oper_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================
-- 10. 登录日志表 (sys_login_log)
-- ============================================
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `user_id` BIGINT(19) DEFAULT NULL COMMENT '用户ID',
    `username` VARCHAR(100) DEFAULT NULL COMMENT '用户名',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT '登录IP地址',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '登录地点',
    `browser` VARCHAR(100) DEFAULT NULL COMMENT '浏览器类型',
    `os` VARCHAR(100) DEFAULT NULL COMMENT '操作系统',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '登录状态：0-失败，1-成功',
    `msg` VARCHAR(500) DEFAULT NULL COMMENT '提示消息',
    `login_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_username` (`username`),
    KEY `idx_login_time` (`login_time`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- ============================================
-- 11. 通知公告表 (sys_notice)
-- ============================================
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `title` VARCHAR(255) NOT NULL COMMENT '公告标题',
    `content` TEXT COMMENT '公告内容',
    `type` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '公告类型：1-通知，2-公告',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` BIGINT(19) DEFAULT NULL COMMENT '创建人ID',
    `update_user` BIGINT(19) DEFAULT NULL COMMENT '修改人ID',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知公告表';

-- ============================================
-- 12. 系统配置表 (sys_config)
-- ============================================
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `name` VARCHAR(100) NOT NULL COMMENT '配置名称',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `value` VARCHAR(500) DEFAULT NULL COMMENT '配置值',
    `type` VARCHAR(20) NOT NULL DEFAULT 'string' COMMENT '配置类型：string, number, boolean, json',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` BIGINT(19) DEFAULT NULL COMMENT '创建人ID',
    `update_user` BIGINT(19) DEFAULT NULL COMMENT '修改人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ============================================
-- 13. 文件管理表 (sys_file)
-- ============================================
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件名称',
    `original_name` VARCHAR(255) DEFAULT NULL COMMENT '原始文件名',
    `file_suffix` VARCHAR(50) DEFAULT NULL COMMENT '文件后缀',
    `file_size` BIGINT(20) DEFAULT NULL COMMENT '文件大小(字节)',
    `file_path` VARCHAR(500) NOT NULL COMMENT '文件路径',
    `file_url` VARCHAR(500) DEFAULT NULL COMMENT '文件访问URL',
    `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型',
    `storage_type` VARCHAR(20) NOT NULL DEFAULT 'local' COMMENT '存储类型：local, oss, s3',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` BIGINT(19) DEFAULT NULL COMMENT '创建人ID',
    `update_user` BIGINT(19) DEFAULT NULL COMMENT '修改人ID',
    PRIMARY KEY (`id`),
    KEY `idx_file_type` (`file_type`),
    KEY `idx_create_user` (`create_user`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件管理表';

-- ============================================
-- 14. 在线用户表 (sys_online_user)
-- 说明: 基于Redis存储，MySQL仅用于备份/审计
-- ============================================
DROP TABLE IF EXISTS `sys_online_user`;
CREATE TABLE `sys_online_user` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `user_id` BIGINT(19) NOT NULL COMMENT '用户ID',
    `username` VARCHAR(100) NOT NULL COMMENT '用户名',
    `token` VARCHAR(500) NOT NULL COMMENT '登录Token',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT '登录IP',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '登录地址',
    `browser` VARCHAR(100) DEFAULT NULL COMMENT '浏览器',
    `os` VARCHAR(100) DEFAULT NULL COMMENT '操作系统',
    `login_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_token` (`token`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_username` (`username`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='在线用户表(Redis备份)';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入默认超级管理员角色
INSERT INTO `sys_role` (`id`, `name`, `code`, `sort`, `status`, `create_time`, `update_time`)
VALUES (751892017468610561, '超级管理员', 'SUPER_ADMIN', 1, 1, NOW(), NOW());

-- 插入默认普通用户角色
INSERT INTO `sys_role` (`id`, `name`, `code`, `sort`, `status`, `create_time`, `update_time`)
VALUES (751892017468610562, '普通用户', 'USER', 2, 1, NOW(), NOW());

-- 插入默认管理员用户 (密码: 123456)
INSERT INTO `login_user` (`id`, `name`, `username`, `password`, `phone`, `sex`, `status`, `create_time`, `update_time`)
VALUES (751892017468610560, '系统管理员', 'admin', '$2a$10$CII/R3vOc5aJL2ncqzSdAuMx9/rh5Mi9zqwXMfdYAJnvS8rPgfbwm', NULL, NULL, 1, NOW(), NOW());

-- 关联管理员和超级管理员角色
INSERT INTO `account_role` (`id`, `user_id`, `role_id`, `create_time`)
VALUES (751892017468610563, 751892017468610560, 751892017468610561, NOW());

-- 关联超级管理员和所有菜单
INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`, `create_time`) VALUES
(751892017468610700, 751892017468610561, 751892017468610101, NOW()),
(751892017468610701, 751892017468610561, 751892017468610401, NOW()),
(751892017468610702, 751892017468610561, 751892017468610102, NOW()),
(751892017468610703, 751892017468610561, 751892017468610103, NOW()),
(751892017468610704, 751892017468610561, 751892017468610104, NOW()),
(751892017468610705, 751892017468610561, 751892017468610105, NOW()),
(751892017468610706, 751892017468610561, 751892017468610106, NOW()),
(751892017468610707, 751892017468610561, 751892017468610107, NOW()),
(751892017468610708, 751892017468610561, 751892017468610402, NOW()),
(751892017468610709, 751892017468610561, 751892017468610403, NOW()),
(751892017468610710, 751892017468610561, 751892017468610404, NOW()),
(751892017468610711, 751892017468610561, 751892017468610201, NOW()),
(751892017468610712, 751892017468610561, 751892017468610202, NOW()),
(751892017468610713, 751892017468610561, 751892017468610203, NOW()),
(751892017468610714, 751892017468610561, 751892017468610204, NOW()),
(751892017468610715, 751892017468610561, 751892017468610301, NOW()),
(751892017468610716, 751892017468610561, 751892017468610302, NOW()),
(751892017468610717, 751892017468610561, 751892017468610303, NOW());

-- 插入示例字典类型
INSERT INTO `sys_dict_type` (`id`, `name`, `code`, `status`, `create_time`, `update_time`)
VALUES (751892017468610564, '用户状态', 'user_status', 1, NOW(), NOW());

-- 插入示例字典数据
INSERT INTO `sys_dict_data` (`id`, `dict_type_id`, `label`, `value`, `sort`, `status`, `create_time`, `update_time`)
VALUES (751892017468610565, 751892017468610564, '正常', '1', 1, 1, NOW(), NOW()),
       (751892017468610566, 751892017468610564, '禁用', '0', 2, 1, NOW(), NOW());

-- 插入部门数据（树形结构）
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `code`, `sort`, `leader`, `phone`, `email`, `status`, `create_time`, `update_time`) VALUES
(751892017468610001, 0, '总公司', 'HQ', 1, '张三', '13800138000', 'hq@company.com', 1, NOW(), NOW()),
(751892017468610002, 751892017468610001, '技术研发部', 'TECH', 1, '李四', '13800138001', 'tech@company.com', 1, NOW(), NOW()),
(751892017468610003, 751892017468610001, '产品运营部', 'OPS', 2, '王五', '13800138002', 'ops@company.com', 1, NOW(), NOW()),
(751892017468610004, 751892017468610001, '市场营销部', 'MKT', 3, '赵六', '13800138003', 'mkt@company.com', 1, NOW(), NOW()),
(751892017468610005, 751892017468610002, '前端开发组', 'FE', 1, '前端组长', '13800138004', 'fe@company.com', 1, NOW(), NOW()),
(751892017468610006, 751892017468610002, '后端开发组', 'BE', 2, '后端组长', '13800138005', 'be@company.com', 1, NOW(), NOW());

-- 插入菜单数据
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `type`, `path`, `component`, `icon`, `sort`, `visible`, `redirect`, `perm`, `keep_alive`, `always_show`, `create_time`, `update_time`) VALUES
-- 根目录
(751892017468610101, 0, '系统管理', 0, '/system', NULL, 'Setting', 1, 1, NULL, NULL, 0, 1, NOW(), NOW()),
-- 子菜单
(751892017468610102, 751892017468610101, '用户管理', 1, 'user', 'system/user/index', 'User', 1, 1, NULL, 'sys:user:list', 0, 0, NOW(), NOW()),
(751892017468610103, 751892017468610101, '角色管理', 1, 'role', 'system/role/index', 'Role', 2, 1, NULL, 'sys:role:list', 0, 0, NOW(), NOW()),
(751892017468610104, 751892017468610101, '菜单管理', 1, 'menu', 'system/menu/index', 'Menu', 3, 1, NULL, 'sys:menu:list', 0, 0, NOW(), NOW()),
(751892017468610105, 751892017468610101, '部门管理', 1, 'dept', 'system/dept/index', 'Office', 4, 1, NULL, 'sys:dept:list', 0, 0, NOW(), NOW()),
(751892017468610106, 751892017468610101, '字典管理', 1, 'dict', 'system/dict/index', 'Memo', 5, 1, NULL, 'sys:dict:list', 0, 0, NOW(), NOW()),
(751892017468610107, 751892017468610101, '系统配置', 1, 'config', 'system/config/index', 'Config', 6, 1, NULL, 'sys:config:list', 0, 0, NOW(), NOW()),
-- 按钮权限
(751892017468610201, 751892017468610102, '用户新增', 2, NULL, NULL, NULL, 1, 1, NULL, 'sys:user:add', 0, 0, NOW(), NOW()),
(751892017468610202, 751892017468610102, '用户编辑', 2, NULL, NULL, NULL, 2, 1, NULL, 'sys:user:edit', 0, 0, NOW(), NOW()),
(751892017468610203, 751892017468610102, '用户删除', 2, NULL, NULL, NULL, 3, 1, NULL, 'sys:user:delete', 0, 0, NOW(), NOW()),
(751892017468610204, 751892017468610102, '重置密码', 2, NULL, NULL, NULL, 4, 1, NULL, 'sys:user:password:reset', 0, 0, NOW(), NOW()),
(751892017468610301, 751892017468610103, '角色新增', 2, NULL, NULL, NULL, 1, 1, NULL, 'sys:role:add', 0, 0, NOW(), NOW()),
(751892017468610302, 751892017468610103, '角色编辑', 2, NULL, NULL, NULL, 2, 1, NULL, 'sys:role:edit', 0, 0, NOW(), NOW()),
(751892017468610303, 751892017468610103, '角色删除', 2, NULL, NULL, NULL, 3, 1, NULL, 'sys:role:delete', 0, 0, NOW(), NOW()),
-- 系统监控
(751892017468610401, 0, '系统监控', 0, '/monitor', NULL, 'Monitor', 2, 1, NULL, NULL, 0, 1, NOW(), NOW()),
(751892017468610402, 751892017468610401, '在线用户', 1, 'online', 'monitor/online/index', 'UserFilled', 1, 1, NULL, 'sys:online:list', 0, 0, NOW(), NOW()),
(751892017468610403, 751892017468610401, '登录日志', 1, 'login-log', 'monitor/log/login', 'Log', 2, 1, NULL, 'sys:login:list', 0, 0, NOW(), NOW()),
(751892017468610404, 751892017468610401, '操作日志', 1, 'oper-log', 'monitor/log/oper', 'Document', 3, 1, NULL, 'sys:oper:list', 0, 0, NOW(), NOW());

-- 插入更多字典类型
INSERT INTO `sys_dict_type` (`id`, `name`, `code`, `status`, `remark`, `create_time`, `update_time`) VALUES
(751892017468610570, '菜单类型', 'menu_type', 1, '菜单类型', NOW(), NOW()),
(751892017468610571, '部门状态', 'dept_status', 1, '部门状态', NOW(), NOW()),
(751892017468610572, '公告类型', 'notice_type', 1, '公告类型', NOW(), NOW());

-- 插入字典数据
INSERT INTO `sys_dict_data` (`id`, `dict_type_id`, `label`, `value`, `sort`, `status`, `remark`, `create_time`, `update_time`) VALUES
(751892017468610580, 751892017468610570, '目录', '0', 1, 1, '目录类型菜单', NOW(), NOW()),
(751892017468610581, 751892017468610570, '菜单', '1', 2, 1, '菜单类型页面', NOW(), NOW()),
(751892017468610582, 751892017468610570, '按钮', '2', 3, 1, '按钮/权限', NOW(), NOW()),
(751892017468610583, 751892017468610571, '正常', '1', 1, 1, '正常状态', NOW(), NOW()),
(751892017468610584, 751892017468610571, '禁用', '0', 2, 1, '禁用状态', NOW(), NOW()),
(751892017468610585, 751892017468610572, '通知', '1', 1, 1, '通知类型', NOW(), NOW()),
(751892017468610586, 751892017468610572, '公告', '2', 2, 1, '公告类型', NOW(), NOW());

-- 插入系统配置
INSERT INTO `sys_config` (`id`, `name`, `config_key`, `value`, `type`, `status`, `remark`, `create_time`, `update_time`) VALUES
(751892017468610600, '系统名称', 'sys.name', 'JOSP管理系统', 'string', 1, '系统名称', NOW(), NOW()),
(751892017468610601, '系统版本', 'sys.version', '1.0.0', 'string', 1, '系统版本', NOW(), NOW()),
(751892017468610602, '版权信息', 'sys.copyright', '© 2024 JOSP', 'string', 1, '版权信息', NOW(), NOW());

-- 插入岗位数据
INSERT INTO `sys_post` (`id`, `name`, `code`, `sort`, `status`, `remark`, `create_time`, `update_time`) VALUES
(751892017468610700, 'CEO', 'CEO', 1, 1, '首席执行官', NOW(), NOW()),
(751892017468610701, 'CTO', 'CTO', 2, 1, '首席技术官', NOW(), NOW()),
(751892017468610702, 'Java开发工程师', 'JAVA_DEV', 3, 1, 'Java开发工程师', NOW(), NOW()),
(751892017468610703, '前端开发工程师', 'FE_DEV', 4, 1, '前端开发工程师', NOW(), NOW());
