# 异步场景处理

## 问题说明

`AuthInfoUtils` 依赖当前 HTTP Request 上下文，在异步场景下会丢失用户信息：

```java
// ❌ 错误：异步线程中无法获取用户上下文
public void asyncMethod() {
    CompletableFuture.runAsync(() -> {
        String userId = AuthInfoUtils.getCurrentUserId();  // null!
        // 业务逻辑...
    });
}
```

**原因**:
- `AuthInfoUtils` 通过 `RequestContextHolder` 获取当前请求信息
- 异步线程（线程池、CompletableFuture、MQ 消费等）不在原请求上下文中
- 新线程无法访问原请求的 Header 或 Attribute

## 解决方案

### 方案 1: 显式传递用户信息（推荐）

在发起异步调用前捕获用户上下文，作为参数传递：

```java
@Service
public class MyService {
    
    public void processAsync() {
        // 1. 在主线程捕获用户上下文
        String userId = AuthInfoUtils.getCurrentUserId();
        String tenantId = AuthInfoUtils.getCurrentTenantId();
        String account = AuthInfoUtils.getCurrentAccount();
        
        // 2. 传递到异步任务
        CompletableFuture.runAsync(() -> {
            processInAsync(userId, tenantId, account);
        });
    }
    
    private void processInAsync(String userId, String tenantId, String account) {
        // 使用传递过来的用户信息
        log.info("Processing for user: {}", userId);
        
        // 业务逻辑...
    }
}
```

### 方案 2: 封装 UserContext 快照

创建用户上下文对象，便于传递：

```java
/**
 * 用户上下文快照
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContextSnapshot {
    private String userId;
    private String tenantId;
    private String account;
    
    /**
     * 从当前请求创建快照
     */
    public static UserContextSnapshot capture() {
        return new UserContextSnapshot(
            AuthInfoUtils.getCurrentUserId(),
            AuthInfoUtils.getCurrentTenantId(),
            AuthInfoUtils.getCurrentAccount()
        );
    }
    
    /**
     * 恢复用户上下文到当前线程
     */
    public void restore() {
        // 如果需要，可以设置到当前线程的上下文
        MDC.put("userId", userId);
        MDC.put("tenantId", tenantId);
    }
}
```

**使用示例**:

```java
@Service
public class MyService {
    
    public void processWithSnapshot() {
        // 1. 创建快照
        UserContextSnapshot snapshot = UserContextSnapshot.capture();
        
        // 2. 在异步任务中使用
        CompletableFuture.runAsync(() -> {
            // 恢复上下文（可选）
            snapshot.restore();
            
            // 使用快照中的数据
            log.info("Processing for user: {}", snapshot.getUserId());
            
            // 业务逻辑...
        });
    }
}
```

### 方案 3: 自定义线程池传递上下文

创建自定义线程池，自动传递用户上下文：

```java
/**
 * 支持用户上下文传递的线程池
 */
public class UserContextThreadPoolExecutor extends ThreadPoolExecutor {
    
    public UserContextThreadPoolExecutor(
        int corePoolSize,
        int maximumPoolSize,
        long keepAliveTime,
        TimeUnit unit,
        BlockingQueue<Runnable> workQueue
    ) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }
    
    @Override
    public void execute(Runnable command) {
        // 捕获主线程的用户上下文
        UserContextSnapshot snapshot = UserContextSnapshot.capture();
        
        // 包装任务，在子线程中恢复上下文
        super.execute(() -> {
            try {
                snapshot.restore();
                command.run();
            } finally {
                // 清理上下文
                MDC.clear();
            }
        });
    }
}
```

**配置线程池**:

```java
@Configuration
public class ThreadPoolConfig {
    
    @Bean("userContextThreadPool")
    public Executor userContextThreadPool() {
        return new UserContextThreadPoolExecutor(
            5,   // corePoolSize
            10,  // maximumPoolSize
            60,  // keepAliveTime
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100)
        );
    }
}
```

**使用线程池**:

```java
@Service
public class MyService {
    
    @Autowired
    @Qualifier("userContextThreadPool")
    private Executor executor;
    
    public void processWithThreadPool() {
        // 用户上下文会自动传递到线程池中的任务
        executor.execute(() -> {
            // 可以直接使用 AuthInfoUtils（如果在线程池中恢复了上下文）
            String userId = AuthInfoUtils.getCurrentUserId();
            
            // 或者使用 MDC 中的信息
            String userIdFromMDC = MDC.get("userId");
            
            // 业务逻辑...
        });
    }
}
```

### 方案 4: 使用 @Async 注解

如果使用 Spring 的 `@Async`，需要配置异步拦截器：

```java
/**
 * 异步方法拦截器，传递用户上下文
 */
public class UserContextAsyncInterceptor implements AsyncUncaughtExceptionHandler {
    
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("Async method error: {}", method.getName(), ex);
    }
}

/**
 * 异步配置
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        
        // 设置任务装饰器，传递用户上下文
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
        
        executor.initialize();
        return executor;
    }
}
```

**使用 @Async**:

```java
@Service
public class MyService {
    
    /**
     * 异步方法，用户上下文会自动传递
     */
    @Async
    public CompletableFuture<Void> asyncProcess() {
        String userId = MDC.get("userId");  // 可以获取到
        log.info("Async processing for user: {}", userId);
        
        // 业务逻辑...
        return CompletableFuture.completedFuture(null);
    }
}
```

## 具体场景示例

### 1. CompletableFuture

```java
@Service
public class OrderService {
    
    public void createOrder(Order order) {
        // 捕获用户上下文
        String userId = AuthInfoUtils.getCurrentUserId();
        String tenantId = AuthInfoUtils.getCurrentTenantId();
        
        // 保存订单
        orderMapper.insert(order);
        
        // 异步发送通知
        CompletableFuture.runAsync(() -> {
            sendOrderNotification(order.getId(), userId, tenantId);
        });
    }
    
    private void sendOrderNotification(String orderId, String userId, String tenantId) {
        log.info("Sending notification for order: {}, user: {}", orderId, userId);
        // 发送通知逻辑...
    }
}
```

### 2. 定时任务

```java
@Component
public class ScheduledTask {
    
    /**
     * 定时任务没有用户上下文
     * 需要使用系统用户或指定用户执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void dailyReport() {
        // 使用系统用户 ID
        String systemUserId = "system";
        String tenantId = "all";
        
        log.info("Generating daily report, executed by: {}", systemUserId);
        
        // 生成报表逻辑...
    }
}
```

### 3. MQ 消息消费

```java
@Component
public class MessageConsumer {
    
    /**
     * MQ 消费者没有 HTTP 请求上下文
     * 需要在消息体中携带用户信息
     */
    @RabbitListener(queues = "order.queue")
    public void consumeOrderMessage(OrderMessage message) {
        // 从消息体中获取用户信息
        String userId = message.getUserId();
        String tenantId = message.getTenantId();
        
        log.info("Processing order message for user: {}", userId);
        
        // 处理消息逻辑...
    }
}
```

**消息体设计**:

```java
@Data
public class OrderMessage {
    private String orderId;
    private String userId;       // 必需
    private String tenantId;     // 必需
    private String account;      // 可选
    // 其他业务字段...
}
```

### 4. 批量异步处理

```java
@Service
public class BatchService {
    
    @Autowired
    @Qualifier("userContextThreadPool")
    private Executor executor;
    
    /**
     * 批量异步处理
     */
    public void batchProcess(List<String> itemIds) {
        String userId = AuthInfoUtils.getCurrentUserId();
        String tenantId = AuthInfoUtils.getCurrentTenantId();
        
        List<CompletableFuture<Void>> futures = itemIds.stream()
            .map(itemId -> CompletableFuture.runAsync(() -> {
                processItem(itemId, userId, tenantId);
            }, executor))
            .collect(Collectors.toList());
        
        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
    
    private void processItem(String itemId, String userId, String tenantId) {
        log.info("Processing item: {} for user: {}", itemId, userId);
        // 处理逻辑...
    }
}
```

## 最佳实践

### ✅ 推荐做法

1. **显式传递**: 优先选择显式传递用户信息参数
2. **快照封装**: 使用 `UserContextSnapshot` 封装上下文
3. **线程池装饰**: 自定义线程池自动传递上下文
4. **消息携带**: MQ 消息体中包含用户信息
5. **日志记录**: 异步任务日志中包含用户 ID

### ❌ 避免做法

1. **直接调用**: 不要在异步任务中直接调用 `AuthInfoUtils`
2. **静态变量**: 不要用静态变量存储用户上下文（线程不安全）
3. **ThreadLocal 滥用**: 谨慎使用 ThreadLocal，注意内存泄漏
4. **忽略上下文**: 异步任务中忽略用户上下文（影响审计和权限）

## 注意事项

### 1. 内存泄漏

使用 ThreadLocal 或 MDC 时，务必在任务完成后清理：

```java
executor.execute(() -> {
    try {
        snapshot.restore();
        task.run();
    } finally {
        MDC.clear();  // 清理上下文
    }
});
```

### 2. 线程池队列溢出

异步任务过多可能导致队列溢出：

```java
// 设置合理的队列大小和拒绝策略
executor.setQueueCapacity(100);
executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
```

### 3. 事务管理

异步任务中无法使用主线程的事务：

```java
// ❌ 错误：异步任务不在原事务中
@Transactional
public void createOrder(Order order) {
    orderMapper.insert(order);
    
    CompletableFuture.runAsync(() -> {
        // 这个操作不在上面的事务中
        auditMapper.insert(audit);  // 独立事务
    });
}
```

### 4. 异常处理

异步任务的异常需要妥善处理：

```java
CompletableFuture.runAsync(() -> {
    try {
        processItem(itemId, userId, tenantId);
    } catch (Exception e) {
        log.error("Async task failed for item: {}, user: {}", itemId, userId, e);
        // 记录失败信息，便于后续重试
    }
});
```

## 总结

| 场景 | 推荐方案 | 用户上下文来源 |
|------|---------|---------------|
| CompletableFuture | 显式传递参数 | 主线程捕获 |
| @Async | TaskDecorator | 自动传递 |
| 自定义线程池 | TaskDecorator | 自动传递 |
| MQ 消费 | 消息体携带 | 消息内容 |
| 定时任务 | 系统用户 | 配置指定 |
| 批量处理 | 线程池 + 显式传递 | 主线程捕获 |

**核心原则**: 异步任务必须显式传递或恢复用户上下文，不能依赖 `AuthInfoUtils` 直接获取！
