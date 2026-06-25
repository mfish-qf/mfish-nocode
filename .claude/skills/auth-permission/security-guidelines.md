# 安全注意事项

## 🔒 重要安全原则

### 1. 内部认证不能仅依赖 Header

**风险**: `req-origin: inner` 只是一个普通 HTTP Header，不能作为强内部身份凭证。

**问题场景**:
```
客户端 → 绕过网关 → 直接访问业务服务
       → 手动添加 req-origin: inner Header
       → 绕过 AuthorizationAspect 和 DataScopeAspect
       → 访问内部接口
```

**解决方案**:
- ✅ **网络隔离**: 业务服务不暴露公网，只能通过内网访问
- ✅ **服务间认证**: 使用 mTLS、服务网格身份、密钥签名等
- ✅ **网关/注册中心隔离**: 通过注册中心网络策略限制访问
- ❌ **不要仅依赖**: `req-origin: inner` Header

### 2. Header 清理机制

**当前问题**: 网关只显式移除了 `req-origin`，没有清理其他用户上下文 Header。

**风险**:
- 客户端可能伪造 `req-user-id`、`req-tenant-id`、`req-account`
- Header 多值问题（客户端传入 + 网关注入）
- 大小写不一致导致清理失败

**推荐实现**:

```java
// mf-gateway/AuthFilter.java
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    
    // 1. 先清理所有用户上下文 Header
    ServerHttpRequest decoratedRequest = request.mutate()
        .header("req-origin")
        .remove()
        .header("req-user-id")
        .remove()
        .header("req-tenant-id")
        .remove()
        .header("req-account")
        .remove()
        .build();
    
    // 2. 校验 token
    String token = extractToken(request);
    if (!validateToken(token)) {
        return unauthorizedResponse(exchange);
    }
    
    // 3. 解析用户信息
    String userId = parseUserId(token);
    String tenantId = parseTenantId(token);
    String account = parseAccount(token);
    
    // 4. 重新写入用户上下文 Header
    ServerHttpRequest finalRequest = decoratedRequest.mutate()
        .header("req-user-id", userId)
        .header("req-tenant-id", tenantId)
        .header("req-account", account)
        .build();
    
    return chain.filter(exchange.mutate().request(finalRequest).build());
}
```

### 3. Feign 拦截器白名单透传

**当前问题**: `BearerTokenInterceptor` 会复制几乎所有 Header（只过滤 content-length）。

**风险**:
- 客户端 Header 污染下游服务
- 可能传递不应该透传的敏感信息
- 内部标识被错误传递

**当前实现**:
```java
// 风险：透传了太多 Header
public void apply(RequestTemplate template) {
    HttpServletRequest request = getRequest();
    Enumeration<String> headerNames = request.getHeaderNames();
    
    while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        if (!"content-length".equalsIgnoreCase(headerName)) {
            template.header(headerName, request.getHeaders(headerName));
        }
    }
}
```

**推荐实现（白名单）**:
```java
// 安全：只透传必要的 Header
private static final Set<String> ALLOWED_HEADERS = new HashSet<>(Arrays.asList(
    "Authorization",
    "req-user-id",
    "req-tenant-id",
    "req-account",
    "X-Trace-Id",        // 链路追踪
    "X-Request-Id"       // 请求追踪
));

public void apply(RequestTemplate template) {
    HttpServletRequest request = getRequest();
    
    for (String headerName : ALLOWED_HEADERS) {
        String headerValue = request.getHeader(headerName);
        if (headerValue != null) {
            template.header(headerName, headerValue);
        }
    }
    
    // 内部标识由 Feign 客户端显式设置
    if (isInternalCall()) {
        template.header("req-origin", "inner");
    }
}
```

## ⚠️ 风险规避

### 1. 权限绕过风险

**AuthorizationAspect 和 DataScopeAspect 的行为**:
```java
// 遇到 inner 直接跳过权限校验
if ("inner".equals(origin)) {
    // 跳过权限检查
    return;
}
```

**风险场景**:
- 如果业务服务端口被绕过网关直接访问
- 请求方自己添加 `req-origin: inner`
- 可能跳过所有权限校验

**解决方案**:

#### 方案 A: 区分内部调用类型

```java
@InnerUser(validateUser = false, type = InnerType.SYSTEM)
public void systemInternalCall() {
    // 系统级内部调用：跳过用户权限
    // 适用于：数据同步、定时任务
}

@InnerUser(validateUser = true, type = InnerType.DELEGATE)
public void delegateUserCall() {
    // 代用户内部调用：保留用户上下文，执行权限校验
    // 适用于：跨服务业务操作
}

@InnerUser(validateUser = false, type = InnerType.ADMIN)
public void adminInternalCall() {
    // 管理型内部接口：需要服务权限或 scope
    // 适用于：管理后台、运维接口
}
```

#### 方案 B: 增加服务间签名

```java
// 内部调用需要携带签名
@PostMapping("/internal/sync")
@InnerUser(validateUser = false)
public Result sync(
    @RequestHeader("X-Service-Signature") String signature,
    @RequestHeader("X-Service-Name") String serviceName
) {
    // 验证服务签名
    if (!SignatureValidator.validate(signature, serviceName)) {
        throw new SecurityException("Invalid service signature");
    }
    
    // 业务逻辑...
    return Result.ok();
}
```

### 2. 网络隔离策略

**必须做到的**:
- ✅ 业务服务不绑定公网 IP
- ✅ 通过防火墙/安全组限制端口访问
- ✅ 只有网关和内部服务可以访问业务服务端口
- ✅ 使用 VPC、子网等网络隔离手段

**推荐做到的**:
- ✅ 服务间使用 mTLS 双向认证
- ✅ 服务网格（如 Istio）进行身份验证
- ✅ 注册中心网络策略限制
- ✅ API 网关统一入口

### 3. 租户隔离风险

**风险场景**:
```java
// 错误：没有租户隔离
public List<Data> queryData() {
    return mapper.selectList();  // 可能查询到其他租户数据
}

// 正确：租户隔离
public List<Data> queryData() {
    String tenantId = AuthInfoUtils.getCurrentTenantId();
    return mapper.selectList(
        new QueryWrapper<Data>().eq("tenant_id", tenantId)
    );
}
```

**注意事项**:
- 所有数据查询都必须考虑租户隔离
- MyBatis 拦截器可以自动添加租户条件
- 内部调用也要注意租户边界

### 4. 用户上下文伪造

**防护措施**:

1. **网关层**:
   - 严格校验 token 签名
   - 清理所有客户端传入的用户上下文 Header
   - 使用 HTTPS 防止中间人攻击

2. **业务服务层**:
   - 信任网关注入的 Header，不信任客户端
   - 定期检查服务是否暴露公网
   - 监控异常的访问模式

3. **网络层**:
   - 业务服务端口只对网关开放
   - 使用内网通信
   - 配置安全组规则

## 安全检查清单

### 开发阶段
- [ ] 使用 AuthInfoUtils 获取用户信息，不直接读取 Header
- [ ] 内部接口正确使用 @InnerUser 注解
- [ ] 数据查询包含租户隔离条件
- [ ] 审计字段（createBy, updateBy）自动填充
- [ ] 异常信息不暴露敏感数据给前端

### 部署阶段
- [ ] 业务服务不暴露公网
- [ ] 配置网络安全组规则
- [ ] 启用 HTTPS
- [ ] 配置服务间认证（如果支持）
- [ ] 监控和日志记录

### 运维阶段
- [ ] 定期检查服务端口暴露情况
- [ ] 监控异常访问模式
- [ ] 定期轮换服务密钥
- [ ] 审计内部接口调用日志
- [ ] 及时更新安全补丁

## 常见安全漏洞示例

### ❌ 错误示例 1: 直接读取 Header

```java
// 危险：可能被客户端伪造
@GetMapping("/data")
public Result getData(HttpServletRequest request) {
    String userId = request.getHeader("req-user-id");  // ❌
    // 业务逻辑...
}
```

### ✅ 正确示例 1: 使用 AuthInfoUtils

```java
// 安全：通过统一的工具类获取
@GetMapping("/data")
public Result getData() {
    String userId = AuthInfoUtils.getCurrentUserId();  // ✅
    // 业务逻辑...
}
```

### ❌ 错误示例 2: 内部接口无保护

```java
// 危险：任何知道 URL 的人都可以调用
@PostMapping("/internal/sync")
public Result sync() {
    // 业务逻辑...
}
```

### ✅ 正确示例 2: 内部接口加保护

```java
// 安全：需要 req-origin: inner Header
@PostMapping("/internal/sync")
@InnerUser(validateUser = false)
public Result sync() {
    // 业务逻辑...
}
```

### ❌ 错误示例 3: 无租户隔离

```java
// 危险：可能查询到其他租户数据
public List<Data> queryAll() {
    return mapper.selectList();  // ❌
}
```

### ✅ 正确示例 3: 租户隔离

```java
// 安全：只查询当前租户数据
public List<Data> queryAll() {
    String tenantId = AuthInfoUtils.getCurrentTenantId();
    return mapper.selectList(
        new QueryWrapper<Data>().eq("tenant_id", tenantId)
    );  // ✅
}
```

## 应急响应

### 发现安全漏洞时

1. **立即措施**:
   - 隔离受影响的服务
   - 阻止外部直接访问
   - 记录所有访问日志

2. **修复措施**:
   - 修补安全漏洞
   - 加强网络隔离
   - 更新认证机制

3. **后续措施**:
   - 全面安全审计
   - 更新安全检查清单
   - 加强监控告警

## 总结

安全是一个系统工程，需要多层次防护：

1. **网络层**: 网络隔离、防火墙、安全组
2. **传输层**: HTTPS、mTLS
3. **应用层**: Token 校验、Header 清理、权限控制
4. **数据层**: 租户隔离、数据加密
5. **监控层**: 日志审计、异常检测、告警

**核心原则**: 不要信任任何来自外部的输入，包括 HTTP Header！
