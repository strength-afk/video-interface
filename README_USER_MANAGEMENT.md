# 用户管理模块功能说明

## 概述

用户管理模块为管理员提供了完整的用户账户管理功能，包括用户查询、信息更新、账户锁定/解锁、VIP状态管理、余额调整等操作。

## 功能特性

### 1. 用户查询功能
- **分页查询**：支持分页显示用户列表
- **多条件筛选**：
  - 关键词搜索（用户名、邮箱、手机号）
  - 用户状态筛选（正常、未激活、锁定）
  - 用户角色筛选（管理员、普通用户、VIP）
  - VIP状态筛选（是/否）
  - 锁定状态筛选（已锁定/未锁定）
- **排序功能**：支持按创建时间、最后登录时间等字段排序
- **时间范围筛选**：支持按注册时间范围筛选

### 2. 用户信息管理
- **查看用户详情**：显示用户的完整信息
- **更新用户信息**：修改用户基本信息
- **删除用户**：软删除用户账户（管理员账户不可删除）

### 3. 账户安全管理
- **锁定用户**：
  - 支持设置锁定原因
  - 支持设置锁定时长（分钟），0表示永久锁定
  - 管理员账户不可被锁定
- **解锁用户**：解除用户账户锁定状态
- **重置登录失败次数**：清除用户的登录失败记录

### 4. VIP状态管理
- **设置VIP状态**：开启或关闭用户VIP权限
- **设置过期时间**：支持设置VIP过期时间，留空表示永久VIP

### 5. 余额管理
- **调整余额**：
  - 支持增加或减少用户余额
  - 需要填写调整原因
  - 防止余额变为负数

### 6. 统计信息
- **用户统计**：
  - 总用户数
  - VIP用户数
  - 锁定用户数
  - 今日/本周/本月新增用户数
  - 今日/本周/本月活跃用户数
  - 管理员用户数
  - 普通用户数

## API接口

### 1. 用户列表查询
```
POST /admin/users/list
```
**请求参数：**
```json
{
  "page": 1,
  "size": 10,
  "keyword": "搜索关键词",
  "statusFilter": "ACTIVE",
  "roleFilter": "USER",
  "vipFilter": true,
  "lockedFilter": false,
  "startTime": "2024-01-01T00:00:00",
  "endTime": "2024-12-31T23:59:59",
  "sortBy": "createdAt",
  "sortDirection": "desc"
}
```

**响应数据：**
```json
{
  "content": [
    {
      "id": 1,
      "username": "user1",
      "email": "user1@example.com",
      "phoneNumber": "13800138000",
      "isVip": true,
      "vipExpireTime": "2024-12-31T23:59:59",
      "accountBalance": 100.00,
      "watchTime": 120,
      "isLocked": false,
      "status": "ACTIVE",
      "role": "USER",
      "lastLoginTime": "2024-01-15T10:30:00",
      "createdAt": "2024-01-01T00:00:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "currentPage": 1,
  "size": 10
}
```

### 2. 用户详情查询
```
GET /admin/users/{userId}
```

### 3. 更新用户信息
```
PUT /admin/users/update
```

### 4. 删除用户
```
DELETE /admin/users/{userId}
```

### 5. 锁定用户
```
POST /admin/users/{userId}/lock?reason=违规操作&lockDuration=60
```

### 6. 解锁用户
```
POST /admin/users/{userId}/unlock
```

### 7. 重置登录失败次数
```
POST /admin/users/{userId}/reset-login-attempts
```

### 8. 设置VIP状态
```
POST /admin/users/{userId}/vip?isVip=true&expireTime=2024-12-31T23:59:59
```

### 9. 调整余额
```
POST /admin/users/{userId}/balance?amount=50.00&reason=充值
```

### 10. 获取统计信息
```
GET /admin/users/statistics
```

## 数据库设计

### User表字段说明
- `id`: 用户ID，主键
- `username`: 用户名，唯一
- `email`: 邮箱地址，唯一
- `phone_number`: 手机号码
- `avatar`: 用户头像URL
- `is_vip`: 是否为VIP用户
- `vip_expire_time`: VIP过期时间
- `account_balance`: 账户余额
- `watch_time`: 观看时长（分钟）
- `last_login_time`: 最后登录时间
- `last_login_ip`: 最后登录IP地址
- `is_locked`: 账户是否被锁定
- `lock_reason`: 账户锁定原因
- `failed_login_attempts`: 连续登录失败次数
- `last_failed_login_time`: 最后一次登录失败时间
- `lock_time`: 账户锁定时间
- `unlock_time`: 账户自动解锁时间
- `status`: 用户状态（ACTIVE/INACTIVE/LOCKED/DELETED）
- `role`: 用户角色（ADMIN/USER/VIP）
- `created_at`: 创建时间
- `updated_at`: 更新时间

## 安全考虑

1. **权限控制**：只有管理员可以访问用户管理功能
2. **数据保护**：管理员账户不能被删除或锁定
3. **操作日志**：所有管理操作都会记录详细日志
4. **数据验证**：所有输入数据都进行严格验证
5. **软删除**：用户删除采用软删除方式，保留数据完整性

## 使用说明

### 管理员操作流程

1. **查看用户列表**：
   - 进入用户管理页面
   - 使用搜索和筛选功能查找目标用户
   - 点击"查看"按钮查看用户详情

2. **管理用户状态**：
   - 锁定用户：选择用户 → 更多操作 → 锁定 → 填写原因和时长
   - 解锁用户：选择用户 → 更多操作 → 解锁 → 确认操作

3. **设置VIP状态**：
   - 选择用户 → 更多操作 → 设置VIP → 选择状态和过期时间

4. **调整余额**：
   - 选择用户 → 更多操作 → 调整余额 → 输入金额和原因

5. **删除用户**：
   - 选择用户 → 更多操作 → 删除 → 确认删除（管理员账户不可删除）

## 注意事项

1. 所有操作都需要管理员权限
2. 管理员账户具有特殊保护，不能被删除或锁定
3. 用户删除为软删除，数据仍保留在数据库中
4. 余额调整需要填写原因，便于审计
5. 锁定用户时建议填写详细原因
6. VIP过期时间留空表示永久VIP
7. 所有操作都有相应的成功/失败提示

## 技术实现

### 后端技术栈
- Spring Boot 3.x
- Spring Data JPA
- Spring Security
- MySQL数据库
- JWT认证

### 前端技术栈
- Vue 3
- Element Plus
- Axios
- Vue Router

### 代码结构
```
video-interface/
├── controller/admin/
│   └── AdminUserController.java
├── service/admin/
│   ├── IAdminUserService.java
│   └── impl/AdminUserServiceImpl.java
├── dto/admin/
│   ├── AdminUserDTO.java
│   ├── AdminUserRequest.java
│   └── UserStatistics.java
└── repository/
    └── UserRepository.java

video-admin/
├── api/
│   └── user.js
└── views/Users/
    └── index.vue
``` 