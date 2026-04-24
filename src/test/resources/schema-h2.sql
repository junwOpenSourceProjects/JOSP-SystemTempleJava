-- H2 MySQL 兼容模式数据库 schema
-- 对应 db/schema.sql，去除了 H2 不支持的语法

-- ============================================
-- 1. 登录用户表 (login_user)
-- ============================================
DROP TABLE IF EXISTS login_user;
CREATE TABLE login_user (
    id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(100),
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255),
    phone VARCHAR(20),
    sex VARCHAR(10),
    id_number VARCHAR(30),
    status TINYINT NOT NULL DEFAULT 1,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user BIGINT,
    update_user BIGINT,
    CONSTRAINT uk_username UNIQUE (username)
);

CREATE INDEX idx_phone ON login_user(phone);
CREATE INDEX idx_status ON login_user(status);

-- ============================================
-- 2. 角色表 (sys_role)
-- ============================================
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL,
    sort INT DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_code UNIQUE (code)
);

CREATE INDEX idx_status ON sys_role(status);

-- ============================================
-- 3. 用户角色关联表 (account_role)
-- ============================================
DROP TABLE IF EXISTS account_role;
CREATE TABLE account_role (
    id BIGINT NOT NULL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id)
);

CREATE INDEX idx_user_id ON account_role(user_id);
CREATE INDEX idx_role_id ON account_role(role_id);

-- ============================================
-- 3.1 角色菜单关联表 (sys_role_menu)
-- ============================================
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    id BIGINT NOT NULL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_role_menu UNIQUE (role_id, menu_id)
);

CREATE INDEX idx_role_id ON sys_role_menu(role_id);
CREATE INDEX idx_menu_id ON sys_role_menu(menu_id);

-- ============================================
-- 4. 菜单表 (sys_menu)
-- ============================================
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id BIGINT NOT NULL PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    name VARCHAR(100) NOT NULL,
    type TINYINT NOT NULL DEFAULT 0,
    path VARCHAR(255),
    component VARCHAR(255),
    icon VARCHAR(100),
    sort INT DEFAULT 0,
    visible TINYINT NOT NULL DEFAULT 1,
    redirect VARCHAR(255),
    perm VARCHAR(100),
    keep_alive TINYINT NOT NULL DEFAULT 0,
    always_show TINYINT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_parent_id ON sys_menu(parent_id);
CREATE INDEX idx_type ON sys_menu(type);
CREATE INDEX idx_visible ON sys_menu(visible);

-- ============================================
-- 5. 部门表 (sys_dept)
-- ============================================
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
    id BIGINT NOT NULL PRIMARY KEY,
    parent_id BIGINT NOT NULL DEFAULT 0,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50),
    sort INT DEFAULT 0,
    leader VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    status TINYINT NOT NULL DEFAULT 1,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user BIGINT,
    update_user BIGINT
);

CREATE INDEX idx_parent_id ON sys_dept(parent_id);
CREATE INDEX idx_status ON sys_dept(status);

-- ============================================
-- 6. 字典类型表 (sys_dict_type)
-- ============================================
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
    id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(100) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user BIGINT,
    update_user BIGINT,
    CONSTRAINT uk_code UNIQUE (code)
);

CREATE INDEX idx_status ON sys_dict_type(status);

-- ============================================
-- 7. 字典数据表 (sys_dict_data)
-- ============================================
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
    id BIGINT NOT NULL PRIMARY KEY,
    dict_type_id BIGINT NOT NULL,
    label VARCHAR(100) NOT NULL,
    value VARCHAR(255) NOT NULL,
    sort INT DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user BIGINT,
    update_user BIGINT
);

CREATE INDEX idx_dict_type_id ON sys_dict_data(dict_type_id);
CREATE INDEX idx_status ON sys_dict_data(status);
CREATE INDEX idx_sort ON sys_dict_data(sort);

-- ============================================
-- 8. 岗位表 (sys_post)
-- ============================================
DROP TABLE IF EXISTS sys_post;
CREATE TABLE sys_post (
    id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL,
    sort INT DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user BIGINT,
    update_user BIGINT,
    CONSTRAINT uk_post_code UNIQUE (code)
);

CREATE INDEX idx_status ON sys_post(status);

-- ============================================
-- 9. 操作日志表 (sys_oper_log)
-- ============================================
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    id BIGINT NOT NULL PRIMARY KEY,
    title VARCHAR(255),
    business_type VARCHAR(50),
    method VARCHAR(200),
    request_method VARCHAR(10),
    operator_type VARCHAR(20),
    oper_id BIGINT,
    oper_name VARCHAR(100),
    dept_name VARCHAR(100),
    oper_url VARCHAR(500),
    oper_ip VARCHAR(50),
    oper_location VARCHAR(255),
    oper_param TEXT,
    json_result TEXT,
    status TINYINT NOT NULL DEFAULT 1,
    error_msg TEXT,
    oper_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cost_time BIGINT
);

CREATE INDEX idx_oper_time ON sys_oper_log(oper_time);
CREATE INDEX idx_oper_id ON sys_oper_log(oper_id);
CREATE INDEX idx_status ON sys_oper_log(status);

-- ============================================
-- 10. 登录日志表 (sys_login_log)
-- ============================================
DROP TABLE IF EXISTS sys_login_log;
CREATE TABLE sys_login_log (
    id BIGINT NOT NULL PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(100),
    ip VARCHAR(50),
    address VARCHAR(255),
    browser VARCHAR(100),
    os VARCHAR(100),
    status TINYINT NOT NULL DEFAULT 1,
    msg VARCHAR(500),
    login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_id ON sys_login_log(user_id);
CREATE INDEX idx_username ON sys_login_log(username);
CREATE INDEX idx_login_time ON sys_login_log(login_time);
CREATE INDEX idx_status ON sys_login_log(status);

-- ============================================
-- 11. 通知公告表 (sys_notice)
-- ============================================
DROP TABLE IF EXISTS sys_notice;
CREATE TABLE sys_notice (
    id BIGINT NOT NULL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    type TINYINT NOT NULL DEFAULT 1,
    status TINYINT NOT NULL DEFAULT 1,
    publish_time TIMESTAMP,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user BIGINT,
    update_user BIGINT
);

CREATE INDEX idx_type ON sys_notice(type);
CREATE INDEX idx_status ON sys_notice(status);
CREATE INDEX idx_publish_time ON sys_notice(publish_time);

-- ============================================
-- 12. 系统配置表 (sys_config)
-- ============================================
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    config_key VARCHAR(100) NOT NULL,
    value VARCHAR(500),
    type VARCHAR(20) NOT NULL DEFAULT 'string',
    status TINYINT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user BIGINT,
    update_user BIGINT,
    CONSTRAINT uk_config_key UNIQUE (config_key)
);

CREATE INDEX idx_status ON sys_config(status);

-- ============================================
-- 13. 文件管理表 (sys_file)
-- ============================================
DROP TABLE IF EXISTS sys_file;
CREATE TABLE sys_file (
    id BIGINT NOT NULL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    original_name VARCHAR(255),
    file_suffix VARCHAR(50),
    file_size BIGINT,
    file_path VARCHAR(500) NOT NULL,
    file_url VARCHAR(500),
    file_type VARCHAR(50),
    storage_type VARCHAR(20) NOT NULL DEFAULT 'local',
    status TINYINT NOT NULL DEFAULT 1,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user BIGINT,
    update_user BIGINT
);

CREATE INDEX idx_file_type ON sys_file(file_type);
CREATE INDEX idx_create_user ON sys_file(create_user);
CREATE INDEX idx_status ON sys_file(status);

-- ============================================
-- 14. 在线用户表 (sys_online_user)
-- ============================================
DROP TABLE IF EXISTS sys_online_user;
CREATE TABLE sys_online_user (
    id BIGINT NOT NULL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(100) NOT NULL,
    token VARCHAR(500) NOT NULL,
    ip VARCHAR(50),
    address VARCHAR(255),
    browser VARCHAR(100),
    os VARCHAR(100),
    login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expire_time TIMESTAMP NOT NULL,
    CONSTRAINT uk_token UNIQUE (token)
);

CREATE INDEX idx_user_id ON sys_online_user(user_id);
CREATE INDEX idx_username ON sys_online_user(username);
CREATE INDEX idx_expire_time ON sys_online_user(expire_time);
