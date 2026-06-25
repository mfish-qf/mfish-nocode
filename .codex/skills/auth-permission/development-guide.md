# 开发规范

## 1. 获取用户信息

### 基本用法

```java
import cn.com.mfish.common.core.utils.AuthInfoUtils;

@Service
public class MyService {
    
    public void doSomething() {
        // 获取当前用户 ID
        String userId = AuthInfoUtils.getCurrentUserId();
        
        // 获取当前租户 ID
        String tenantId = AuthInfoUtils.getCurrentTenantId();
        
        // 获取当前账号
        String account = AuthInfoUtils.getCurrentAccount();
        
        // 业务逻辑...
    }
}
```

### 注意事项

1. **空值处理**: 用户信息可能为空，需要做判空处理
2. **异步场景**: AuthInfoUtils 不支持异步调用（详见 [异步场景处理](./async-context.md)）
3. **内部调用**: 内部接口可能没有用户上下文

## 2. 内部接口开发

### 使用 @InnerUser 注解

```java
import cn.com.mfish.common.core.annotation.InnerUser;

@RestController
@RequestMapping("/internal")
public class InternalController {
    
    /**
     * 系统级内部调用
     * 跳过用户校验，仅依赖服务间认证
     */
    @InnerUser(validateUser = false)
    @PostMapping("/system-sync")
    public Result systemSync() {
        // 系统维护逻辑，不需要用户上下文
        return Result.ok();
    }
    
    /**
     * 代用户内部调用
     * 保留用户上下文，执行权限校验
     */
    @InnerUser(validateUser = true)
    @PostMapping("/user-operation")
    public Result userOperation() {
        // 需要用户上下文的逻辑
        String userId = AuthInfoUtils.getCurrentUserId();
        return Result.ok();
    }
}
```

### 内部调用类型区分

| 类型 | validateUser | 用户上下文 | 权限校验 | 使用场景 |
|------|--------------|------------|----------|----------|
| 系统级内部调用 | false | 不需要 | 跳过 | 数据同步、系统维护 |
| 代用户内部调用 | true | 需要 | 执行 | 跨服务业务操作 |
| 管理型内部接口 | false | 可选 | 服务权限 | 管理后台、运维接口 |

### 调用内部接口

```java
// 方式 1: 通过 Feign 调用（自动透传 Header）
@FeignClient(name = "mf-sys")
public interface RemoteSysService {
    @PostMapping("/internal/user-operation")
    Result userOperation();
}

// 方式 2: 手动设置 Header（不推荐）
RestTemplate restTemplate = new RestTemplate();
HttpHeaders headers = new HttpHeaders();
headers.set("req-origin", "inner");
headers.set("req-user-id", userId);
// ...
```

## 3. 数据权限控制

### 使用 DataScopeAspect

数据权限切面会自动处理数据范围过滤：

```java
@Service
public class MyService {
    
    /**
     * 数据权限会自动根据用户角色、部门等过滤数据
     */
    public List<Data> queryData() {
        // Mapper 中的 SQL 会被 DataScopeAspect 拦截并追加权限条件
        return dataMapper.selectList();
    }
}
```

### 内部调用与数据权限

```java
/**
 * 系统级内部调用：跳过数据权限
 * 适用于：系统数据同步、定时任务等
 */
@InnerUser(validateUser = false)
public void systemSync() {
    // 不受数据权限限制，可以访问全量数据
    List<Data> allData = dataMapper.selectList();
}

/**
 * 代用户内部调用：执行数据权限
 * 适用于：跨服务业务操作
 */
@InnerUser(validateUser = true)
public void userOperation() {
    // 受数据权限限制，只能访问用户有权限的数据
    List<Data> userData = dataMapper.selectList();
}
```

## 4. Controller 开发规范

### 外部接口（需要认证）

```java
@RestController
@RequestMapping("/api/data")
public class DataController {
    
    /**
     * 外部接口：需要用户登录
     * 网关会自动校验 token 并注入用户上下文
     */
    @GetMapping("/list")
    public Result list() {
        String userId = AuthInfoUtils.getCurrentUserId();
        String tenantId = AuthInfoUtils.getCurrentTenantId();
        
        // 业务逻辑...
        return Result.ok();
    }
}
```

### 内部接口（服务间调用）

```java
@RestController
@RequestMapping("/internal/data")
public class InternalDataController {
    
    /**
     * 内部接口：服务间调用
     * 需要 req-origin: inner Header
     */
    @InnerUser(validateUser = true)
    @PostMapping("/sync")
    public Result sync() {
        String userId = AuthInfoUtils.getCurrentUserId();
        
        // 业务逻辑...
        return Result.ok();
    }
}
```

### 匿名接口（无需认证）

```java
@RestController
@RequestMapping("/public")
public class PublicController {
    
    /**
     * 公开接口：不需要认证
     * 例如：登录、注册、验证码等
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginReq req) {
        // 业务逻辑...
        return Result.ok();
    }
}
```

## 5. Service 层开发规范

### 记录审计字段

```java
@Service
public class MyService {
    
    public void create(Entity entity) {
        // 自动填充创建人信息
        entity.setCreateBy(AuthInfoUtils.getCurrentUserId());
        entity.setCreateTime(new Date());
        
        // 自动填充租户 ID
        entity.setTenantId(AuthInfoUtils.getCurrentTenantId());
        
        mapper.insert(entity);
    }
    
    public void update(Entity entity) {
        // 自动填充更新人信息
        entity.setUpdateBy(AuthInfoUtils.getCurrentUserId());
        entity.setUpdateTime(new Date());
        
        mapper.updateById(entity);
    }
}
```

### 租户隔离

```java
@Service
public class MyService {
    
    public List<Entity> queryList() {
        String tenantId = AuthInfoUtils.getCurrentTenantId();
        
        // 确保只查询当前租户的数据
        return mapper.selectList(
            new QueryWrapper<Entity>().eq("tenant_id", tenantId)
        );
    }
}
```

## 6. Feign 接口开发规范

### 定义 Feign 接口

```java
@FeignClient(name = "mf-sys", path = "/api")
public interface RemoteSysService {
    
    /**
     * 调用外部服务接口
     * BearerTokenInterceptor 会自动透传用户上下文 Header
     */
    @GetMapping("/user/info")
    Result<UserInfo> getUserInfo(@RequestParam("userId") String userId);
    
    /**
     * 调用内部服务接口
     * 需要确保 req-origin Header 也被透传
     */
    @PostMapping("/internal/sync")
    Result sync();
}
```

### 使用 Feign 接口

```java
@Service
public class MyService {
    
    @Autowired
    private RemoteSysService remoteSysService;
    
    public void callRemoteService() {
        // 当前用户上下文会自动通过 BearerTokenInterceptor 透传
        Result<UserInfo> result = remoteSysService.getUserInfo("123");
        
        // 下游服务可以通过 AuthInfoUtils 获取到当前用户信息
    }
}
```

## 7. 单元测试

### Mock 用户上下文

```java
@SpringBootTest
class MyServiceTest {
    
    @Test
    void testWithUserContext() {
        // Mock 用户上下文
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("req-user-id", "test-user");
        request.addHeader("req-tenant-id", "test-tenant");
        request.addHeader("req-account", "test-account");
        
        RequestContextHolder.setRequestAttributes(
            new ServletRequestAttributes(request)
        );
        
        try {
            // 执行业务逻辑
            myService.doSomething();
            
            // 验证结果...
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }
}
```

## 最佳实践

1. **统一使用 AuthInfoUtils**: 不要直接从 Header 或 Attribute 中读取用户信息
2. **合理选择内部调用类型**: 根据业务需求选择 validateUser 的值
3. **注意租户隔离**: 所有数据查询都应该考虑租户隔离
4. **审计字段自动填充**: 在 Service 层统一处理创建人、更新人等字段
5. **异常处理**: 用户信息为空时的降级策略
