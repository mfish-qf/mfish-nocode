# 内部调用安全

## 概述

内部调用是指服务之间的 RPC 调用，需要平衡**便利性**和**安全性**。本文档详细说明内部调用的安全机制和最佳实践。

## 内部调用类型

根据业务需求，内部调用分为三种类型：

### 1. 系统级内部调用

**特点**:
- 服务身份鉴权
- 跳过用户权限校验
- 不需要用户上下文

**使用场景**:
- 数据同步
- 定时任务触发
- 系统维护接口
- 缓存刷新

**示例**:

```java
/**
 * 数据同步接口
 * 由其他服务调用，不需要用户上下文
 */
@InnerUser(validateUser = false)
@PostMapping("/internal/sync-data")
public Result syncData() {
    // 同步全量数据，不受数据权限限制
    List<Data> allData = dataMapper.selectList();
    syncToRemote(allData);
    return Result.ok();
}
```

**安全要求**:
- ✅ 网络隔离（业务服务不暴露公网）
- ✅ 服务间认证（密钥签名、mTLS 等）
- ✅ 访问日志记录

### 2. 代用户内部调用

**特点**:
- 保留用户上下文
- 执行权限校验
- 受数据权限限制

**使用场景**:
- 跨服务业务操作
- 级联查询
- 用户操作审计

**示例**:

```java
/**
 * 查询用户订单详情
 * 保留用户上下文，执行权限校验
 */
@InnerUser(validateUser = true)
@GetMapping("/internal/order/{orderId}")
public Result<OrderDetail> getOrderDetail(@PathVariable String orderId) {
    // 获取当前用户信息
    String userId = AuthInfoUtils.getCurrentUserId();
    String tenantId = AuthInfoUtils.getCurrentTenantId();
    
    // 查询订单（受数据权限限制）
    OrderDetail order = orderMapper.selectDetail(orderId);
    
    // 验证订单属于当前用户
    if (!order.getUserId().equals(userId)) {
        throw new SecurityException("无权访问此订单");
    }
    
    return Result.ok(order);
}
```

**安全要求**:
- ✅ 用户上下文通过 Header 透传
- ✅ 执行权限校验（AuthorizationAspect）
- ✅ 执行数据权限过滤（DataScopeAspect）
- ✅ 租户隔离

### 3. 管理型内部接口

**特点**:
- 服务权限或 scope 校验
- 可能有或没有用户上下文
- 管理后台专用

**使用场景**:
- 管理后台操作
- 运维接口
- 配置管理

**示例**:

```java
/**
 * 系统配置管理
 * 需要管理员权限
 */
@InnerUser(validateUser = false)
@RequireScope("admin:config:manage")
@PostMapping("/internal/config/update")
public Result updateConfig(@RequestBody ConfigUpdateReq req) {
    // 更新系统配置
    configService.update(req);
    return Result.ok();
}
```

**安全要求**:
- ✅ 服务权限校验
- ✅ Scope 或角色校验
- ✅ 操作审计日志
- ✅ 网络隔离

## Feign 内部调用实现

### 1. 定义 Feign 接口

```java
/**
 * 远程用户服务接口
 */
@FeignClient(name = "mf-sys", path = "/api")
public interface RemoteUserService {
    
    /**
     * 查询用户信息（代用户调用）
     * 会自动透传用户上下文 Header
     */
    @GetMapping("/user/info")
    Result<UserInfo> getUserInfo(@RequestParam("userId") String userId);
    
    /**
     * 同步用户数据（系统级调用）
     * 需要手动设置 req-origin Header
     */
    @PostMapping("/internal/sync-users")
    Result syncUsers(@RequestBody List<User> users);
}
```

### 2. 调用内部接口

```java
@Service
public class OrderService {
    
    @Autowired
    private RemoteUserService remoteUserService;
    
    /**
     * 创建订单（代用户调用）
     */
    public void createOrder(Order order) {
        // 当前用户上下文会自动通过 BearerTokenInterceptor 透传
        Result<UserInfo> result = remoteUserService.getUserInfo(order.getUserId());
        
        if (!result.isSuccess()) {
            throw new BusinessException("用户不存在");
        }
        
        // 继续创建订单逻辑...
    }
    
    /**
     * 同步订单数据（系统级调用）
     */
    public void syncOrders() {
        // 系统级调用不需要用户上下文
        List<Order> orders = orderMapper.selectList();
        remoteOrderService.syncOrders(orders);
    }
}
```

### 3. Header 透传机制

**BearerTokenInterceptor 当前行为**:

```java
public void apply(RequestTemplate template) {
    HttpServletRequest request = getRequest();
    Enumeration<String> headerNames = request.getHeaderNames();
    
    // 复制几乎所有 Header（只过滤 content-length）
    while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        if (!"content-length".equalsIgnoreCase(headerName)) {
            template.header(headerName, request.getHeaders(headerName));
        }
    }
}
```

**透传的 Header**:
- `Authorization`: 认证令牌
- `req-user-id`: 用户 ID
- `req-tenant-id`: 租户 ID
- `req-account`: 账号
- `req-origin`: 内部标识（如果有）
- 其他所有 Header

**⚠️ 风险**: 见 [安全注意事项](./security-guidelines.md)

## 服务间认证增强

### 方案 1: 服务密钥签名

**实现**:

```java
/**
 * 服务间签名生成器
 */
@Component
public class ServiceSignatureGenerator {
    
    @Value("${service.secret}")
    private String serviceSecret;
    
    /**
     * 生成签名
     */
    public String generateSignature(String serviceName, long timestamp) {
        String data = serviceName + ":" + timestamp;
        return HmacUtils.hmacSha256Hex(serviceSecret, data);
    }
    
    /**
     * 验证签名
     */
    public boolean validateSignature(String serviceName, String signature, long timestamp) {
        // 验证时间戳（5 分钟内有效）
        if (System.currentTimeMillis() - timestamp > 5 * 60 * 1000) {
            return false;
        }
        
        String expectedSignature = generateSignature(serviceName, timestamp);
        return expectedSignature.equals(signature);
    }
}
```

**使用**:

```java
// 调用方：添加签名
@PostMapping("/internal/sync")
public Result sync() {
    String serviceName = "order-service";
    long timestamp = System.currentTimeMillis();
    String signature = signatureGenerator.generateSignature(serviceName, timestamp);
    
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Service-Name", serviceName);
    headers.set("X-Service-Signature", signature);
    headers.set("X-Service-Timestamp", String.valueOf(timestamp));
    
    // 发送请求...
}

// 接收方：验证签名
@PostMapping("/internal/sync")
@InnerUser(validateUser = false)
public Result sync(
    @RequestHeader("X-Service-Name") String serviceName,
    @RequestHeader("X-Service-Signature") String signature,
    @RequestHeader("X-Service-Timestamp") long timestamp
) {
    if (!signatureGenerator.validateSignature(serviceName, signature, timestamp)) {
        throw new SecurityException("Invalid service signature");
    }
    
    // 业务逻辑...
}
```

### 方案 2: mTLS 双向认证

**配置** (如果使用 Spring Cloud Gateway):

```yaml
# application.yml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: changeit
    trust-store: classpath:truststore.p12
    trust-store-password: changeit
    client-auth: need  # 强制客户端证书认证
```

### 方案 3: 服务网格（Istio）

**PeerAuthentication**:

```yaml
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default
spec:
  mtls:
    mode: STRICT  # 强制 mTLS
```

**AuthorizationPolicy**:

```yaml
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: service-policy
spec:
  selector:
    matchLabels:
      app: mf-sys
  rules:
  - from:
    - source:
        principals: ["cluster.local/ns/default/sa/mf-gateway"]
    to:
    - operation:
        methods: ["GET", "POST"]
```

## 内部接口安全设计

### 1. 最小权限原则

```java
/**
 * ❌ 错误：权限过大
 */
@InnerUser(validateUser = false)
@PostMapping("/internal/admin/*")
public Result adminOperation() {
    // 所有内部服务都可以调用
}

/**
 * ✅ 正确：限制调用方
 */
@InnerUser(validateUser = false)
@RequireSourceService({"mf-order", "mf-payment"})
@PostMapping("/internal/sync")
public Result sync() {
    // 只有指定服务可以调用
}
```

### 2. 参数校验

```java
/**
 * 内部接口也要严格校验参数
 */
@InnerUser(validateUser = true)
@PostMapping("/internal/create-order")
public Result createOrder(@Valid @RequestBody CreateOrderReq req) {
    // 校验业务参数
    if (req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        throw new BusinessException("订单金额必须大于 0");
    }
    
    // 业务逻辑...
}
```

### 3. 限流和熔断

```java
/**
 * 内部接口也要限流
 */
@InnerUser(validateUser = false)
@RateLimiter(value = 100, timeout = 60)
@PostMapping("/internal/sync")
public Result sync() {
    // 每秒最多 100 次调用
}
```

### 4. 审计日志

```java
/**
 * 记录内部接口调用日志
 */
@InnerUser(validateUser = false)
@PostMapping("/internal/sync")
public Result sync() {
    String serviceName = getRequest().getHeader("X-Service-Name");
    String userId = AuthInfoUtils.getCurrentUserId();
    
    log.info("Internal call from service: {}, user: {}", serviceName, userId);
    
    // 记录审计日志
    auditLogService.logInternalCall(serviceName, userId, "sync");
    
    // 业务逻辑...
}
```

## 监控和告警

### 1. 监控指标

```java
/**
 * 内部调用监控
 */
@Component
public class InternalCallMetrics {
    
    private final MeterRegistry meterRegistry;
    
    public void recordCall(String serviceName, String api, boolean success, long duration) {
        Timer.builder("internal.call.duration")
            .tag("service", serviceName)
            .tag("api", api)
            .tag("success", String.valueOf(success))
            .register(meterRegistry)
            .record(Duration.ofMillis(duration));
    }
}
```

### 2. 异常告警

```java
/**
 * 内部调用异常告警
 */
@Aspect
@Component
@Slf4j
public class InternalCallAlertAspect {
    
    @AfterThrowing(pointcut = "@annotation(InnerUser)", throwing = "ex")
    public void alertInternalCallException(JoinPoint joinPoint, Exception ex) {
        String serviceName = getRequest().getHeader("X-Service-Name");
        String api = joinPoint.getSignature().toShortString();
        
        log.error("Internal call failed: service={}, api={}, error={}", 
                  serviceName, api, ex.getMessage());
        
        // 发送告警
        alertService.sendAlert(
            "InternalCallFailed",
            String.format("Service %s called %s failed: %s", 
                         serviceName, api, ex.getMessage())
        );
    }
}
```

## 安全检查清单

### 开发阶段

- [ ] 明确内部调用类型（系统级/代用户/管理型）
- [ ] 正确使用 `@InnerUser` 注解
- [ ] 设置合适的 `validateUser` 参数
- [ ] 添加参数校验（@Valid）
- [ ] 记录审计日志
- [ ] 添加限流和熔断

### 部署阶段

- [ ] 配置网络隔离
- [ ] 启用服务间认证（如果支持）
- [ ] 配置 HTTPS/mTLS
- [ ] 设置合理的限流策略
- [ ] 配置监控和告警

### 运维阶段

- [ ] 定期检查内部接口调用日志
- [ ] 监控异常调用模式
- [ ] 定期轮换服务密钥
- [ ] 审查内部接口权限
- [ ] 及时更新安全策略

## 常见问题

### Q1: 内部接口是否需要参数校验？

**A**: 是的！内部接口也要严格校验参数，防止服务 bug 导致的数据问题。

### Q2: 系统级内部调用如何追踪操作用户？

**A**: 可以传递一个系统用户 ID 或在审计日志中标记为系统操作。

### Q3: 如何防止内部接口被滥用？

**A**: 
- 网络隔离（最基本）
- 服务间认证
- 限流策略
- 访问白名单
- 审计日志

### Q4: 代用户调用时，下游服务如何知道是内部调用？

**A**: 通过 `req-origin: inner` Header 标识，下游服务的 `@InnerUser` 注解会处理。

### Q5: 内部调用失败如何重试？

**A**: 使用 Feign 的重试机制或消息队列的失败重试：

```java
@FeignClient(name = "mf-sys", configuration = FeignRetryConfig.class)
public interface RemoteSysService {
    // ...
}

@Configuration
public class FeignRetryConfig {
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, 1000, 3);  // 最多重试 3 次
    }
}
```

## 总结

内部调用安全的关键点：

1. **区分调用类型**: 根据业务需求选择合适的内部调用类型
2. **多层防护**: 网络隔离 + 服务认证 + 权限校验
3. **最小权限**: 只授予必要的权限
4. **审计追踪**: 记录所有内部调用日志
5. **监控告警**: 及时发现异常调用

**核心原则**: 内部调用≠无条件信任，仍然需要多层次的安全防护！
