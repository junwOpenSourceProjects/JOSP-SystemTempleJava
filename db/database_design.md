# 数据库设计文档

## 一、数据库设计原则

1. **雪花ID主键**: 所有表使用 `IdType.ASSIGN_ID` 生成19位雪花ID
2. **无外键设计**: 不使用数据库外键约束，在业务层逻辑关联
3. **逻辑删除**: 状态字段代替物理删除
4. **统一审计字段**: create_time, update_time, create_user, update_user
5. **字符集**: 所有表使用 utf8mb4 字符集，排序规则 utf8mb4_unicode_ci

---

## 二、数据表清单

| 序号 | 表名 | 说明 |
|------|------|------|
| 1 | login_user | 登录用户表 |
| 2 | sys_role | 角色表 |
| 3 | account_role | 用户角色关联表 |
| 4 | sys_role_menu | 角色菜单关联表 |
| 5 | sys_menu | 菜单表 |
| 6 | sys_dept | 部门表 |
| 7 | sys_dict_type | 字典类型表 |
| 8 | sys_dict_data | 字典数据表 |
| 9 | sys_post | 岗位表 |
| 10 | sys_oper_log | 操作日志表 |
| 11 | sys_login_log | 登录日志表 |
| 12 | sys_notice | 通知公告表 |
| 13 | sys_config | 系统配置表 |
| 14 | sys_file | 文件管理表 |
| 15 | sys_online_user | 在线用户表 |

---

## 三、实体关系图 (ER Diagram)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              ER Diagram                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────┐       ┌────────────────┐       ┌──────────────┐         │
│  │  login_user  │       │  account_role  │       │   sys_role   │         │
│  ├──────────────┤       ├────────────────┤       ├──────────────┤         │
│  │ id (PK)      │       │ id (PK)        │       │ id (PK)      │         │
│  │ username     │───────│ user_id (idx)  │───────│ role_id (FK) │         │
│  │ password     │       │ role_id (idx)  │       │ name         │         │
│  │ name         │       └────────────────┘       │ code         │         │
│  │ phone        │              │                │ sort         │         │
│  │ sex          │              │                │ status       │         │
│  │ id_number    │              │                └──────────────┘         │
│  │ status       │              │                         ▲                 │
│  └──────────────┘              │                         │                 │
│          │                    │                ┌───────┴───────┐         │
│          │                    │                │ sys_role_menu  │         │
│          │                    │                ├───────────────┤         │
│          │                    │                │ id (PK)       │         │
│          │                    │                │ role_id (idx) │         │
│          │                    │                │ menu_id (idx) │         │
│          ▼                    ▼                └───────────────┘         │
│  ┌──────────────┐       ┌──────────────┐                │                 │
│  │    menu      │       │   sys_dept   │                │                 │
│  ├──────────────┤       ├──────────────┤                │                 │
│  │ id (PK)      │       │ id (PK)      │                ▼                 │
│  │ parent_id    │       │ parent_id    │         ┌──────────────┐         │
│  │ name         │       │ name         │         │   sys_menu   │         │
│  │ type         │       │ code         │         ├──────────────┤         │
│  │ path         │       │ leader       │         │ id (PK)      │         │
│  │ component    │       │ phone        │         │ parent_id    │         │
│  │ icon         │       │ status       │         │ name         │         │
│  │ sort         │       └──────────────┘         │ type         │         │
│  │ visible      │                              │ path         │         │
│  │ perm         │                              │ component    │         │
│  └──────────────┘                              │ icon         │         │
│                                                  └──────────────┘         │
│  ┌──────────────┐       ┌──────────────────┐                               │
│  │  sys_config  │       │   sys_dict_type  │───────┐                     │
│  ├──────────────┤       ├──────────────────┤       │                     │
│  │ id (PK)      │       │ id (PK)          │       ▼                     │
│  │ name         │       │ name             │ ┌──────────────┐              │
│  │ key          │       │ code             │ │ sys_dict_data│              │
│  │ value        │       │ status           │ ├──────────────┤              │
│  │ type         │       └──────────────────┘ │ id (PK)      │              │
│  │ status       │                          │ dict_type_id │              │
│  └──────────────┘                          │ label        │              │
│                                            │ value        │              │
│  ┌──────────────┐       ┌──────────────┐    │ sort         │              │
│  │   sys_post   │       │ sys_oper_log │    │ status       │              │
│  ├──────────────┤       ├──────────────┤    └──────────────┘              │
│  │ id (PK)      │       │ id (PK)      │                                │
│  │ name         │       │ title        │    ┌──────────────┐              │
│  │ code         │       │ business_type│    │ sys_notice   │              │
│  │ sort         │       │ method       │    ├──────────────┤              │
│  │ status       │       │ oper_id      │    │ id (PK)      │              │
│  └──────────────┘       │ oper_name    │    │ title        │              │
│                        │ oper_url     │    │ content      │              │
│  ┌──────────────┐       │ oper_ip      │    │ type         │              │
│  │ sys_login_log│       │ status       │    │ status       │              │
│  ├──────────────┤       │ oper_time    │    └──────────────┘              │
│  │ id (PK)      │       └──────────────┘                                  │
│  │ user_id      │                                                         │
│  │ username     │       ┌──────────────┐                                    │
│  │ ip           │       │  sys_file    │                                    │
│  │ address      │       ├──────────────┤                                    │
│  │ browser      │       │ id (PK)      │                                    │
│  │ os           │       │ file_name    │                                    │
│  │ status       │       │ file_path    │                                    │
│  │ login_time   │       │ file_url     │                                    │
│  └──────────────┘       │ storage_type │                                    │
│                         │ status       │                                    │
│  ┌──────────────┐       └──────────────┘                                    │
│  │sys_online_user│                                                        │
│  ├──────────────┤                                                          │
│  │ id (PK)      │                                                          │
│  │ user_id      │                                                          │
│  │ username     │                                                          │
│  │ token        │                                                          │
│  │ ip           │                                                          │
│  │ login_time   │                                                          │
│  │ expire_time  │                                                          │
│  └──────────────┘                                                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、表结构设计

### 4.1 login_user (登录用户表)

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
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 更新时间 |
| create_user | BIGINT | 19 | YES | NULL | 创建人ID |
| update_user | BIGINT | 19 | YES | NULL | 修改人ID |

**索引**:
- `uk_username` UNIQUE ON (username)
- `idx_phone` ON (phone)
- `idx_status` ON (status)

---

### 4.2 sys_role (角色表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| name | VARCHAR | 50 | NO | NULL | 角色名称 |
| code | VARCHAR | 50 | NO | NULL | 角色编码，唯一索引 |
| sort | INT | 11 | YES | 0 | 排序 |
| status | TINYINT | 1 | NO | 1 | 状态：0-禁用，1-正常 |
| remark | VARCHAR | 500 | YES | NULL | 备注 |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 更新时间 |

**索引**:
- `uk_code` UNIQUE ON (code)
- `idx_status` ON (status)

---

### 4.3 account_role (用户角色关联表)

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

### 4.4 sys_role_menu (角色菜单关联表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| role_id | BIGINT | 19 | NO | NULL | 角色ID |
| menu_id | BIGINT | 19 | NO | NULL | 菜单ID |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |

**索引**:
- `idx_role_id` ON (role_id)
- `idx_menu_id` ON (menu_id)
- `uk_role_menu` UNIQUE ON (role_id, menu_id)

---

### 4.5 sys_menu (菜单表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| parent_id | BIGINT | 19 | YES | 0 | 父菜单ID |
| name | VARCHAR | 100 | NO | NULL | 菜单名称 |
| type | TINYINT | 1 | NO | 0 | 类型：0-目录，1-菜单，2-按钮 |
| path | VARCHAR | 255 | YES | NULL | 路由路径 |
| component | VARCHAR | 255 | YES | NULL | 组件路径 |
| icon | VARCHAR | 100 | YES | NULL | 图标 |
| sort | INT | 11 | YES | 0 | 排序 |
| visible | TINYINT | 1 | NO | 1 | 是否显示：0-隐藏，1-显示 |
| redirect | VARCHAR | 255 | YES | NULL | 重定向地址 |
| perm | VARCHAR | 100 | YES | NULL | 权限标识 |
| keep_alive | TINYINT | 1 | NO | 0 | 是否缓存：0-否，1-是 |
| always_show | TINYINT | 1 | NO | 0 | 是否总是显示：0-否，1-是 |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 更新时间 |

**索引**:
- `idx_parent_id` ON (parent_id)
- `idx_type` ON (type)
- `idx_visible` ON (visible)

---

### 4.6 sys_dept (部门表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| parent_id | BIGINT | 19 | NO | 0 | 父部门ID |
| name | VARCHAR | 100 | NO | NULL | 部门名称 |
| code | VARCHAR | 50 | YES | NULL | 部门编码 |
| sort | INT | 11 | YES | 0 | 排序 |
| leader | VARCHAR | 50 | YES | NULL | 负责人 |
| phone | VARCHAR | 20 | YES | NULL | 联系电话 |
| email | VARCHAR | 100 | YES | NULL | 邮箱 |
| status | TINYINT | 1 | NO | 1 | 状态：0-禁用，1-正常 |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 更新时间 |
| create_user | BIGINT | 19 | YES | NULL | 创建人ID |
| update_user | BIGINT | 19 | YES | NULL | 修改人ID |

**索引**:
- `idx_parent_id` ON (parent_id)
- `idx_status` ON (status)

---

### 4.7 sys_dict_type (字典类型表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| name | VARCHAR | 100 | NO | NULL | 字典类型名称 |
| code | VARCHAR | 100 | NO | NULL | 字典类型编码，唯一索引 |
| status | TINYINT | 1 | NO | 1 | 状态：0-禁用，1-正常 |
| remark | VARCHAR | 500 | YES | NULL | 备注 |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 更新时间 |
| create_user | BIGINT | 19 | YES | NULL | 创建人ID |
| update_user | BIGINT | 19 | YES | NULL | 修改人ID |

**索引**:
- `uk_code` UNIQUE ON (code)
- `idx_status` ON (status)

---

### 4.8 sys_dict_data (字典数据表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| dict_type_id | BIGINT | 19 | NO | NULL | 字典类型ID |
| label | VARCHAR | 100 | NO | NULL | 字典标签 |
| value | VARCHAR | 255 | NO | NULL | 字典值 |
| sort | INT | 11 | YES | 0 | 排序 |
| status | TINYINT | 1 | NO | 1 | 状态：0-禁用，1-正常 |
| remark | VARCHAR | 500 | YES | NULL | 备注 |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 更新时间 |
| create_user | BIGINT | 19 | YES | NULL | 创建人ID |
| update_user | BIGINT | 19 | YES | NULL | 修改人ID |

**索引**:
- `idx_dict_type_id` ON (dict_type_id)
- `idx_status` ON (status)
- `idx_sort` ON (sort)

---

### 4.9 sys_post (岗位表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| name | VARCHAR | 100 | NO | NULL | 岗位名称 |
| code | VARCHAR | 50 | NO | NULL | 岗位编码，唯一索引 |
| sort | INT | 11 | YES | 0 | 排序 |
| status | TINYINT | 1 | NO | 1 | 状态：0-禁用，1-正常 |
| remark | VARCHAR | 500 | YES | NULL | 备注 |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 更新时间 |
| create_user | BIGINT | 19 | YES | NULL | 创建人ID |
| update_user | BIGINT | 19 | YES | NULL | 修改人ID |

**索引**:
- `uk_post_code` UNIQUE ON (code)
- `idx_status` ON (status)

---

### 4.10 sys_oper_log (操作日志表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| title | VARCHAR | 255 | YES | NULL | 操作标题 |
| business_type | VARCHAR | 50 | YES | NULL | 业务类型 |
| method | VARCHAR | 200 | YES | NULL | 请求方法 |
| request_method | VARCHAR | 10 | YES | NULL | 请求方式 |
| operator_type | VARCHAR | 20 | YES | NULL | 操作类别 |
| oper_id | BIGINT | 19 | YES | NULL | 操作人ID |
| oper_name | VARCHAR | 100 | YES | NULL | 操作人名称 |
| dept_name | VARCHAR | 100 | YES | NULL | 部门名称 |
| oper_url | VARCHAR | 500 | YES | NULL | 请求URL |
| oper_ip | VARCHAR | 50 | YES | NULL | 操作地址 |
| oper_location | VARCHAR | 255 | YES | NULL | 操作地点 |
| oper_param | TEXT | - | YES | NULL | 请求参数 |
| json_result | TEXT | - | YES | NULL | 返回参数 |
| status | TINYINT | 1 | NO | 1 | 操作状态：0-异常，1-正常 |
| error_msg | TEXT | - | YES | NULL | 错误消息 |
| oper_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 操作时间 |
| cost_time | BIGINT | 20 | YES | NULL | 消耗时间(毫秒) |

**索引**:
- `idx_oper_time` ON (oper_time)
- `idx_oper_id` ON (oper_id)
- `idx_status` ON (status)

---

### 4.11 sys_login_log (登录日志表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| user_id | BIGINT | 19 | YES | NULL | 用户ID |
| username | VARCHAR | 100 | YES | NULL | 用户名 |
| ip | VARCHAR | 50 | YES | NULL | 登录IP地址 |
| address | VARCHAR | 255 | YES | NULL | 登录地点 |
| browser | VARCHAR | 100 | YES | NULL | 浏览器类型 |
| os | VARCHAR | 100 | YES | NULL | 操作系统 |
| status | TINYINT | 1 | NO | 1 | 登录状态：0-失败，1-成功 |
| msg | VARCHAR | 500 | YES | NULL | 提示消息 |
| login_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 登录时间 |

**索引**:
- `idx_user_id` ON (user_id)
- `idx_username` ON (username)
- `idx_login_time` ON (login_time)
- `idx_status` ON (status)

---

### 4.12 sys_notice (通知公告表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| title | VARCHAR | 255 | NO | NULL | 公告标题 |
| content | TEXT | - | YES | NULL | 公告内容 |
| type | TINYINT | 1 | NO | 1 | 公告类型：1-通知，2-公告 |
| status | TINYINT | 1 | NO | 1 | 状态：0-禁用，1-正常 |
| publish_time | DATETIME | - | YES | NULL | 发布时间 |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 更新时间 |
| create_user | BIGINT | 19 | YES | NULL | 创建人ID |
| update_user | BIGINT | 19 | YES | NULL | 修改人ID |

**索引**:
- `idx_type` ON (type)
- `idx_status` ON (status)
- `idx_publish_time` ON (publish_time)

---

### 4.13 sys_config (系统配置表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| name | VARCHAR | 100 | NO | NULL | 配置名称 |
| key | VARCHAR | 100 | NO | NULL | 配置键，唯一索引 |
| value | VARCHAR | 500 | YES | NULL | 配置值 |
| type | VARCHAR | 20 | NO | string | 配置类型：string, number, boolean, json |
| status | TINYINT | 1 | NO | 1 | 状态：0-禁用，1-正常 |
| remark | VARCHAR | 500 | YES | NULL | 备注 |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 更新时间 |
| create_user | BIGINT | 19 | YES | NULL | 创建人ID |
| update_user | BIGINT | 19 | YES | NULL | 修改人ID |

**索引**:
- `uk_config_key` UNIQUE ON (key)
- `idx_status` ON (status)

---

### 4.14 sys_file (文件管理表)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| file_name | VARCHAR | 255 | NO | NULL | 文件名称 |
| original_name | VARCHAR | 255 | YES | NULL | 原始文件名 |
| file_suffix | VARCHAR | 50 | YES | NULL | 文件后缀 |
| file_size | BIGINT | 20 | YES | NULL | 文件大小(字节) |
| file_path | VARCHAR | 500 | NO | NULL | 文件路径 |
| file_url | VARCHAR | 500 | YES | NULL | 文件访问URL |
| file_type | VARCHAR | 50 | YES | NULL | 文件类型 |
| storage_type | VARCHAR | 20 | NO | local | 存储类型：local, oss, s3 |
| status | TINYINT | 1 | NO | 1 | 状态：0-禁用，1-正常 |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 更新时间 |
| create_user | BIGINT | 19 | YES | NULL | 创建人ID |
| update_user | BIGINT | 19 | YES | NULL | 修改人ID |

**索引**:
- `idx_file_type` ON (file_type)
- `idx_create_user` ON (create_user)
- `idx_status` ON (status)

---

### 4.15 sys_online_user (在线用户表)

> 说明: 基于Redis存储，MySQL仅用于备份/审计

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | 19 | NO | NULL | 主键，雪花ID |
| user_id | BIGINT | 19 | NO | NULL | 用户ID |
| username | VARCHAR | 100 | NO | NULL | 用户名 |
| token | VARCHAR | 500 | NO | NULL | 登录Token，唯一索引 |
| ip | VARCHAR | 50 | YES | NULL | 登录IP |
| address | VARCHAR | 255 | YES | NULL | 登录地址 |
| browser | VARCHAR | 100 | YES | NULL | 浏览器 |
| os | VARCHAR | 100 | YES | NULL | 操作系统 |
| login_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 登录时间 |
| expire_time | DATETIME | - | NO | NULL | 过期时间 |

**索引**:
- `uk_token` UNIQUE ON (token)
- `idx_user_id` ON (user_id)
- `idx_username` ON (username)
- `idx_expire_time` ON (expire_time)

---

## 五、表关系说明

### 5.1 LoginUser与Role的关系 (多对多)
- 通过中间表 `account_role` 关联
- 一个用户可以拥有多个角色
- 一个角色可以分配给多个用户

### 5.2 Role与Menu的关系 (多对多)
- 通过中间表 `sys_role_menu` 关联
- 一个角色可以拥有多个菜单权限
- 一个菜单可以分配给多个角色

### 5.3 Menu自关联 (一对多)
- 通过 `parent_id` 字段实现树形结构
- `parent_id = 0` 表示顶级菜单
- 支持多级菜单嵌套

### 5.4 Dept自关联 (一对多)
- 通过 `parent_id` 字段实现树形结构
- `parent_id = 0` 表示顶级部门
- 支持多级部门嵌套

### 5.5 DictType与DictData的关系 (一对多)
- 一个字典类型下可以有多个字典数据
- 通过 `dict_type_id` 字段关联

---

## 六、索引设计汇总

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
| sys_role_menu | UNIQUE | uk_role_menu | role_id, menu_id | 角色菜单唯一组合 |
| sys_role_menu | INDEX | idx_role_id | role_id | 角色查询 |
| sys_role_menu | INDEX | idx_menu_id | menu_id | 菜单查询 |
| sys_menu | INDEX | idx_parent_id | parent_id | 父子菜单查询 |
| sys_menu | INDEX | idx_type | type | 类型筛选 |
| sys_menu | INDEX | idx_visible | visible | 显示筛选 |
| sys_dept | INDEX | idx_parent_id | parent_id | 父子部门查询 |
| sys_dept | INDEX | idx_status | status | 状态筛选 |
| sys_dict_type | UNIQUE | uk_code | code | 类型编码唯一 |
| sys_dict_type | INDEX | idx_status | status | 状态筛选 |
| sys_dict_data | INDEX | idx_dict_type_id | dict_type_id | 类型查询 |
| sys_dict_data | INDEX | idx_status | status | 状态筛选 |
| sys_dict_data | INDEX | idx_sort | sort | 排序查询 |
| sys_post | UNIQUE | uk_post_code | code | 岗位编码唯一 |
| sys_post | INDEX | idx_status | status | 状态筛选 |
| sys_oper_log | INDEX | idx_oper_time | oper_time | 操作时间查询 |
| sys_oper_log | INDEX | idx_oper_id | oper_id | 操作人查询 |
| sys_oper_log | INDEX | idx_status | status | 状态筛选 |
| sys_login_log | INDEX | idx_user_id | user_id | 用户查询 |
| sys_login_log | INDEX | idx_username | username | 用户名查询 |
| sys_login_log | INDEX | idx_login_time | login_time | 登录时间查询 |
| sys_login_log | INDEX | idx_status | status | 状态筛选 |
| sys_notice | INDEX | idx_type | type | 类型筛选 |
| sys_notice | INDEX | idx_status | status | 状态筛选 |
| sys_notice | INDEX | idx_publish_time | publish_time | 发布时间查询 |
| sys_config | UNIQUE | uk_config_key | key | 配置键唯一 |
| sys_config | INDEX | idx_status | status | 状态筛选 |
| sys_file | INDEX | idx_file_type | file_type | 文件类型查询 |
| sys_file | INDEX | idx_create_user | create_user | 创建人查询 |
| sys_file | INDEX | idx_status | status | 状态筛选 |
| sys_online_user | UNIQUE | uk_token | token | Token唯一 |
| sys_online_user | INDEX | idx_user_id | user_id | 用户查询 |
| sys_online_user | INDEX | idx_username | username | 用户名查询 |
| sys_online_user | INDEX | idx_expire_time | expire_time | 过期时间查询 |

---

## 七、设计规范

### 7.1 命名规范
- 表名使用snake_case命名
- 字段名使用snake_case命名
- 索引名使用 `idx_` 前缀，唯一索引使用 `uk_` 前缀

### 7.2 字段类型规范
- 状态字段使用 TINYINT(1)
- ID主键使用 BIGINT(19) 雪花ID
- 时间字段使用 DATETIME
- 字符串根据长度选择 VARCHAR 或 TEXT

### 7.3 字符集
- 所有表使用 utf8mb4 字符集
- 排序规则使用 utf8mb4_unicode_ci

### 7.4 建表语句特性
- 所有表都有 `DROP TABLE IF EXISTS` 语句，确保脚本可重复执行
- 所有表都使用 InnoDB 引擎
- 主键使用 BIGINT(19) 雪花ID

---

## 八、参考文件

- 数据库建表脚本: `/db/schema.sql`
