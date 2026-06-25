# 认证架构概览

## 整体认证链路

```
客户端请求
    ↓
[mf-gateway] AuthFilter 校验 token
    ↓
写入用户上下文 Header:
  - req-user-id
  - req-tenant-id
  - req-account
    ↓
[业务服务] 通过 AuthInfoUtils 从 Header 获取
    ↓
[Feign 调用] BearerTokenInterceptor 透传 Header
    ↓
[下游服务] 继续使用 AuthInfoUtils 获取用户信息
```

## 核心组件说明

### 1. 网关认证过滤器 (AuthFilter)

**位置**: `mf-gateway/src/main/java/cn/com/mfish/gateway/filter/AuthFilter.java`

**职责**:
- 校验请求中的 token
- 解析用户身份信息
- 将用户信息写入请求 Header
- 移除外部传入的 `req-origin` Header

**处理流程**:
1. 验证 token 有效性
2. 从 token 中提取用户 ID、租户 ID、账号
3. 移除客户端传入的 `req-origin` Header
4. 写入 `req-user-id`、`req-tenant-id`、`req-account`
5. 转发请求到下游服务

### 2. 用户上下文工具 (AuthInfoUtils)

**位置**: `mf-common/mf-common-core/src/main/java/cn/com/mfish/common/core/utils/AuthInfoUtils.java`

**职责**:
- 提供统一的用户信息获取接口
- 兼容微服务和单体两种启动模式
- 从 Header 或 Request Attribute 中读取用户信息

**主要方法**:
```java
// 获取当前用户 ID
String getCurrentUserId()

// 获取当前租户 ID
String getCurrentTenantId()

// 获取当前账号
String getCurrentAccount()
```

**兼容性说明**:
- **微服务模式**: 从 HTTP Header 中读取
- **单体模式**: 从 Request Attribute 中读取（由 TokenFilter 设置）

### 3. OAuth Token 过滤器 (TokenFilter)

**位置**: `mf-oauth/src/main/java/cn/com/mfish/oauth/filter/TokenFilter.java`

**职责** (单体模式):
- 校验 token
- 将用户信息放入 Request Attribute
- 与 AuthInfoUtils 配合使用

### 4. Feign 拦截器 (BearerTokenInterceptor)

**位置**: `mf-common/mf-common-cloud/src/main/java/cn/com/mfish/common/cloud/feign/BearerTokenInterceptor.java`

**职责**:
- 在 Feign 调用时透传用户上下文 Header
- 保持服务间调用的用户身份连续性

**当前行为**:
- 复制几乎所有 Header（只过滤 content-length）
- 透传 Authorization Header

**⚠️ 风险**:
- 会透传所有客户端 Header，存在污染风险
- 建议改为白名单透传机制

### 5. 内部调用保护

**注解**: `@InnerUser`
**位置**: `mf-common/mf-common-core/src/main/java/cn/com/mfish/common/core/annotation/InnerUser.java`

**职责**:
- 标记内部接口
- 配合 `req-origin: inner` Header 使用
- 可选择是否校验用户身份

**使用方式**:
```java
// 跳过用户校验（系统级内部调用）
@InnerUser(validateUser = false)

// 保留用户上下文（代用户内部调用）
@InnerUser(validateUser = true)
```

## 数据流向图

```
┌─────────────┐
│  客户端请求   │
└──────┬──────┘
       │
       ↓
┌─────────────────────────────────┐
│      mf-gateway (AuthFilter)     │
│  1. 校验 token                    │
│  2. 移除 req-origin              │
│  3. 写入 req-user-id             │
│     req-tenant-id                │
│     req-account                  │
└──────┬──────────────────────────┘
       │
       ↓
┌─────────────────────────────────┐
│      业务服务                     │
│  AuthInfoUtils.getCurrentXXX()  │
│  - 从 Header 读取 (微服务)       │
│  - 从 Attribute 读取 (单体)      │
└──────┬──────────────────────────┘
       │
       ↓ (Feign 调用)
┌─────────────────────────────────┐
│  BearerTokenInterceptor          │
│  透传 Header 到下游服务           │
└──────┬──────────────────────────┘
       │
       ↓
┌─────────────────────────────────┐
│      下游服务                     │
│  AuthInfoUtils.getCurrentXXX()  │
└─────────────────────────────────┘
```

## 配置示例

### 网关配置
网关需要确保在转发前先清理再写入用户上下文 Header：
```java
// 推荐的实现方式
request.mutate()
    .header("req-origin")  // 先移除
    .remove()
    .header("req-user-id", userId)  // 再写入
    .header("req-tenant-id", tenantId)
    .header("req-account", account)
    .build();
```

### 业务服务使用
```java
@Service
public class MyService {
    public void doSomething() {
        String userId = AuthInfoUtils.getCurrentUserId();
        String tenantId = AuthInfoUtils.getCurrentTenantId();
        
        // 业务逻辑...
    }
}
```

## 注意事项

1. **Header 优先级**: 网关注入的 Header 应该覆盖客户端传入的
2. **大小写敏感**: HTTP Header 名称可能大小写不一致，需要兼容处理
3. **多值问题**: 确保 Header 不会出现多个值的情况
4. **网络隔离**: 业务服务不应暴露给外部直接访问
