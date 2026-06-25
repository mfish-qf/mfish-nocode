# 认证与权限开发指南

## 概述

本指南描述了摸鱼低代码平台的认证与权限架构，适用于需要处理用户身份验证、权限控制、数据权限等相关业务开发场景。

## 架构概览

### 认证链路
- **网关层**: mf-gateway/AuthFilter 校验 token，写入用户上下文 Header
- **业务服务**: 通过 `AuthInfoUtils` 从 Header 获取用户上下文
- **单体模式**: mf-oauth/TokenFilter 将信息放到 request attribute，AuthInfoUtils 兼容 attribute
- **服务间通信**: BearerTokenInterceptor 透传 Header 和 Authorization 给 Feign 下游服务
- **内部接口**: 使用 `req-origin: inner` + `@InnerUser` 做内部调用保护

### 用户上下文 Header
- `req-user-id`: 当前用户 ID
- `req-tenant-id`: 当前租户 ID  
- `req-account`: 当前账号
- `req-origin: inner`: 内部调用标识

## 目录结构

- [架构概览](./architecture.md) - 完整的认证链路和组件说明
- [开发规范](./development-guide.md) - 获取用户信息、内部接口开发规范
- [安全注意事项](./security-guidelines.md) - 安全原则和风险规避
- [异步场景处理](./async-context.md) - 异步任务中的用户上下文处理
- [内部调用安全](./internal-call-security.md) - 内部服务调用的安全机制
- [常见问题](./faq.md) - 常见问题和解决方案

## 快速开始

### 获取当前用户信息
```java
String userId = AuthInfoUtils.getCurrentUserId();
String tenantId = AuthInfoUtils.getCurrentTenantId();
String account = AuthInfoUtils.getCurrentAccount();
```

### 标记内部接口
```java
@InnerUser
@PostMapping("/internal/api")
public Result internalApi() {
    // 内部接口逻辑
}
```

## 核心组件位置
- 网关认证过滤器: `mf-gateway/src/main/java/cn/com/mfish/gateway/filter/AuthFilter.java`
- 用户上下文工具: `mf-common/mf-common-core/src/main/java/cn/com/mfish/common/core/utils/AuthInfoUtils.java`
- 内部用户注解: `mf-common/mf-common-core/src/main/java/cn/com/mfish/common/core/annotation/InnerUser.java`
- 权限切面: `mf-common/mf-common-core/src/main/java/cn/com/mfish/common/core/aop/AuthorizationAspect.java`
- 数据权限切面: `mf-common/mf-common-core/src/main/java/cn/com/mfish/common/core/aop/DataScopeAspect.java`
- Feign 拦截器: `mf-common/mf-common-cloud/src/main/java/cn/com/mfish/common/cloud/feign/BearerTokenInterceptor.java`
