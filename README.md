# JOSP-SystemTempleJava

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0.5-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-17-blue.svg)
![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.7-blue.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)
![License](https://img.shields.io/badge/License-AGPL--3.0-blue.svg)

> **Spring Boot 3 后端项目模板** - 一键创建新项目,统一框架结构

## 🚀 从模板创建新项目

### 方式1: GitHub网页操作

1. 点击本页面上方的 **"Use this template"** 按钮
2. 选择 **"Create a new repository"**
3. 填写新项目名称(例如: `JOSP-NewProjectJava`)
4. 点击 **"Create repository"**
5. 克隆新项目到本地:
   ```bash
   git clone https://github.com/你的用户名/JOSP-NewProjectJava.git
   cd JOSP-NewProjectJava
   ```

### 方式2: GitHub CLI

```bash
# 创建新仓库
gh repo create JOSP-NewProjectJava --template junwOpenSourceProjects/JOSP-SystemTempleJava --public

# 克隆到本地
gh repo clone 你的用户名/JOSP-NewProjectJava
cd JOSP-NewProjectJava
```

## 📝 创建后修改项目信息

### 1. 修改pom.xml

```xml
<artifactId>JOSP-NewProjectJava</artifactId>
<name>JOSP-NewProjectJava</name>
<description>新项目描述</description>
```

### 2. 修改包名

全局搜索替换:
- `wo1261931780.JOSPexaminationSystemJava` → `你的包名.新项目名`
- `JOSPexaminationSystemJava` → `新项目名`

### 3. 修改数据库配置

编辑 `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    dynamic:
      primary: master
      datasource:
        master:
          url: jdbc:mysql://localhost:3306/你的数据库名
          username: 你的用户名
          password: 你的密码
```

## 📖 项目简介

JOSP-SystemTempleJava 是一个Spring Boot 3后端项目模板,提供统一的项目框架结构、API封装规范、数据库配置等基础功能,帮助你快速启动新项目开发。

**配套前端模板**: 
- Vue2版本: [JOSP-SystemTempleVue2](https://github.com/junwOpenSourceProjects/JOSP-SystemTempleVue2)
- Vue3版本: [JOSP-SystemTempleVue3](https://github.com/junwOpenSourceProjects/JOSP-SystemTempleVue3)

## 🏗️ 系统架构

```mermaid
graph TB
    subgraph Frontend[前端展示层]
        Vue2[Vue2前端<br/>SystemTempleVue2]
        Vue3[Vue3前端<br/>SystemTempleVue3]
    end
    
    subgraph Gateway[网关层]
        Nginx[Nginx<br/>反向代理]
    end
    
    subgraph Backend[后端服务 Spring Boot 3.0.5]
        Controller[Controller层<br/>接口控制]
        Service[Service层<br/>业务处理]
        Mapper[Mapper层<br/>数据访问]
        
        Controller --> Service
        Service --> Mapper
    end
    
    subgraph Database[数据层]
        Master[(主库<br/>MySQL Master)]
        Slave[(从库<br/>MySQL Slave)]
        DynamicDS[动态数据源<br/>路由]
    end
    
    subgraph Tools[工具组件]
        Knife4j[API文档<br/>Knife4j]
        Generator[代码生成<br/>MyBatis-Plus]
        Monitor[监控<br/>Actuator]
    end
    
    Vue2 --> Nginx
    Vue3 --> Nginx
    Nginx --> Controller
    DynamicDS --> Master
    DynamicDS --> Slave
    Mapper --> DynamicDS
    Service --> Monitor
    
    style Frontend fill:#e1f5ff
    style Gateway fill:#f0f9ff
    style Backend fill:#fff4e6
    style Database fill:#f3f9ff
    style Tools fill:#f0fff4
```

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Spring Boot** | 3.0.5 | 核心框架 |
| **Java** | 17 | 开发语言 |
| **MyBatis-Plus** | 3.5.7 | ORM框架 |
| **MySQL** | 8.0.32 | 数据库 |
| **Knife4j** | 3.0.3 | API文档 |
| **Hutool** | 5.8.21 | 工具库 |
| **Dynamic Datasource** | 3.5.0 | 多数据源 |
| **Velocity** | 2.3 | 模板引擎 |

## 📦 核心依赖

### Web层
- `spring-boot-starter-web` - Web开发
- `spring-boot-starter-webflux` - 响应式Web
- `spring-boot-starter-web-services` - Web服务

### 数据层
- `mybatis-plus-spring-boot3-starter` - MyBatis增强
- `dynamic-datasource-spring-boot-starter` - 多数据源
- `mybatis-plus-generator` - 代码生成器

### 工具层
- `lombok` - 简化代码
- `hutool-all` - 工具集
- `fastjson2` - JSON处理

### 监控层
- `spring-boot-starter-actuator` - 应用监控

## 🚀 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 运行模板项目

```bash
# 1. 克隆模板项目(用于测试)
git clone https://github.com/junwOpenSourceProjects/JOSP-SystemTempleJava.git
cd JOSP-SystemTempleJava

# 2. 创建数据库
mysql -u root -p -e "CREATE DATABASE system_template CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 3. 修改数据库配置
# 编辑 src/main/resources/application.yml,修改数据库连接信息

# 4. 运行项目
./mvnw spring-boot:run

# 5. 访问服务
# 后端地址: http://localhost:8088
# API文档: http://localhost:8088/doc.html
# 健康检查: http://localhost:8088/actuator/health
```

### 从模板创建新项目后的安装步骤

1. **克隆新项目**
```bash
git clone https://github.com/你的用户名/JOSP-NewProjectJava.git
cd JOSP-NewProjectJava
```

2. **配置数据库**
```bash
# 创建数据库
CREATE DATABASE system_template;

# 导入SQL脚本
mysql -u root -p system_template < db/schema.sql
```

3. **修改配置**
```yaml
# application.yml
spring:
  datasource:
    dynamic:
      primary: master
      datasource:
        master:
          url: jdbc:mysql://localhost:3306/system_template
          username: root
          password: your_password
```

4. **运行项目**
```bash
mvn clean install
mvn spring-boot:run
```

5. **访问服务**
- 后端地址: http://localhost:8088
- API文档: http://localhost:8088/doc.html
- 健康检查: http://localhost:8088/actuator/health

## 📁 项目结构

```
JOSP-SystemTempleJava/
├── src/main/java/
│   └── wo1261931780/
│       ├── controller/          # 控制器层
│       │   ├── system/          # 系统管理
│       │   └── common/          # 通用接口
│       ├── service/             # 业务逻辑层
│       │   ├── system/          # 系统服务
│       │   └── impl/            # 服务实现
│       ├── mapper/              # 数据访问层
│       ├── entity/              # 实体类
│       ├── config/              # 配置类
│       │   ├── DataSourceConfig.java
│       │   ├── MybatisPlusConfig.java
│       │   └── WebConfig.java
│       ├── common/              # 通用模块
│       │   ├── Result.java      # 统一返回
│       │   ├── PageResult.java  # 分页结果
│       │   └── Constants.java   # 常量定义
│       └── utils/               # 工具类
├── src/main/resources/
│   ├── application.yml          # 主配置
│   ├── application-dev.yml      # 开发环境
│   ├── application-prod.yml     # 生产环境
│   └── mapper/                  # MyBatis映射
└── pom.xml
```

## 🔧 核心功能

### 1. 多数据源配置
```yaml
spring:
  datasource:
    dynamic:
      primary: master
      strict: false
      datasource:
        master:
          url: jdbc:mysql://localhost:3306/master_db
          username: root
          password: password
        slave:
          url: jdbc:mysql://localhost:3306/slave_db
          username: root
          password: password
```

### 2. 统一返回格式
```java
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }
    
    public static Result<?> error(String message) {
        return new Result<>(500, message, null);
    }
}
```

### 3. 分页查询
```java
@GetMapping("/page")
public Result<PageResult<User>> page(
    @RequestParam(defaultValue = "1") Integer pageNum,
    @RequestParam(defaultValue = "10") Integer pageSize
) {
    Page<User> page = userService.page(
        new Page<>(pageNum, pageSize)
    );
    return Result.success(PageResult.of(page));
}
```

### 4. 代码生成器
```java
// 自动生成Entity、Mapper、Service、Controller
FastAutoGenerator.create("jdbc:mysql://localhost:3306/db", "root", "password")
    .globalConfig(builder -> builder.author("junw"))
    .packageConfig(builder -> builder.parent("wo1261931780"))
    .strategyConfig(builder -> builder
        .addInclude("user")  // 表名
        .entityBuilder()
        .mapperBuilder()
        .serviceBuilder()
        .controllerBuilder()
    )
    .execute();
```

## 📊 数据库表结构

### 用户表 (login_user)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 用户ID |
| username | VARCHAR | 用户名 |
| password | VARCHAR | 密码 (MD5加密) |

### 账号角色表 (account_role)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | VARCHAR | 主键 |
| name | VARCHAR | 用户名称 |
| roles | LIST | 角色列表 |
| avatar | VARCHAR | 头像URL |
| introduction | VARCHAR | 用户说明 |

## 💡 核心功能模块

### 1. 用户登录认证

```java
@PostMapping("/vue-element-admin/user/login")
public ShowResult<LoginUser> userLogin(@RequestBody LoginUser loginUser) {
    // MD5密码加密
    String passwordMd5 = DigestUtils.md5DigestAsHex(loginUser.getPassword().getBytes());
    // Lambda查询验证
    LambdaQueryWrapper<LoginUser> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(LoginUser::getUsername, loginUser.getUsername())
           .eq(LoginUser::getPassword, passwordMd5);
    LoginUser user = loginUserService.getOne(wrapper);
    return ShowResult.sendSuccess(user);
}
```

**功能特性:**
- 用户名密码验证
- MD5密码加密
- 返回用户信息
- 错误提示处理

### 2. 用户信息获取

```java
@GetMapping("/vue-element-admin/user/info")
public ShowResult<AccountRole> userInfo() {
    AccountRole accountRole = new AccountRole();
    accountRole.setRoles(Arrays.asList("admin"));
    accountRole.setIntroduction("I am a super administrator");
    accountRole.setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
    accountRole.setName("Super Admin");
    return ShowResult.sendSuccess(accountRole);
}
```

**功能特性:**
- 返回用户角色信息
- 返回用户头像
- 返回用户说明
- 暂时写死返回数据

### 基础模块
- ✅ 用户管理
- ✅ 角色管理
- ✅ 权限管理
- ✅ 菜单管理
- ✅ 日志管理
- ✅ 字典管理

### 系统监控
- ✅ 在线用户
- ✅ 服务监控
- ✅ 缓存监控
- ✅ SQL监控

## 🔌 API文档

启动项目后访问 Knife4j 接口文档:
- 地址: http://localhost:8088/doc.html
- 功能: 
  - 在线接口测试
  - API文档查看
  - 接口分组管理

## 🔐 安全配置

### 跨域配置
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*");
    }
}
```

### 参数校验
```java
@PostMapping("/save")
public Result<?> save(@Valid @RequestBody UserDTO dto) {
    // 自动参数校验
    userService.save(dto);
    return Result.success();
}
```

## 📝 开发指南

### 新增模块
1. 创建Entity实体类
2. 创建Mapper接口
3. 创建Service接口和实现
4. 创建Controller控制器
5. 编写API文档

### 多环境配置
```bash
# 开发环境
mvn spring-boot:run -Dspring.profiles.active=dev

# 生产环境
mvn clean package -Pprod
```

## 📈 性能优化

### 数据库优化
- 合理使用索引
- 开启查询缓存
- 读写分离配置

### 连接池优化
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

### 缓存策略
```java
@Cacheable(value = "user", key = "#id")
public User getById(Long id) {
    return userMapper.selectById(id);
}
```

## 📝 更新日志

### v0.0.1-SNAPSHOT
- 初始化项目结构
- 集成Spring Boot 3.0.5
- 配置多数据源支持
- 添加代码生成器
- 集成Knife4j文档
- 添加系统监控功能

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request!

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📮 联系方式

- 作者: junw
- Email: wo1261931780@gmail.com
- GitHub: [@wo1261931780](https://github.com/wo1261931780)

## 🙏 致谢

感谢 Spring Boot、MyBatis-Plus 等开源项目的支持!

---

**提示**: 本项目作为系统模板,提供了完整的后台系统基础功能。建议根据实际业务需求调整和扩展功能模块。
