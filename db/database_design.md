# 数据库设计文档

## 一、数据库设计原则

1. **雪花ID主键**: 所有表使用 `IdType.ASSIGN_ID` 生成19位雪花ID
2. **无外键设计**: 不使用数据库外键约束，在业务层逻辑关联
3. **逻辑删除**: 状态字段代替物理删除
4. **统一审计字段**: create_time, update_time, create_user, update_user

---

## 二、实体关系图 (ER Diagram)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              ER Diagram                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────┐       ┌────────────────┐       ┌──────────────┐         │
│  │  login_user  │       │  account_role  │       │     role     │         │
│  ├──────────────┤       ├────────────────┤       ├──────────────┤         │
│  │ id (PK)      │       │ id (PK)        │       │ id (PK)      │         │
│  │ username     │───────│ user_id (idx)  │───────│ id (FK)      │         │
│  │ password     │       │ role_id (idx)  │       │ name         │         │
│  │ name         │       │ introduction   │       │ code         │         │
│  │ phone        │       │ avatar          │       │ sort         │         │
│  │ sex          │       │ name           │       │ status       │         │
│  │ id_number    │       └────────────────┘       │ create_time   │         │
│  │ status       │              │                │ update_time   │         │
│  │ create_time  │              │                └──────────────┘         │
│  │ update_time  │              │                                              │
│  │ create_user  │              │                                              │
│  │ update_user  │              │                                              │
│  └──────────────┘              │                                              │
│          │                     │                                              │
│          │    ┌────────────────┘                                              │
│          │    │                                                              │
│          ▼    ▼                                                              │
│  ┌──────────────┐       ┌──────────────┐       ┌──────────────────┐         │
│  │    menu      │       │  dict_type   │       │    dict_data     │         │
│  ├──────────────┤       ├──────────────┤       ├──────────────────┤         │
│  │ id (PK)      │       │ id (PK)      │       │ id (PK)          │         │
│  │ parent_id    │       │ name         │       │ dict_type_id(idx) │         │
│  │ name         │       │ code         │───────│ label            │         │
│  │ type         │       │ status       │       │ value            │         │
│  │ path         │       │ create_time  │       │ sort             │         │
│  │ component    │       │ update_time  │       │ status           │         │
│  │ icon         │       └──────────────┘       │ create_time      │         │
│  │ sort         │                              │ update_time      │         │
│  │ visible      │                              └──────────────────┘         │
│  │ redirect     │                                                             │
│  │ perm         │                                                             │
│  │ keep_alive   │                                                             │
│  │ always_show  │                                                             │
│  │ create_time  │                                                             │
│  │ update_time  │                                                             │
│  └──────────────┘                                                             │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、表结构设计

### 3.1 login_user (用户表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| name | VARCHAR | 100 | YES | NULL | 姓名 |
| username | VARCHAR | 50 | NO | NULL | 用户名，唯一索引 |
| password | VARCHAR | 255 | YES | NULL | 密码（BCrypt加密） |
| phone | VARCHAR | 20 | YES | NULL | 手机号 |
| sex | VARCHAR | 10 | YES | NULL | 性别 |
| id_number | VARCHAR | 30 | YES | NULL | 身份证号 |
| status | TINYINT | 1 | NO | 1 | 状态：0-禁用，1-正常 |
| create_time | DATETIME | - | NO | NULL | 创建时间 |
| update_time | DATETIME | - | NO | NULL | 更新时间 |
| create_user | BIGINT | 19 | YES | NULL | 创建人ID |
| update_user | BIGINT | 19 | YES | NULL | 修改人ID |

**索引**:
- `idx_username` ON (username)
- `idx_phone` ON (phone)
- `idx_status` ON (status)

---

### 3.2 sys_role (角色表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| name | VARCHAR | 50 | NO | NULL | 角色名称 |
| code | VARCHAR | 50 | NO | NULL | 角色编码，唯一索引 |
| sort | INT | - | YES | 0 | 排序 |
| status | TINYINT | - | NO | 1 | 状态：0-禁用，1-正常 |
| create_time | DATETIME | - | NO | NULL | 创建时间 |
| update_time | DATETIME | - | NO | NULL | 更新时间 |

**索引**:
- `uk_code` UNIQUE ON (code)
- `idx_status` ON (status)

---

### 3.3 account_role (用户角色关联表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| user_id | BIGINT | 19 | NO | NULL | 用户ID |
| role_id | BIGINT | 19 | NO | NULL | 角色ID |
| create_time | DATETIME | - | YES | NULL | 创建时间 |

**索引**:
- `idx_user_id` ON (user_id)
- `idx_role_id` ON (role_id)
- `uk_user_role` UNIQUE ON (user_id, role_id)

---

### 3.4 sys_menu (菜单表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| parent_id | BIGINT | 19 | YES | 0 | 父菜单ID |
| name | VARCHAR | 100 | NO | NULL | 菜单名称 |
| type | TINYINT | - | NO | 0 | 类型：0-目录，1-菜单，2-按钮 |
| path | VARCHAR | 255 | YES | NULL | 路由路径 |
| component | VARCHAR | 255 | YES | NULL | 组件路径 |
| icon | VARCHAR | 100 | YES | NULL | 图标 |
| sort | INT | - | YES | 0 | 排序 |
| visible | TINYINT | - | NO | 1 | 是否显示：0-隐藏，1-显示 |
| redirect | VARCHAR | 255 | YES | NULL | 重定向地址 |
| perm | VARCHAR | 100 | YES | NULL | 权限标识 |
| keep_alive | TINYINT | - | NO | 0 | 是否缓存：0-否，1-是 |
| always_show | TINYINT | - | NO | 0 | 是否总是显示：0-否，1-是 |
| create_time | DATETIME | - | NO | NULL | 创建时间 |
| update_time | DATETIME | - | NO | NULL | 更新时间 |

**索引**:
- `idx_parent_id` ON (parent_id)
- `idx_type` ON (type)
- `idx_visible` ON (visible)

---

### 3.5 sys_dict_type (字典类型表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| name | VARCHAR | 100 | NO | NULL | 字典类型名称 |
| code | VARCHAR | 100 | NO | NULL | 字典类型编码，唯一索引 |
| status | TINYINT | - | NO | 1 | 状态：0-禁用，1-正常 |
| create_time | DATETIME | - | NO | NULL | 创建时间 |
| update_time | DATETIME | - | NO | NULL | 更新时间 |

**索引**:
- `uk_code` UNIQUE ON (code)
- `idx_status` ON (status)

---

### 3.6 sys_dict_data (字典数据表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| dict_type_id | BIGINT | 19 | NO | NULL | 字典类型ID |
| label | VARCHAR | 100 | NO | NULL | 字典标签 |
| value | VARCHAR | 255 | NO | NULL | 字典值 |
| sort | INT | - | YES | 0 | 排序 |
| status | TINYINT | - | NO | 1 | 状态：0-禁用，1-正常 |
| create_time | DATETIME | - | NO | NULL | 创建时间 |
| update_time | DATETIME | - | NO | NULL | 更新时间 |

**索引**:
- `idx_dict_type_id` ON (dict_type_id)
- `idx_status` ON (status)
- `idx_sort` ON (sort)

---

## 四、完整建表SQL

```sql
-- ============================================
-- 数据库设计文档
-- 项目: JOSP-SystemTempleJava
-- 版本: 1.0.0
-- 更新日期: 2026-04-21
-- ============================================

-- 创建数据库(如果不存在)
-- CREATE DATABASE IF NOT EXISTS josp_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE josp_system;

-- -----------------------------------------
-- 1. 登录用户表 (login_user)
-- -----------------------------------------
DROP TABLE IF EXISTS `login_user`;
CREATE TABLE `login_user` (
    `id` BIGINT NOT NULL COMMENT '主键，雪花ID',
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

-- -----------------------------------------
-- 2. 角色表 (sys_role)
-- -----------------------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- -----------------------------------------
-- 3. 用户角色关联表 (account_role)
-- -----------------------------------------
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

-- -----------------------------------------
-- 4. 菜单表 (sys_menu)
-- -----------------------------------------
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

-- -----------------------------------------
-- 5. 字典类型表 (sys_dict_type)
-- -----------------------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `name` VARCHAR(100) NOT NULL COMMENT '字典类型名称',
    `code` VARCHAR(100) NOT NULL COMMENT '字典类型编码',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- -----------------------------------------
-- 6. 字典数据表 (sys_dict_data)
-- -----------------------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
    `id` BIGINT(19) NOT NULL COMMENT '主键，雪花ID',
    `dict_type_id` BIGINT(19) NOT NULL COMMENT '字典类型ID',
    `label` VARCHAR(100) NOT NULL COMMENT '字典标签',
    `value` VARCHAR(255) NOT NULL COMMENT '字典值',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_dict_type_id` (`dict_type_id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';

-- -----------------------------------------
-- 初始化数据
-- -----------------------------------------

-- 插入默认超级管理员角色
INSERT INTO `sys_role` (`id`, `name`, `code`, `sort`, `status`, `create_time`, `update_time`)
VALUES (751892017468610561, '超级管理员', 'SUPER_ADMIN', 1, 1, NOW(), NOW());

-- 插入默认普通用户角色
INSERT INTO `sys_role` (`id`, `name`, `code`, `sort`, `status`, `create_time`, `update_time`)
VALUES (751892017468610562, '普通用户', 'USER', 2, 1, NOW(), NOW());

-- 插入默认管理员用户 (密码: admin123)
INSERT INTO `login_user` (`id`, `name`, `username`, `password`, `phone`, `sex`, `status`, `create_time`, `update_time`)
VALUES (751892017468610560, '系统管理员', 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', NULL, NULL, 1, NOW(), NOW());

-- 关联管理员和超级管理员角色
INSERT INTO `account_role` (`id`, `user_id`, `role_id`, `create_time`)
VALUES (751892017468610563, 751892017468610560, 751892017468610561, NOW());

-- 插入示例字典类型
INSERT INTO `sys_dict_type` (`id`, `name`, `code`, `status`, `create_time`, `update_time`)
VALUES (751892017468610564, '用户状态', 'user_status', 1, NOW(), NOW());

-- 插入示例字典数据
INSERT INTO `sys_dict_data` (`id`, `dict_type_id`, `label`, `value`, `sort`, `status`, `create_time`, `update_time`)
VALUES (751892017468610565, 751892017468610564, '正常', '1', 1, 1, NOW(), NOW()),
       (751892017468610566, 751892017468610564, '禁用', '0', 2, 1, NOW(), NOW());
```

---

## 五、表关系说明

### 5.1 LoginUser与Role的关系 (多对多)
- 通过中间表 `account_role` 关联
- 一个用户可以拥有多个角色
- 一个角色可以分配给多个用户

### 5.2 Menu自关联 (一对多)
- 通过 `parent_id` 字段实现树形结构
- `parent_id = 0` 表示顶级菜单
- 支持多级菜单嵌套

### 5.3 DictType与DictData的关系 (一对多)
- 一个字典类型下可以有多个字典数据
- 通过 `dict_type_id` 字段关联

---

## 六、索引设计说明

| 表名 | 索引类型 | 索引名 | 包含字段 | 说明 |
|------|---------|--------|----------|------|
| login_user | UNIQUE | uk_username | username | 用户名唯一 |
| login_user | INDEX | idx_phone | phone | 手机号查询 |
| login_user | INDEX | idx_status | status | 状态筛选 |
| sys_role | UNIQUE | uk_code | code | 角色编码唯一 |
| sys_role | INDEX | idx_status | status | 状态筛选 |
| account_role | UNIQUE | uk_user_role | user_id, role_id | 用户角色唯一组合 |
| account_role | INDEX | idx_user_id | user_id | 用户查询 |
| account_role | INDEX | idx_role_id | role_id | 角色查询 |
| sys_menu | INDEX | idx_parent_id | parent_id | 父子菜单查询 |
| sys_menu | INDEX | idx_type | type | 类型筛选 |
| sys_menu | INDEX | idx_visible | visible | 显示筛选 |
| sys_dict_type | UNIQUE | uk_code | code | 类型编码唯一 |
| sys_dict_type | INDEX | idx_status | status | 状态筛选 |
| sys_dict_data | INDEX | idx_dict_type_id | dict_type_id | 类型查询 |
| sys_dict_data | INDEX | idx_status | status | 状态筛选 |
| sys_dict_data | INDEX | idx_sort | sort | 排序查询 |

---

## 七、设计规范

1. **命名规范**
   - 表名使用snake_case命名
   - 字段名使用snake_case命名
   - 索引名使用 `idx_` 前缀，唯一索引使用 `uk_` 前缀

2. **字段类型规范**
   - 状态字段使用 TINYINT(1)
   - ID主键使用 BIGINT(19)
   - 时间字段使用 DATETIME
   - 字符串根据长度选择 VARCHAR 或 TEXT

3. **字符集**
   - 所有表使用 utf8mb4 字符集
   - 排序规则使用 utf8mb4_unicode_ci