# FB视频后端项目

这是一个基于Spring Boot的视频网站后端项目。

## 开发说明

```bash
# 使用Maven包装器运行项目
./mvnw spring-boot:run

# 或者使用已安装的Maven
mvn spring-boot:run

# 打包
./mvnw package
```

## 技术栈

- Spring Boot 3.5.3
- Spring Data JPA
- MySQL
- Maven
- Java 17

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── video_interface/
│   │               ├── controller/    # 控制器
│   │               ├── model/         # 数据模型
│   │               ├── repository/    # 数据访问
│   │               └── service/       # 业务逻辑
│   └── resources/
│       ├── application.properties    # 配置文件
│       ├── static/                  # 静态资源
│       └── templates/               # 模板文件
```

## 配置说明

1. 数据库配置（application.properties）:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/fb_video
   spring.datasource.username=root
   spring.datasource.password=root
   ```

## 注意事项

1. 请确保已安装Java 17或更高版本
2. 运行前需要创建MySQL数据库fb_video
3. 修改application.properties中的数据库配置以匹配您的环境
4. 所有API接口都应该有适当的文档注释
5. 确保所有的异常处理都符合项目规范 

## 数据库迁移修复步骤

如果遇到Flyway迁移问题，请按以下步骤操作：

1. 停止应用程序
2. 备份数据库（重要！）
   ```bash
   mysqldump -u root -p fb_video > fb_video_backup.sql
   ```

3. 执行Flyway修复
   ```bash
   ./mvnw flyway:repair
   ```

4. 重启应用程序
   ```bash
   ./mvnw spring-boot:run
   ```

如果还遇到问题，可以尝试：
1. 清理Flyway历史记录（注意：这将删除所有迁移历史！）
   ```bash
   ./mvnw flyway:clean
   ```
2. 重新执行迁移
   ```bash
   ./mvnw flyway:migrate
   ```

## 常见问题解决

1. 403 Forbidden错误
   - 检查SecurityConfig中的CORS配置
   - 确保API路径前缀正确（/api）
   - 验证请求头中的Authorization格式

2. CORS预检请求失败
   - 在WebMvcConfig中允许所需的请求头和方法
   - 确保前端请求配置正确
   - 检查allowedOrigins配置

3. 数据库字段错误
   - 执行最新的数据库迁移脚本
   - 检查实体类与数据库表结构是否匹配
   - 使用JPA的validate模式验证映射 