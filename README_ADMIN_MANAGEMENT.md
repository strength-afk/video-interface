# 管理员管理功能说明

## 功能概述

管理员管理功能允许超级管理员管理系统中的其他管理员账户，包括创建、编辑、删除和启用/禁用管理员。

## 功能特性

### 1. 管理员列表
- 分页显示所有管理员
- 支持按用户名/邮箱搜索
- 支持按状态筛选（正常/未激活）
- 显示管理员基本信息：ID、用户名、邮箱、角色、状态、最后登录时间

### 2. 统计信息
- 总管理员数量
- 启用管理员数量
- 禁用管理员数量

### 3. 管理员操作
- **创建管理员**：添加新的管理员账户
- **编辑管理员**：修改管理员信息（用户名、邮箱、密码）
- **启用/禁用管理员**：控制管理员账户状态
- **删除管理员**：永久删除管理员账户

## 技术实现

### 后端架构
- **控制层**：`AdminController` - 处理HTTP请求
- **服务层**：`IAdminService` - 业务逻辑接口
- **实现层**：`AdminServiceImpl` - 业务逻辑实现
- **数据层**：`UserRepository` - 数据访问

### 前端架构
- **页面组件**：`Admins.vue` - 管理员管理页面
- **API接口**：`admin.ts` - 管理员管理API
- **状态管理**：使用Vue 3 Composition API

## API接口

### 1. 获取管理员列表
```
POST /admin/management/list
```

**请求参数：**
```json
{
  "page": 1,
  "size": 10,
  "username": "admin",
  "email": "admin@example.com",
  "enabled": true
}
```

**响应数据：**
```json
{
  "code": 200,
  "message": "获取管理员列表成功",
  "data": {
    "list": [...],
    "total": 5,
    "page": 1,
    "size": 10,
    "totalPages": 1,
    "statistics": {
      "totalAdmins": 5,
      "enabledAdmins": 4,
      "disabledAdmins": 1
    }
  }
}
```

### 2. 创建管理员
```
POST /admin/management/create
```

**请求参数：**
```json
{
  "username": "newadmin",
  "password": "password123",
  "email": "newadmin@example.com"
}
```

### 3. 更新管理员
```
POST /admin/management/update
```

**请求参数：**
```json
{
  "id": 1,
  "username": "updatedadmin",
  "password": "newpassword",
  "email": "updated@example.com"
}
```

### 4. 删除管理员
```
POST /admin/management/delete
```

**请求参数：**
```json
{
  "id": 1
}
```

### 5. 启用/禁用管理员
```
POST /admin/management/toggle-status
```

**请求参数：**
```json
{
  "id": 1,
  "enabled": true
}
```

## 安全限制

1. **当前登录管理员保护**：不能删除或修改当前登录的管理员状态
2. **默认管理员保护**：不能删除或修改用户名为"admin"的管理员
3. **权限验证**：所有操作都需要管理员权限

## 数据模型

### AdminUserDTO
```java
public class AdminUserDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String status;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### AdminManagementRequest
```java
public class AdminManagementRequest {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String nickname;
    private Boolean enabled;
    private Integer page;
    private Integer size;
}
```

## 使用说明

1. **访问管理员管理页面**：在系统设置中点击"管理员管理"
2. **查看管理员列表**：页面自动加载所有管理员信息
3. **搜索管理员**：使用搜索框按用户名或邮箱搜索
4. **筛选管理员**：使用状态下拉框筛选正常/未激活的管理员
5. **添加管理员**：点击"添加管理员"按钮，填写表单信息
6. **编辑管理员**：点击操作列的"编辑"按钮修改信息
7. **启用/禁用管理员**：点击操作列的"启用"/"禁用"按钮
8. **删除管理员**：点击操作列的"删除"按钮（需要确认）

## 注意事项

1. 管理员密码在创建时必须提供，编辑时可选
2. 用户名和邮箱必须唯一
3. 删除操作不可恢复，请谨慎操作
4. 当前登录的管理员不能删除自己或修改自己的状态
5. 默认管理员账户（admin）受保护，不能删除或修改状态 