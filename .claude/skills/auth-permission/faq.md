# 常见问题解答 (FAQ)

## 认证相关

### Q1: 如何在业务代码中获取当前用户信息？

**A**: 使用 `AuthInfoUtils` 工具类：

```java
String userId = AuthInfoUtils.getCurrentUserId();
String tenantId = AuthInfoUtils.getCurrentTenantId();
String account = AuthInfoUtils.getCurrentAccount();
```

**注意**: 
- 不要在异步任务中直接使用（会返回 null）
- 不要直接从 HttpServletRequest 中读取 Header

---

### Q2: AuthInfoUtils 在什么场景下会返回 null？

**A**: 以下场景会返回 null：

1. **异步任务**: CompletableFuture、线程池、@Async
2. **定时任务**: @Scheduled 注解的方法
3. **MQ 消费**: RabbitListener、KafkaListener
4. **未登录请求**: 公开接口（如登录接口）
5. **单元测试**: 没有 Mock Request 上下文

**解决方案**: 显式传递用户信息或使用系统用户 ID。

---

### Q3: 微服务模式和单体模式下，AuthInfoUtils 有什么区别？

**A**: 

| 模式 | 数据来源 | 说明 |
|------|---------|------|
| 微服务 | HTTP Header | 网关校验 token 后写入 Header |
| 单体 | Request Attribute | OAuth Filter 校验后放入 Attribute |

AuthInfoUtils 内部会自动兼容两种模式，业务代码无需关心差异。

---

### Q4: 如何判断用户是否已登录？

**A**: 

```java
String userId = AuthInfoUtils.getCurrentUserId();
if (userId == null) {
    // 用户未登录
    throw new UnauthorizedException("请先登录");
}
```

或者使用注解：

```java
// 需要登录才能访问的接口
@GetMapping("/user/profile")
public Result getProfile() {
    // AuthInfoUtils 会确保用户已登录
}

// 公开接口，无需登录
@GetMapping("/public/info")
@Anonymous
public Result getPublicInfo() {
    // 可以不登录访问
}
```

---

## 权限相关

### Q5: @InnerUser 注解的作用是什么？

**A**: 标记内部接口，配合 `req-origin: inner` Header 使用。

```java
@InnerUser(validateUser = false)  // 系统级内部调用，跳过用户校验
@PostMapping("/internal/sync")
public Result sync() { }

@InnerUser(validateUser = true)   // 代用户内部调用，保留用户校验
@PostMapping("/internal/query")
public Result query() { }
```

---

### Q6: 内部接口可以被外部直接调用吗？

**A**: 理论上可以（如果知道 URL 并添加了正确的 Header），但应该通过以下措施防止：

1. **网络隔离**: 业务服务不暴露公网
2. **服务间认证**: 密钥签名、mTLS
3. **监控告警**: 检测异常调用模式

---

### Q7: AuthorizationAspect 和 DataScopeAspect 的作用是什么？

**A**:

- **AuthorizationAspect**: 校验用户是否有权限访问接口（基于角色、权限等）
- **DataScopeAspect**: 过滤用户能访问的数据范围（基于部门、角色等）

内部调用时（`req-origin: inner`），这两个切面会跳过校验。

---

### Q8: 如何实现数据权限过滤？

**A**: DataScopeAspect 会自动处理，只需在 SQL 中预留占位符：

```xml
<!-- Mapper XML -->
<select id="selectList" resultType="Data">
    SELECT * FROM t_data
    ${dataScope}  <!-- DataScopeAspect 会自动替换为权限条件 -->
</select>
```

---

## 内部调用相关

### Q9: Feign 调用时，用户上下文是如何传递的？

**A**: 通过 `BearerTokenInterceptor` 自动透传 Header：

```
服务 A (用户上下文)
    ↓ Feign 调用
BearerTokenInterceptor 复制 Header
    ↓
服务 B (收到相同的 Header)
    ↓
AuthInfoUtils 可以正常获取用户信息
```

---

### Q10: 如何在 Feign 调用中手动设置 Header？

**A**: 

**方式 1**: 使用 `@RequestHeader` 注解

```java
@FeignClient(name = "mf-sys")
public interface RemoteSysService {
    @GetMapping("/api/user")
    Result getUser(
        @RequestHeader("req-user-id") String userId,
        @RequestParam("id") String id
    );
}
```

**方式 2**: 使用 RequestInterceptor

```java
@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("X-Custom-Header", "value");
        };
    }
}
```

---

### Q11: 内部调用时，如何区分是系统调用还是代用户调用？

**A**: 通过 `@InnerUser(validateUser)` 参数：

- `validateUser = false`: 系统级调用，跳过用户校验
- `validateUser = true`: 代用户调用，执行用户校验

---

### Q12: 服务 A 调用服务 B，服务 B 再调用服务 C，用户上下文会丢失吗？

**A**: 不会。BearerTokenInterceptor 会在每次 Feign 调用时透传 Header：

```
客户端 → 服务 A (Header) → 服务 B (Header) → 服务 C (Header)
```

每一层都可以通过 `AuthInfoUtils` 获取用户信息。

---

## 异步场景相关

### Q13: 异步任务中如何获取用户信息？

**A**: 在主线程捕获后传递：

```java
public void asyncMethod() {
    // 1. 主线程捕获
    String userId = AuthInfoUtils.getCurrentUserId();
    
    // 2. 传递到异步任务
    CompletableFuture.runAsync(() -> {
        processWithUser(userId);
    });
}
```

详见 [异步场景处理](./async-context.md)。

---

### Q14: @Async 注解的方法能获取用户信息吗？

**A**: 默认不能。需要配置 TaskDecorator：

```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // ...
        
        executor.setTaskDecorator(runnable -> {
            UserContextSnapshot snapshot = UserContextSnapshot.capture();
            return () -> {
                try {
                    snapshot.restore();
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        });
        
        return executor;
    }
}
```

---

### Q15: MQ 消费者如何获取用户信息？

**A**: MQ 消息没有 HTTP 上下文，需要在消息体中携带：

```java
@Data
public class OrderMessage {
    private String orderId;
    private String userId;    // 必需
    private String tenantId;  // 必需
}

@RabbitListener(queues = "order.queue")
public void consume(OrderMessage message) {
    String userId = message.getUserId();  // 从消息体获取
    // 业务逻辑...
}
```

---

## 安全相关

### Q16: req-origin: inner 是否安全？

**A**: 不够安全！它只是普通 HTTP Header，可以被伪造。

**必须配合**:
- 网络隔离（业务服务不暴露公网）
- 服务间认证（密钥签名、mTLS）
- 监控告警

---

### Q17: 客户端能否伪造 req-user-id 等 Header？

**A**: 如果网关没有清理，客户端可以伪造。

**网关应该**:
```java
// 先清理
request.mutate().header("req-user-id").remove();
// 再写入
request.mutate().header("req-user-id", userId);
```

---

### Q18: 如何防止越权访问？

**A**: 

1. **接口权限**: AuthorizationAspect 校验
2. **数据权限**: DataScopeAspect 过滤
3. **租户隔离**: 查询时加上 tenant_id 条件
4. **业务校验**: 验证资源归属

```java
public Order getOrder(String orderId) {
    String userId = AuthInfoUtils.getCurrentUserId();
    
    Order order = orderMapper.selectById(orderId);
    if (!order.getUserId().equals(userId)) {
        throw new SecurityException("无权访问");
    }
    
    return order;
}
```

---

### Q19: 异常信息会暴露给前端吗？

**A**: 不应该！异常信息应该记录在日志中，返回给前端的应该是友好提示：

```java
try {
    // 业务逻辑
} catch (Exception e) {
    log.error("操作失败", e);  // 记录详细错误
    throw new MyRuntimeException("操作失败，请稍后重试");  // 返回友好提示
}
```

---

## 测试相关

### Q20: 单元测试中如何 Mock 用户上下文？

**A**: 

```java
@Test
void testWithUser() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("req-user-id", "test-user");
    request.addHeader("req-tenant-id", "test-tenant");
    
    RequestContextHolder.setRequestAttributes(
        new ServletRequestAttributes(request)
    );
    
    try {
        // 执行业务逻辑
        myService.doSomething();
        
        // 验证结果
    } finally {
        RequestContextHolder.resetRequestAttributes();
    }
}
```

---

### Q21: 集成测试中如何测试内部接口？

**A**: 

```java
@Test
void testInternalApi() {
    MvcResult result = mockMvc.perform(
        post("/internal/sync")
            .header("req-origin", "inner")
            .header("req-user-id", "test-user")
    )
    .andExpect(status().isOk())
    .andReturn();
}
```

---

## 配置相关

### Q22: 如何配置匿名接口（无需认证）？

**A**: 在网关或 OAuth 配置中排除：

```yaml
oauth:
  ignore-urls:
    - /api/public/**
    - /api/auth/login
    - /api/auth/captcha
```

---

### Q23: 如何配置内部接口白名单？

**A**: 使用 `@InnerUser` 注解即可，不需要额外配置。

如果需要限制调用方，可以自定义注解：

```java
@InnerUser
@RequireSourceService({"mf-order", "mf-payment"})
@PostMapping("/internal/sync")
public Result sync() { }
```

---

### Q24: Token 过期如何处理？

**A**: 网关会返回 401 错误，前端应该跳转到登录页。

业务服务不需要处理 token 过期（由网关统一处理）。

---

## 性能相关

### Q25: AuthInfoUtils 的性能如何？

**A**: 很高。只是从 Request 中读取 Header，几乎没有性能损耗。

---

### Q26: Feign 透传 Header 会影响性能吗？

**A**: 影响很小。Header 数量不多，复制操作很快。

如果担心性能，可以改为白名单透传（只透传必要的 Header）。

---

## 调试相关

### Q27: 如何查看当前请求的用户信息？

**A**: 

**方式 1**: 日志输出

```java
log.info("User: {}, Tenant: {}, Account: {}", 
         AuthInfoUtils.getCurrentUserId(),
         AuthInfoUtils.getCurrentTenantId(),
         AuthInfoUtils.getCurrentAccount());
```

**方式 2**: Debug 断点

在 `AuthInfoUtils.getCurrentUserId()` 打断点查看。

---

### Q28: Feign 调用失败，如何查看传递的 Header？

**A**: 启用 Feign 日志：

```yaml
feign:
  client:
    config:
      default:
        loggerLevel: FULL

logging:
  level:
    cn.com.mfish: DEBUG
```

---

### Q29: 如何排查权限校验失败的问题？

**A**: 

1. **检查日志**: 查看 AuthorizationAspect 和 DataScopeAspect 的日志
2. **检查用户信息**: 确认 `AuthInfoUtils` 返回正确的用户信息
3. **检查权限配置**: 确认用户角色和权限正确
4. **检查数据权限**: 确认数据范围过滤条件正确

---

## 最佳实践

### Q30: 使用认证权限体系的最佳实践有哪些？

**A**: 

1. ✅ 统一使用 `AuthInfoUtils` 获取用户信息
2. ✅ 合理选择内部调用类型（系统级/代用户/管理型）
3. ✅ 异步任务显式传递用户信息
4. ✅ 所有数据查询考虑租户隔离
5. ✅ 审计字段自动填充（createBy、updateBy）
6. ✅ 异常信息不暴露给前端
7. ✅ 内部接口加网络隔离和服务认证
8. ✅ 定期审查内部接口权限

---

### Q31: 常见的安全漏洞有哪些？

**A**: 

1. ❌ 直接从 Header 读取用户信息（可能被伪造）
2. ❌ 内部接口无保护（任何人都可以调用）
3. ❌ 无租户隔离（可能查询到其他租户数据）
4. ❌ 异步任务中直接使用 AuthInfoUtils（会返回 null）
5. ❌ 异常信息暴露给前端（泄露系统信息）

详见 [安全注意事项](./security-guidelines.md)。

---

### Q32: 如何快速上手？

**A**: 

**最小知识集**:
1. 获取用户信息: `AuthInfoUtils.getCurrentUserId()`
2. 内部接口: `@InnerUser(validateUser = true/false)`
3. 异步传递: 主线程捕获，作为参数传递
4. 租户隔离: 查询时加上 `tenant_id` 条件

**进一步学习**:
- 阅读 [架构概览](./architecture.md)
- 学习 [开发规范](./development-guide.md)
- 了解 [安全注意事项](./security-guidelines.md)

---

## 总结

如果你遇到认证或权限相关的问题：

1. **先查本文档**: 大多数常见问题都在这里
2. **查看相关章节**: 根据问题类型查看对应文档
3. **检查代码**: 对照最佳实践检查代码
4. **查看日志**: 日志通常能快速定位问题

**核心原则**: 
- 使用统一工具（AuthInfoUtils）
- 注意异步场景
- 重视安全防护
- 遵循开发规范
