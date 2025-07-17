# 久伴视频后端项目

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

## 认证端点


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

## 🎨 轮播图系统

### 系统特点
- **统一管理**: 一个数据表管理电影、漫画、小说三种类型的轮播图
- **灵活配置**: 支持内部链接（跳转到具体内容）和外部链接
- **时间控制**: 支持设置轮播图的开始和结束显示时间
- **排序管理**: 支持自定义排序权重（sortOrder，数值越大越靠前）
- **状态控制**: 支持启用/禁用轮播图

### 数据库设计
```sql
-- banners表结构
CREATE TABLE banners (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,                    -- 轮播图标题
    subtitle VARCHAR(500),                          -- 副标题/描述
    image_url TEXT NOT NULL,                        -- 图片URL
    content_type ENUM('VIDEO','MANGA','NOVEL'),     -- 内容类型
    target_type ENUM('INTERNAL','EXTERNAL'),        -- 链接类型
    target_id BIGINT,                               -- 目标内容ID
    target_url TEXT,                                -- 目标链接URL
    sort_order INT DEFAULT 0,                       -- 排序权重
    is_active BOOLEAN DEFAULT TRUE,                 -- 是否启用
    start_time DATETIME,                            -- 开始显示时间
    end_time DATETIME,                              -- 结束显示时间
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 测试数据
- **电影轮播图**: 《寄生虫》、《复仇者联盟：终局之战》、《流浪地球》、《肖申克的救赎》
- **漫画轮播图**: 《进击的巨人》、《鬼灭之刃》、《海贼王》
- **小说轮播图**: 《斗破苍穹》、《完美世界》、《斗罗大陆》

### 使用示例
```javascript
// 前端获取电影轮播图
const response = await fetch('/api/banners/video?limit=3');
const result = await response.json();
console.log(result.banners); // 返回3个电影轮播图
```

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
│   │               ├── controller/           # 控制器层
│   │               │   ├── admin/           # 管理后台API接口
│   │               │   │   ├── AdminMovieController.java        # 电影管理接口
│   │               │   │   ├── AdminMovieCategoryController.java # 电影分类管理接口
│   │               │   │   ├── AdminUserController.java         # 用户管理接口
│   │               │   │   ├── AdminRegionController.java       # 地区管理接口
│   │               │   │   └── AdminFileUploadController.java   # 文件上传接口
│   │               │   └── h5/              # H5端API接口
│   │               │       ├── H5MovieController.java        # 电影相关接口
│   │               │       ├── H5MovieCategoryController.java # 电影分类接口
│   │               │       ├── H5UserController.java         # 用户相关接口
│   │               │       └── H5BannerController.java       # 轮播图接口
│   │               ├── service/            # 服务层
│   │               │   ├── admin/          # 管理后台服务
│   │               │   │   ├── IAdminMovieService.java          # 电影管理服务接口
│   │               │   │   ├── IAdminMovieCategoryService.java  # 电影分类管理服务接口
│   │               │   │   ├── IAdminUserService.java           # 用户管理服务接口
│   │               │   │   ├── IAdminRegionService.java         # 地区管理服务接口
│   │               │   │   └── IAdminFileUploadService.java     # 文件上传服务接口
│   │               │   │   └── impl/                       # 管理后台服务实现
│   │               │   │       ├── AdminMovieServiceImpl.java
│   │               │   │       ├── AdminMovieCategoryServiceImpl.java
│   │               │   │       ├── AdminUserServiceImpl.java
│   │               │   │       ├── AdminRegionServiceImpl.java
│   │               │   │       └── AdminFileUploadServiceImpl.java
│   │               │   └── h5/             # H5端服务
│   │               │       ├── IH5MovieService.java          # 电影服务接口
│   │               │       ├── IH5MovieCategoryService.java  # 电影分类服务接口
│   │               │       ├── IH5UserService.java           # 用户服务接口
│   │               │       ├── IH5RegionService.java         # 地区服务接口
│   │               │       └── IH5CaptchaService.java        # 验证码服务接口
│   │               │       └── impl/                       # H5端服务实现
│   │               │           ├── H5MovieServiceImpl.java
│   │               │           ├── H5MovieCategoryServiceImpl.java
│   │               │           ├── H5UserServiceImpl.java
│   │               │           ├── H5RegionServiceImpl.java
│   │               │           └── H5CaptchaServiceImpl.java
│   │               ├── common/            # 公共组件 公共服务接口
│   │               │   ├── ICaptchaService.java
│   │               │   ├── IH5LoginFailureService.java
│   │               │   ├── ILoginSecurityService.java
│   │               │   ├── IMinioService.java
│   │               │   ├── IRegistrationLimitService.java
│   │               │   └── impl/     # 公共服务实现
│   │               │       ├── CaptchaServiceImpl.java
│   │               │       ├── H5LoginFailureServiceImpl.java
│   │               │       ├── LoginSecurityServiceImpl.java
│   │               │       ├── MinioServiceImpl.java
│   │               │       └── RegistrationLimitServiceImpl.java
│   │               ├── model/             # 数据模型
│   │               ├── repository/        # 数据访问层
│   │               ├── config/           # 配置类
│   │               ├── security/         # 安全相关
│   │               ├── util/             # 公共工具类
│   │               └── dto/              # 数据传输对象
│   │                   ├── admin/        # 管理后台DTO
│   │                   │   ├── AdminLoginRequest.java
│   │                   │   ├── AdminMovieCategoryDTO.java
│   │                   │   ├── AdminMovieDTO.java
│   │                   │   ├── AdminRegionDTO.java
│   │                   │   ├── AdminFileUploadDTO.java
│   │                   │   ├── AdminUserDTO.java
│   │                   │   ├── AdminUserRequest.java
│   │                   │   └── UserStatistics.java
│   │                   └── h5/           # H5端DTO
│   │                       ├── H5LoginRequest.java 
│   │                       ├── H5RegisterRequest.java 
│   │                       ├── H5MovieCategoryDTO.java
│   │                       ├── H5MovieDTO.java
│   │                       ├── H5MovieDetailDTO.java
│   │                       ├── H5MoviePlayRequest.java
│   │                       ├── H5MoviePlayResponse.java
│   │                       └── H5RegionDTO.java  
│   └── resources/
│       ├── application.properties       # 配置文件
│       ├── static/                     # 静态资源
│       └── templates/                  # 模板文件
```

## API接口设计规范

### 1. 接口分层原则

#### 1.1 控制器和服务层分离
- **管理后台 (`controller/admin/` 和 `service/admin/`)**
  - 仅供管理后台使用的接口和服务
  - 需要管理员权限
  - Controller URL前缀: `/api/admin/`
  
- **H5端 (`controller/h5/` 和 `service/h5/`)**
  - 供移动端/H5端使用的接口和服务
  - 普通用户权限
  - Controller URL前缀: `/api/user/`

#### 1.2 业务逻辑分类
- 每个业务模块（如电影、用户、轮播图等）应该有独立的Controller和Service
- 公共功能（如验证码、文件存储等）放在common包下
- 避免在一个Controller或Service中混合不同模块的逻辑

### 2. 命名规范

#### 2.1 Controller命名
- 管理后台和H5端可以使用相同的命名，因为在不同的包下
- 名称应该表达功能，如：`MovieController`, `MovieCategoryController`

#### 2.2 Service命名
- 管理后台和H5端的Service接口可以使用相同的名称
- 实现类添加对应前缀，如：
  - H5端：`H5MovieServiceImpl`
  - 管理后台：`AdminMovieServiceImpl`

### 3. 接口设计原则

#### 3.1 URL路径设计
- 管理后台API示例：
  ```
  GET    /api/admin/movies           # 获取电影列表
  POST   /api/admin/movies           # 创建新电影
  PUT    /api/admin/movies/{id}      # 更新电影信息
  DELETE /api/admin/movies/{id}      # 删除电影
  ```

- H5端API示例：
  ```
  GET    /api/movies                 # 获取电影列表
  GET    /api/movies/{id}            # 获取电影详情
  GET    /api/movies/hot             # 获取热门电影
  GET    /api/movies/recommended     # 获取推荐电影
  ```

#### 3.2 权限控制
- 管理后台接口必须验证管理员权限
- H5端接口根据业务需求决定是否需要用户登录
- 使用Spring Security注解进行权限控制

### 4. 代码组织建议

#### 4.1 Controller层
- 职责：请求处理、参数验证、响应封装
- 不包含业务逻辑
- 统一的异常处理
- 统一的返回格式

#### 4.2 Service层
- 职责：业务逻辑实现
- 事务管理
- 复杂业务逻辑的封装
- 多个Repository的组合使用

#### 4.3 Repository层
- 职责：数据访问
- 基本的CRUD操作
- 复杂查询的实现

#### 4.4 DTO层
- 按照admin和h5分包
- 请求和响应对象分开定义
- 使用验证注解
- 提供转换方法

### 5. 特别说明

#### 5.1 公共功能
- 验证码服务
- 文件存储服务
- 缓存服务
- 消息服务
等公共功能统一放在common包下，方便复用

#### 5.2 安全考虑
- 管理后台和H5端使用不同的认证机制
- 管理后台需要更严格的权限控制
- H5端注重性能和用户体验

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

4. **JSON序列化循环引用错误** ⚠️
   - **症状**: `ERR_INVALID_CHUNKED_ENCODING`, `StackOverflowError`, `ByteBuddyInterceptor`序列化错误
   - **原因**: 实体类之间的双向关联导致循环引用（如Movie↔MovieCategory）
   - **解决方案**:
     - ✅ 在反向关联字段添加`@JsonIgnore`注解（如MovieCategory.movies字段）
     - ✅ 将关键关联字段改为EAGER加载策略避免Hibernate代理对象
     - ✅ 配置Jackson序列化选项：`fail-on-empty-beans=false`
   - **应用场景**: 任何包含双向JPA关联的实体类都可能遇到此问题

## ✅ 测试状态

### 最新测试结果 (2025-07-09)

- ✅ **三层加密系统**: 开发模式下完美工作
- ✅ **统一认证端点**: `/api/users/auth` 正常运行
- ✅ **自动注册功能**: 新用户自动创建成功
- ✅ **JWT Token生成**: 增强型JWT正常生成
- ✅ **CORS配置**: 跨域请求正常处理
- ✅ **数据库连接**: MySQL连接和操作正常
- ✅ **Spring Security**: 公开路径配置正确
- ✅ **电影API系统**: 热门电影、高评分电影、筛选选项等所有API正常
- ✅ **JSON序列化**: 循环引用问题已解决，数据正常返回
- ✅ **实体关联**: Movie↔MovieCategory↔Region关联映射正常工作
- ✅ **轮播图系统**: 支持电影、漫画、小说的自定义轮播图管理 🆕
- ✅ **用户管理模块**: 完整的用户账户管理功能，包括查询、编辑、锁定、VIP设置、余额调整等 🆕
- ✅ **管理员管理模块**: 完整的管理员账户管理功能，包括创建、编辑、删除、启用/禁用等 🆕

### 测试的API端点

#### 电影相关API
- `GET /api/movies/hot?page=0&size=6` - 热门电影列表 ✅
- `GET /api/movies/high-rated?minRating=8&page=0&size=3` - 高评分电影 ✅  
- `GET /api/movies/filters` - 筛选选项（分类、地区、画质等） ✅

#### 轮播图API 🆕
- `GET /api/banners/video?limit=3` - 电影轮播图 ✅ (4个轮播图)
- `GET /api/banners/manga?limit=3` - 漫画轮播图 ✅ (3个轮播图)
- `GET /api/banners/novel?limit=3` - 小说轮播图 ✅ (3个轮播图)
- `GET /api/banners/stats` - 轮播图统计信息 ✅ (总计10个轮播图)
- `GET /api/banners/all` - 所有轮播图（按类型分组）✅

#### 用户管理API 🆕
- `POST /admin/users/list` - 分页查询用户列表 ✅
- `GET /admin/users/{userId}` - 获取用户详情 ✅
- `PUT /admin/users/update` - 更新用户信息 ✅
- `DELETE /admin/users/{userId}` - 删除用户 ✅
- `POST /admin/users/{userId}/lock` - 锁定用户账户 ✅
- `POST /admin/users/{userId}/unlock` - 解锁用户账户 ✅
- `POST /admin/users/{userId}/vip` - 设置VIP状态 ✅
- `POST /admin/users/{userId}/balance` - 调整用户余额 ✅
- `GET /admin/users/statistics` - 获取用户统计信息 ✅

#### 管理员管理API 🆕
- `POST /admin/management/list` - 获取管理员列表 ✅
- `POST /admin/management/create` - 创建管理员 ✅
- `POST /admin/management/update` - 更新管理员信息 ✅
- `POST /admin/management/delete` - 删除管理员 ✅
- `POST /admin/management/toggle-status` - 启用/禁用管理员 ✅

### 测试环境
- Java 17+
- Spring Boot 3.2.3  
- MySQL 数据库
- 服务端口: 8080
- 前端源: localhost:4000

## 📋 用户管理模块

### 功能概述
用户管理模块为管理员提供了完整的用户账户管理功能，包括用户查询、信息编辑、账户锁定/解锁、VIP状态管理、余额调整等操作。

### 主要功能
1. **用户查询**：支持分页查询、多条件筛选、关键词搜索
2. **用户详情**：查看用户的完整信息
3. **账户管理**：锁定/解锁用户、重置登录失败次数
4. **VIP管理**：设置VIP状态、配置过期时间
5. **余额管理**：调整用户账户余额
6. **统计分析**：用户数量、VIP用户、锁定用户等统计

### 安全特性
- 管理员账户特殊保护，不能被删除或锁定
- 所有操作记录详细日志
- 软删除机制，保留数据完整性
- 严格的权限控制和数据验证

### 技术实现
- **后端**：Spring Boot + JPA + Spring Security
- **前端**：Vue 3 + Element Plus
- **数据库**：MySQL + Flyway迁移

详细功能说明请参考：[用户管理模块文档](README_USER_MANAGEMENT.md)

## 👥 管理员管理模块

### 功能概述
管理员管理模块允许超级管理员管理系统中的其他管理员账户，包括创建、编辑、删除和启用/禁用管理员。

### 主要功能
1. **管理员列表**：分页显示所有管理员，支持搜索和筛选
2. **统计信息**：总管理员数量、启用管理员数量、禁用管理员数量
3. **账户管理**：创建新管理员、编辑管理员信息、删除管理员
4. **状态控制**：启用/禁用管理员账户
5. **安全保护**：当前登录管理员和默认管理员账户受保护

### 安全特性
- 当前登录管理员不能删除自己或修改自己的状态
- 默认管理员账户（admin）受保护，不能删除或修改状态
- 所有操作需要管理员权限验证
- 完整的操作日志记录

### 技术实现
- **后端**：基于现有的IAdminService扩展管理员管理功能
- **前端**：Vue 3 + Element Plus + TypeScript
- **API设计**：RESTful风格，统一的响应格式

详细功能说明请参考：[管理员管理模块文档](README_ADMIN_MANAGEMENT.md)

## iDataRiver 支付集成说明

### 1. 配置
在 `src/main/resources/application.properties` 中添加：
```
# iDataRiver 支付API配置
idr.api.base-url=https://open.idatariver.com
idr.api.secret=你的商户Secret
```

### 2. 支付接口说明
所有接口均在 `/api/payment/idr` 路径下，供前端调用。

#### 2.1 创建商品订单
- **接口**：POST `/api/payment/idr/order/create`
- **参数**（JSON）：
  - `projectId`：项目ID（必填）
  - `skuId`：商品ID（必填）
  - `quantity`：购买数量（必填，默认1）
  - `contactInfo`：联系方式（必填，建议邮箱/手机号，默认：BakerBrenda6834@outlook.com）
- **返回**：下单结果（包含订单ID等信息）

#### 2.2 获取订单支付链接
- **接口**：POST `/api/payment/idr/order/pay`
- **参数**（JSON）：
  - `orderId`：订单ID（必填）
  - `method`：支付方式（必填，需从订单详情接口获取）
  - `redirectUrl`：支付成功后跳转链接（选填）
  - `callbackUrl`：支付/退款回调通知链接（选填，需https域名）
- **返回**：支付链接等信息

#### 2.3 查询订单详情
- **接口**：GET `/api/payment/idr/order/info?orderId=xxx`
- **参数**：
  - `orderId`：订单ID（必填）
- **返回**：订单详情

#### 2.4 支付/退款回调接口
- **接口**：POST `/api/payment/idr/order/callback`
- **参数**：回调数据（JSON，iDataRiver平台自动推送）
- **返回**：`success` 或 `fail`

### 3. 调用示例

#### 创建订单
```json
POST /api/payment/idr/order/create
{
  "projectId": "your_project_id",
  "skuId": "your_sku_id",
  "quantity": 1,
  "contactInfo": "user@example.com"
}
```

#### 获取支付链接
```json
POST /api/payment/idr/order/pay
{
  "orderId": "your_order_id",
  "method": "alipay", // 或wechat等，需查订单详情接口
  "redirectUrl": "https://yourdomain.com/order/success",
  "callbackUrl": "https://yourdomain.com/api/payment/idr/order/callback"
}
```

#### 查询订单
```
GET /api/payment/idr/order/info?orderId=your_order_id
```

### 4. 回调说明
- iDataRiver平台支付/退款后会自动POST回调到`callbackUrl`，请保证该接口可公网访问且为https。
- 回调数据请参考iDataRiver官方文档。
- 回调时建议主动再次请求订单详情接口，确认订单真实状态。

### 5. 注意事项
- 商户密钥（idr.api.secret）严禁前端暴露，所有API调用均在后端完成。
- 支持所有支付方式，method参数需从订单详情接口获取。
- 联系方式建议填写用户唯一标识（如邮箱、手机号等）。
- 所有接口返回均含有code字段，0为成功，其他为失败。

如有疑问请联系后端开发或查阅iDataRiver官方文档。

## H5端绑定邮箱功能接口

### 1. 发送邮箱验证码
- 接口路径：`POST /users/send-email-code`
- 请求参数：
  - `email` (string) 邮箱地址
- 返回值：
  - `success` (boolean)
  - `message` (string)
- 说明：5分钟内同一邮箱只能发送一次验证码，验证码有效期5分钟。

### 2. 绑定邮箱
- 接口路径：`POST /users/bind-email`
- 请求参数：
  - `email` (string) 邮箱地址
  - `code` (string) 验证码
- 返回值：
  - `success` (boolean)
  - `message` (string)
- 说明：验证码校验通过后，邮箱将绑定到当前登录用户。

### 3. 前端调用流程
1. 用户输入邮箱，点击“获取验证码”按钮，调用`/users/send-email-code`。
2. 用户收到验证码，输入验证码后点击“绑定”，调用`/users/bind-email`。
3. 成功后前端展示绑定结果。