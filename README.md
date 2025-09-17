# Campus Share - Web课设校园分享平台后端

这是一个基于 Spring Boot 的校园分享平台的后端项目。
欢迎访问[前端仓库](https://github.com/12345txy/campus_share)

## ✨ 项目功能

- 用户模块：用户注册、登录、个人信息管理、关注/取关用户
- 帖子模块：发布、删除、浏览帖子、帖子点赞、收藏
- 评论模块：发表评论、删除评论
- 社区模块：创建社区、加入社区、发布社区公告
- 管理后台：用户管理、帖子管理、系统配置

## 🚀 技术栈

- **核心框架**: Spring Boot 3
- **安全框架**: Spring Security, JWT
- **数据库**: MySQL
- **数据访问**: MyBatis-Plus, Spring Data JPA
- **缓存**: Redis
- **API 文档**: SpringDoc (Swagger UI)
- **文件存储**: 阿里云 OSS
- **实时通信**: WebSocket

## 📦 快速开始

### 环境准备

- JDK 21
- Maven 3.6+
- MySQL 8.0+
- Redis

### 本地运行

1. 克隆项目到本地

   ```bash
   git clone <your-repository-url>
   cd campus_share-main
   ```
2. 创建数据库

   - 在你的 MySQL 中创建一个名为 `campus_share` 的数据库。
   - 项目启动时会自动执行 `schema.sql` 和 `data.sql` 来初始化表结构和数据。
3. 修改配置文件

   - 打开 `src/main/resources/application.properties` 文件。
   - 修改以下配置项以匹配你本地的环境：

     ```properties
     # 数据库连接
     spring.datasource.url=jdbc:mysql://localhost:3306/campus_share
     spring.datasource.username=<your-mysql-username>
     spring.datasource.password=<your-mysql-password>

     # Redis 配置
     spring.data.redis.host=localhost
     spring.data.redis.port=6379

     # 阿里云 OSS 配置 (如果需要文件上传功能)
     aliyun.oss.endpoint=<your-oss-endpoint>
     aliyun.oss.accessKeyId=<your-access-key-id>
     aliyun.oss.accessKeySecret=<your-access-key-secret>
     aliyun.oss.bucketName=<your-bucket-name>
     ```
4. 启动项目

   ```bash
   mvn spring-boot:run
   ```

## 📝 API 文档

项目启动后，可以通过以下地址访问 API 文档：

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## 🤝 贡献者

谭昕宇, 安泽瑞, 郑丽
