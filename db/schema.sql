-- ----------------------------
-- Table structure for login_user
-- ----------------------------
DROP TABLE IF EXISTS `login_user`;
CREATE TABLE `login_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `sex` varchar(2) DEFAULT NULL COMMENT '性别',
  `id_number` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `status` int(1) DEFAULT '1' COMMENT '状态 0:禁用，1:正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='登录用户表';

INSERT INTO `login_user` (`id`, `name`, `username`, `password`, `sex`, `status`) VALUES 
(1, '管理员', 'admin', '$2b$12$yhj3RPHI.QE2YfqIjAkDVuGIJ.7PIrz1XAsIRktjKQ5f8Ly83fLeK', '1', 1),
(2, '测试男', 'test_male', '$2b$12$yhj3RPHI.QE2YfqIjAkDVuGIJ.7PIrz1XAsIRktjKQ5f8Ly83fLeK', '1', 1),
(3, '测试女', 'test_female', '$2b$12$yhj3RPHI.QE2YfqIjAkDVuGIJ.7PIrz1XAsIRktjKQ5f8Ly83fLeK', '2', 1);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '角色名称',
  `code` varchar(64) NOT NULL DEFAULT '' COMMENT '角色编码',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用;1:正常)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色管理表';

INSERT INTO `sys_role` (`id`, `name`, `code`, `sort`, `status`) VALUES
(1, '超级管理员', 'ROOT', 1, 1),
(2, '系统管理员', 'ADMIN', 2, 1),
(3, '普通用户', 'USER', 3, 1);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

INSERT INTO `sys_user_role` VALUES (1, 1), (2, 2), (3, 3);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '菜单名称',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '菜单类型(1:目录;2:菜单;3:按钮)',
  `path` varchar(255) DEFAULT '' COMMENT '路由路径',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(64) DEFAULT '' COMMENT '菜单图标',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `visible` tinyint(1) DEFAULT '1' COMMENT '状态(0:隐藏;1:显示)',
  `redirect` varchar(255) DEFAULT NULL COMMENT '跳转路径',
  `perm` varchar(64) DEFAULT NULL COMMENT '权限标识',
  `keep_alive` tinyint(1) DEFAULT '0' COMMENT '是否开启缓存',
  `always_show` tinyint(1) DEFAULT '0' COMMENT '是否始终显示',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单管理表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `type`, `path`, `component`, `icon`, `sort`, `visible`) VALUES
(1, 0, '首页', 2, '/dashboard', 'dashboard/index', 'homepage', 1, 1),
(2, 0, '系统管理', 1, '/system', 'Layout', 'system', 2, 1),
(3, 2, '用户管理', 2, 'user', 'system/user/index', 'user', 1, 1),
(4, 2, '角色管理', 2, 'role', 'system/role/index', 'role', 2, 1),
(5, 2, '菜单管理', 2, 'menu', 'system/menu/index', 'menu', 3, 1),
(6, 2, '部门管理', 2, 'dept', 'system/dept/index', 'dept', 4, 1),
(7, 2, '字典管理', 2, 'dict', 'system/dict/index', 'dict', 5, 1),
(8, 0, '示例组件', 1, '/demo', 'Layout', 'guide', 3, 1),
(9, 8, '高级表单', 2, 'form', 'demo/form/index', 'form', 1, 1),
(10, 8, '综合表格', 2, 'table', 'demo/table/index', 'table', 2, 1);

-- ----------------------------
-- Table structure for demo
-- ----------------------------
DROP TABLE IF EXISTS `demo`;
CREATE TABLE `demo` (
  `id` bigint(20) NOT NULL COMMENT '主键-雪花ID',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT '标题',
  `author` varchar(100) DEFAULT '' COMMENT '作者',
  `pageviews` int(11) DEFAULT '0' COMMENT '阅读量',
  `status` varchar(20) DEFAULT 'draft' COMMENT '状态',
  `timestamp` date DEFAULT NULL COMMENT '创建日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='示例表格';

-- ----------------------------
-- Records of demo
-- ----------------------------
INSERT INTO `demo` (`id`, `title`, `author`, `pageviews`, `status`, `timestamp`) VALUES
(1875456789012345678, '示例文章标题 1', '张三', 2500, 'published', '2026-03-01'),
(1875456789012345679, '示例文章标题 2', '李四', 1800, 'draft', '2026-03-02'),
(1875456789012345680, '示例文章标题 3', '王五', 3200, 'deleted', '2026-03-03'),
(1875456789012345681, '示例文章标题 4', '赵六', 4100, 'published', '2026-03-04'),
(1875456789012345682, '示例文章标题 5', '钱七', 2900, 'draft', '2026-03-05');
