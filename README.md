# 昊明云设备控制平台（HMCLOUD）

## 项目概述
HMCLOUD是一个基于Spring Boot 3.4.4构建的Java应用程序，提供工业设备的远程管理。项目使用Java 21，采用Gradle进行构建管理。

## 项目架构
该项目采用标准的Spring Boot架构，遵循MVC设计模式，主要包含以下核心组件：

### 核心技术栈
- Spring Boot 3.4.4
- Spring Data JPA
- H2数据库/PostgreSQL
- Lombok
- Sa-Token (认证与权限管理)
- Mica-MQTT (MQTT客户端)
- SpringDoc (API文档)
- Hutool (工具库)

### 目录结构
```
GLHMCloud/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── foxapplication/
│   │   │           └── glhmcloud/
│   │   │               ├── GlhmCloudApplication.java     - 应用程序入口点
│   │   │               │
│   │   │               ├── config/                       - 配置类
│   │   │               │   ├── LogbackColorful.java      - 日志颜色配置
│   │   │               │   ├── SaTokenConfigure.java     - Sa-Token安全配置
│   │   │               │   └── SwaggerConfig.java        - API文档配置
│   │   │               │
│   │   │               ├── controller/                   - 控制器层
│   │   │               │   ├── GlobalExceptionHandler.java - 全局异常处理器
│   │   │               │   │
│   │   │               │   ├── auth/                     - 认证相关控制器
│   │   │               │   │   └── AuthController.java   - 认证控制器
│   │   │               │   │
│   │   │               │   ├── pub/                      - 公共控制器
│   │   │               │   │   └── ImageController.java  - 图片处理控制器
│   │   │               │   │
│   │   │               │   └── view/                     - 视图相关控制器
│   │   │               │       ├── DeviceRecordView.java - 设备记录视图
│   │   │               │       ├── DeviceView.java       - 设备视图
│   │   │               │       ├── OrganizationView.java - 组织视图
│   │   │               │       ├── SuperAdminUserView.java - 超级管理员视图
│   │   │               │       └── UserView.java         - 用户视图
│   │   │               │
│   │   │               ├── service/                      - 服务层
│   │   │               │   ├── KeyValueDatabaseService.java - 键值存储服务接口
│   │   │               │   │
│   │   │               │   └── impl/                     - 服务实现
│   │   │               │       ├── KeyValueDatabaseServiceImpl.java - 键值存储服务实现
│   │   │               │       ├── StpInterfaceImpl.java - Sa-Token接口实现
│   │   │               │       │
│   │   │               │       └── mqtt/                 - MQTT服务
│   │   │               │           ├── MQTTClientConnectListener.java - MQTT客户端连接监听器
│   │   │               │           └── MQTTServiceListener.java       - MQTT服务监听器
│   │   │               │
│   │   │               ├── dao/                          - 数据访问层
│   │   │               │   ├── DeviceDao.java            - 设备数据访问对象
│   │   │               │   ├── DeviceOperateDao.java     - 设备操作数据访问对象
│   │   │               │   ├── DeviceRecordDao.java      - 设备记录数据访问对象
│   │   │               │   ├── DeviceSecRecordDao.java   - 设备安全记录数据访问对象
│   │   │               │   ├── ImageDao.java             - 图片数据访问对象
│   │   │               │   ├── KeyValueDao.java          - 键值存储数据访问对象
│   │   │               │   ├── OrganizationDao.java      - 组织数据访问对象
│   │   │               │   └── UserDao.java              - 用户数据访问对象
│   │   │               │
│   │   │               ├── entity/                       - 实体类
│   │   │               │   ├── DeviceEntity.java         - 设备实体
│   │   │               │   ├── ImageEntity.java          - 图片实体
│   │   │               │   ├── KeyValueEntity.java       - 键值存储实体
│   │   │               │   ├── OrganizationEntity.java   - 组织实体
│   │   │               │   ├── UserEntity.java           - 用户实体
│   │   │               │   │
│   │   │               │   └── devicerecord/             - 设备记录实体
│   │   │               │       ├── BaseDeviceRecordEntity.java - 基础设备记录实体
│   │   │               │       ├── DeviceOperateEntity.java   - 设备操作实体
│   │   │               │       ├── DeviceRecordEntity.java    - 设备记录实体
│   │   │               │       └── DeviceSecRecordEntity.java - 设备安全记录实体
│   │   │               │
│   │   │               ├── param/                        - 参数对象
│   │   │               │   ├── BaseResponse.java         - 基础响应对象
│   │   │               │   ├── ImageProjection.java      - 图片投影对象
│   │   │               │   │
│   │   │               │   └── view/                     - 视图参数
│   │   │               │       └── UserViewData.java     - 用户视图数据
│   │   │               │
│   │   │               └── util/                         - 工具类
│   │   │                   └── Check.java                - 检查工具类
│   │   │
│   │   ├── resources/
│   │   │   ├── application.yml                       - 应用程序配置
│   │   │   ├── banner.txt                           - 应用启动banner
│   │   │   ├── logback-spring.xml                    - 日志配置
│   │   │   ├── static/                              - 静态资源目录
│   │   │   └── templates/                           - 模板文件目录
│   │   │
│   └── test/                                        - 测试代码
│
├── build.gradle                                     - Gradle构建文件
├── settings.gradle                                  - Gradle设置文件
├── gradlew                                          - Gradle包装器脚本(Unix)
└── gradlew.bat                                      - Gradle包装器脚本(Windows)
```

## 编码规范

### 包结构规范
- 控制器类应放在controller包下，并按功能分组
- 服务接口应放在service包下，实现类应放在service.impl包下
- 数据访问对象应放在dao包下
- 实体类应放在entity包下
- 工具类应放在util包下

### 命名规范
- 控制器类名应以Controller或View结尾
- 服务接口名应以Service结尾
- 服务实现类名应以ServiceImpl或Listener结尾
- 数据访问对象应以Dao结尾
- 实体类名应以Entity结尾

### 代码风格
- 使用Lombok减少样板代码
- 方法应该单一职责
- 使用统一的异常处理机制
- API应合理使用HTTP方法和状态码

## 主要功能模块

### 认证与权限管理
- 基于Sa-Token实现用户认证
- 用户角色和权限管理
- AuthController处理登录、注册等功能

### 设备管理
- 设备的CRUD操作
- 设备记录跟踪
- 设备操作日志

### MQTT通信
- MQTT客户端连接管理
- 消息订阅与发布
- 消息处理逻辑

### 组织管理
- 组织的CRUD操作
- 组织与用户关联

### 图片处理
- 图片上传与存储
- 图片查询与获取

## 开发指南
1. 新添加的功能应该遵循项目已有的架构和模式
2. 使用Sa-Token进行安全认证
3. 所有的API应该添加适当的文档
4. 实体类应该使用JPA注解
5. 异常处理应统一经过GlobalExceptionHandler
6. 新的DAO接口应继承JpaRepository或相关接口
7. 新的控制器应使用RestController注解
8. 遵循REST风格的API设计原则

## 配置说明

### 敏感配置
为了保护敏感数据不被上传到GitHub，项目的配置已拆分为两部分：
1. `application.yml` - 包含非敏感配置
2. `application-secret.yml` - 包含敏感配置（数据库凭据、MQTT连接信息等）

### 初次设置
新开发者需要：
1. 复制 `src/main/resources/application-secret.yml.example` 到同目录下的 `application-secret.yml`
2. 用实际值替换配置文件中的占位符

注意：`application-secret.yml` 已添加到 `.gitignore` 中，不会被提交到代码仓库。 
