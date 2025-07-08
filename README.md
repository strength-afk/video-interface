# FB视频后端项目

这是一个基于Spring Boot的视频网站后端项目，集成了三层加密系统和统一认证功能。

## 🔐 三层加密系统

项目实现了完整的三层加密安全架构：

1. **前端加密层**: 使用AES-256-CTR + HMAC-SHA256对敏感数据进行加密和签名
2. **传输安全层**: 基于HTTPS的安全传输通道
3. **后端解密层**: DecryptionFilter自动解密和验证请求数据

### 开发模式配置
```properties
# 开发模式 - 跳过加密验证
app.crypto.security.require-signature=false
```

## 🔑 统一认证端点

系统提供统一的认证API，自动判断登录或注册：

```bash
# 测试统一认证端点
./test_auth.sh

# 或手动测试
curl -X POST http://localhost:8080/api/users/auth \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:4000" \
  -d '{"username":"testuser","password":"123456"}'
```

- **新用户**: 自动执行注册流程
- **现有用户**: 验证密码并登录
- **返回**: JWT token + 完整用户信息

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

## ✅ 测试状态

### 最新测试结果 (2025-07-08)

- ✅ **三层加密系统**: 开发模式下完美工作
- ✅ **统一认证端点**: `/api/users/auth` 正常运行
- ✅ **自动注册功能**: 新用户自动创建成功
- ✅ **JWT Token生成**: 增强型JWT正常生成
- ✅ **CORS配置**: 跨域请求正常处理
- ✅ **数据库连接**: MySQL连接和操作正常
- ✅ **Spring Security**: 公开路径配置正确

### 测试环境
- Java 23.0.2
- Spring Boot 3.2.3  
- MySQL 9.3
- 服务端口: 8080
- 前端源: localhost:4000

**系统运行状态**: 🟢 正常 