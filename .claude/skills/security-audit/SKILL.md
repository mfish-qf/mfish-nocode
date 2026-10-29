---
name: security-audit
description: 对 Spring 框架的 Java 项目执行安全漏洞与代码缺陷审计，完整覆盖 11 项检查：服务端执行完整SQL、密码明文存储、密码明文传输、日志泄密、异常信息泄露、IO资源未关闭、线程不合理使用、内存泄露、数据库连接泄露、不合适比较、越权访问。当用户说"安全审计"、"安全检查"、"漏洞扫描"、"代码审计"、"检查安全漏洞"、"安全自查"时使用此 skill。
---

# 安全审计 Skill

对 Spring 框架的 Java 项目执行安全漏洞与代码缺陷审计。**检查清单共 11 项，一项不可遗漏，全部检查完成后才允许输出报告。** 不修改业务代码，只做检测与整改建议。

## 检查清单总览（11 项）

| 编号 | 检查项 | 类型 | 风险等级 |
|------|--------|------|----------|
| CHECK-01 | 服务端执行由接口参数传递的完整SQL | 安全漏洞 | 高风险，严格禁止 |
| CHECK-02 | 密码明文存储（含代码中密码明文变量、@Value 默认值） | 安全漏洞 | 高风险，代码中严格禁止明文密码 |
| CHECK-03 | Rest 接口中密码类数据未加密传输 | 安全漏洞 | 高风险，严格禁止 |
| CHECK-04 | 日志中包含用户密码、中间件密码、第三方系统访问密码等敏感数据 | 安全漏洞 | 高风险，严格禁止 |
| CHECK-05 | 服务端向调用方返回数据库信息、异常堆栈、操作系统信息等 | 安全漏洞 | 高风险 |
| CHECK-06 | IO 对象未正确关闭导致资源泄露 | 代码缺陷 | 高风险，严格禁止 |
| CHECK-07 | 线程不合理使用（局部线程池未关闭为高风险） | 代码缺陷 | 低风险（局部线程池未关闭：高风险） |
| CHECK-08 | 内存泄露（静态集合持续添加不释放等） | 代码缺陷 | 高风险 |
| CHECK-09 | 数据库连接未释放 / 未池化 / 归还前未重置状态 | 代码缺陷 | 高风险 |
| CHECK-10 | 不合适的比较操作（字符串 ==、类型不一致 equals） | 代码缺陷 | 高风险 |
| CHECK-11 | 未根据用户信息对业务数据访问权限控制（越权） | 安全漏洞 | 高风险（国网通报最多问题，占比 80%+） |

## 扫描范围

**先探测项目结构：**
1. 确认构建体系：Maven（`pom.xml`）或 Gradle（`build.gradle` / `settings.gradle`）
2. 识别模块：单模块项目直接以源码目录为根；多模块项目列出所有含 `pom.xml` / `build.gradle` 的子模块
3. 识别源码根：`**/src/main/java` 与 `**/src/test/java`（main 与 test 都扫）

**扫描：**
- Java 源码：`**/src/**/*.java`
- 配置文件：`**/src/main/resources/**/application*.yml`、`**/bootstrap*.yml`、`**/*.properties`
- MyBatis Mapper XML：`**/*Mapper.xml`（及 `mybatis-config.xml`）

**排除目录：** `node_modules`、`target`、`build`、`dist`、`.git`、`.idea`、`logs`、`out`

所有扫描使用 Grep 工具（ripgrep 语法），范围限定被审计项目的根目录。每项检查必须实际执行 Grep，禁止凭印象跳过。

## 检索策略（两阶段漏斗：快速检索收窄 → 精读筛选）

**Grep 模式 = 快速正则检索**，作用是把全量代码快速收窄到少数可疑位置，而不是通读代码。审计永远遵循"先检索收窄、后精读定性"两阶段，代码量越大越要坚持此顺序，兼顾全面与效率。

**第一阶段 · 宽筛（快速检索，漏报优先）：**
1. 各检查项的"宽筛模式"按"宁可多报、不可漏报"设计，允许噪声；宽筛命中 ≠ 定案，只代表该处进入第二阶段。
2. 执行顺序：先用 `output_mode=files_with_matches` 只取命中文件清单（输出最少、速度最快）→ 命中文件 ≤ 20 时，再逐文件 `output_mode=content`（`-n` 取行号）→ 命中文件 > 20 时，按模块/子目录用 `glob` 参数分批重复执行同一模式（防止单次结果被截断漏看），必要时用该检查项的第二组模式进一步收窄。
3. 同一检查项的多组模式、多个互不依赖的检查项，Grep 调用尽量在同一条消息中并行发起。
4. 高风险严格禁止项（CHECK-01/02/03/04）除宽筛模式外，**必须再执行各项的"交叉模式"补扫一次**，两组模式互补防漏。
5. 宽筛（含交叉模式）全部无命中 → 该项记"通过（宽筛 0 命中）"；有任何命中 → 一律进入第二阶段，禁止在宽筛阶段凭变量名直觉直接判"误报放过"。

**第二阶段 · 精筛（Read 复核定案）：**
1. 只对宽筛收窄后的候选做 Read 上下文复看（前后 20~40 行），按各项"命中判定/误报排除"定性为：存在风险 / 人工复核 / 误报排除（写明排除理由）。
2. 无法确定的一律标"人工复核"，既不算通过也不算误报。
3. 精筛只读候选文件，不全量通读代码——这正是漏斗的意义；不得因文件多而跳过 Read 直接报风险或直接放过。

**漏报兜底：** 模式清单不可能穷举所有写法。审计中一旦发现目标项目存在模式未覆盖的等价写法（同义方法名、内部工具类封装、自定义 Mapper/Utils 基类转发等），必须即时补充 Grep 检索并纳入该检查项后再下结论。

---

## 逐项检测规则

### CHECK-01 服务端执行由接口参数传递的完整SQL（安全漏洞 · 高风险严格禁止）

**问题描述：** 服务端直接执行前端传来的完整 SQL（如 `executeSql(String sql, String limit)` 直接拼接执行），可导致 SQL 注入与任意数据访问。

**宽筛模式（Java，漏报优先）：**
```
executeSql|execSql|runSql|executeNativeSql
String\s+(sql|SQL)\s*[,)]
@Select\("\$\{|@Update\("\$\{|@Delete\("\$\{|@Insert\("\$\{
createStatement\(\)|\.execute(?:Query|Update)?\s*\(\s*\w*[sS]ql
jdbcTemplate\.(?:query|execute|update|queryForList)\s*\([^,)]*\+
```

**宽筛模式（Mapper XML）：**
```
\$\{
```
（Mapper XML 中 `${}` 为字符串拼接，非 `#{}` 预编译；记录每一处 `${}` 及其所在 XML 与语句 id）

**交叉模式（Java，补充扫描）：**
```
\.last\(\s*"?\s*(?:select|SELECT|update|UPDATE|delete|DELETE)
\.apply\(\s*"[^"]*(?:AND|OR|and|or)\s
@Param\(\s*"(?:sql|sqlText|execSql|sqlStr)"\s*\)
```
（MyBatis-Plus 的 `.last()`/`.apply()` 直接拼接 SQL 片段，同样是注入入口）

**命中判定：** Controller/Service 方法参数直接接收 SQL 语句字符串并交由 Mapper/JdbcTemplate/Statement 执行；或 Mapper XML/注解中使用 `${}` 拼接用户可控参数。

**误报排除：** `${}` 拼接的是服务端白名单常量（如排序字段枚举、内部生成的表名且经过校验）时标注"低风险-人工确认"，仍需列出。

**整改建议：** 绝对不允许服务端执行前端完整 SQL。应预定义 SQL，前端只传 SQL 标识及参数，服务端校验参数注入风险后执行。

---

### CHECK-02 密码明文存储（安全漏洞 · 高风险，代码中严格禁止明文密码）

**问题描述：** 密码等敏感数据直接明文存储，或在代码中设置密码明文变量值，或在 `@Value` 中定义为默认值。

**宽筛模式（Java，漏报优先）：**
```
private\s+String\s+(?:password|pwd|passwd|passWord)
(?:password|pwd|passwd)\s*=\s*"[^"]{3,}"
@Value\("[^"]*(?:password|pwd|passwd|secret|accessKey|secretKey)[^"]*"\)
```

**宽筛模式（yml/properties）：**
```
(?i)(password|pwd|passwd|secret|access-key|secret-key|accessKey|secretKey)\s*[:=]\s*\S+
```

**交叉模式（补充扫描）：**
```
(?i)(?:jdbc|redis|amqp|kafka|mongodb)\S{0,20}[:=]\s*"?[^\s"]*(?:password|pwd|auth)=[^&\s"]
(?i)(?:secret|app_?key|app_?secret|access_?key|private_?key|token)[^=]{0,20}=\s*"[^"]{8,}"
```
（连接 URL 中内嵌的明文密码参数；各类硬编码密钥/凭证）

**命中判定：**
1. 实体/PO 类中存在 password 类字段且入库前无哈希处理（检查保存用户的服务是否调用 `PasswordEncoder`/`BCrypt`/`Argon2`/加盐哈希，`Grep: BCrypt|PasswordEncoder|Argon2|SecureHash|salt`）
2. 代码中出现 `password = "明文"` 形式赋值
3. `@Value` 注解中含明文密码默认值
4. yml/properties 中存在明文密码（值为 `${...}` 占位符、`ENC(...)` 加密串则视为通过）

**误报排除：** 值为环境变量占位符 `${XXX}`、Jasypt `ENC()`、配置中心引用；仅作为空默认值 `password:`（空值）不算命中但需提示。

**整改建议：** 存储密码哈希值（bcrypt/Argon2 + 每用户唯一盐）；禁止在代码、配置中硬编码密码与应用密钥。

---

### CHECK-03 Rest 接口中密码类数据未加密传输（安全漏洞 · 高风险严格禁止）

**问题描述：** 登录、修改密码、重置密码等接口中密码数据以明文提交/返回。

**检测步骤：**
1. Grep 定位接口：`` @PostMapping[^\n]*\n[^\n]*(?:login|Login|password|Password|resetPwd|changePwd) `` 与 `Grep: (?:String|.*VO)\s+\w*(?:password|pwd|passwd)\w*` 限定 `**/controller/**/*.java`（或 `**/*Controller.java`）
2. 若项目包含前端工程（含 `package.json` 与 `src/`），Grep 加密痕迹：`encrypt|rsa|sm2|crypto` 限定登录相关源码文件；无前端工程则跳过此子步
3. 检查后端 Controller 收到的 password 是否为密文并在服务端解密（`decrypt|Decode|sm2|RsaUtils`），还是直接原文入库比对

**命中判定：** 接口直接接收/返回明文 password，且前后端均无应用层加密处理（仅依赖 HTTPS 不视为已加密）。

**整改建议：** 密码类数据在接口传输中必须加密（如 RSA/SM2 加密后传输，服务端解密后立即哈希比对），且接口响应中不得回显密码。

---

### CHECK-04 日志中包含密码等敏感数据（安全漏洞 · 高风险严格禁止）

**问题描述：** 日志输出用户密码、中间件密码、第三方系统访问密码等（如 `log.debug("验证失败: name:{} pwdDB:{} pwd{}", ...)`、`log.info("redis配置：...password-{}", password)`）。

**宽筛模式（Java，漏报优先）：**
```
(?i)log(?:ger)?\.(?:trace|debug|info|warn|error)\s*\([^;]*(?:password|pwd|passwd|secret|appkey|app_key|accesskey|access_key|token\s*\+)
(?i)System\.out\.print(?:ln)?\s*\([^;]*(?:password|pwd|passwd|secret)
```

**交叉模式（补充扫描）：**
```
(?i)System\.err\.print(?:ln)?\s*\([^;]*(?:password|pwd|passwd|secret)
(?i)(?:logger|log)\.(?:trace|debug|info|warn|error)\s*\([^;]{0,200}(?:credential|passphrase|privateKey|signKey)
```

**命中判定：** 任何日志语句的参数或拼接中出现密码/密钥类变量或字面量。

**误报排除：** 输出的是加密/脱敏后的值（如 `DigestUtils.md5` 结果、`***` 遮蔽函数）；变量名含 password 但实为非敏感语义（需人工确认后标注）。

**整改建议：** 禁止在日志中输出任何密码数据；确需调试时输出脱敏标识。

---

### CHECK-05 服务端向调用方返回数据库信息、异常堆栈、操作系统信息（安全漏洞 · 高风险）

**问题描述：** 接口异常时直接返回 `e.getMessage()`（含 SQL 报错、表名、字段名）、异常堆栈、服务器 OS 信息等（如 `WrappedResult.failedWrappedResult(e.getMessage())`）。

**宽筛模式（Java，漏报优先）：**
```
(?i)(?:failed|fail|error|result)\w*\s*\(\s*e\.getMessage\(\)\s*\)
return\s+.*e\.getMessage\(\)
(?:getMessage|toString)\(\)\s*;\s*return|return\s+.*\.printStackTrace|ExceptionUtils\.getStackTrace
catch\s*\([^)]*Exception[^)]*\)\s*\{[^}]*return[^}]*e\.
```

**检测步骤：** 重点审查全局异常处理器（`Grep: @RestControllerAdvice|@ControllerAdvice|GlobalException|ExceptionHandler`）与各 Controller 的 catch 块，确认返回给前端的异常内容是否经过统一编码/脱敏。

**命中判定：** 任何返回给调用方的响应体携带 `e.getMessage()`、堆栈、SQL 片段、表/字段名、OS/中间件版本信息。

**误报排除：** 仅写入日志不返回调用方；返回的是自定义业务错误码与通用描述。

**整改建议：** 服务端异常统一拦截、统一编码及描述，不返回异常详细信息；详细信息仅入服务端日志。

---

### CHECK-06 IO 对象未正确关闭导致资源泄露（代码缺陷 · 高风险严格禁止）

**问题描述：** 文件输入/输出流、网络连接、数据库连接等 IO 对象未正确关闭，导致文件句柄泄露、内存泄露。

**宽筛模式（Java，漏报优先）：**
```
new\s+(?:FileInputStream|FileOutputStream|FileReader|FileWriter|BufferedReader|BufferedWriter|InputStreamReader|OutputStreamWriter|ObjectInputStream|ObjectOutputStream|RandomAccessFile|ByteArrayOutputStream|ByteArrayInputStream|DataInputStream|DataOutputStream|PrintWriter|Scanner)\s*\(
```

**检测步骤：**
1. 对每个命中文件 Read 上下文，判断是否使用 try-with-resources：`try\s*\(\s*` 包裹资源声明
2. 若非 try-with-resources，检查是否在 `finally` 中关闭（`finally[\s\S]{0,400}\.close\(\)`）
3. 两者皆无 → 命中；仅在 catch/正常流程 close 而异常路径不关闭 → 命中

**误报排除：** try-with-resources 写法（推荐写法，通过）；由框架管理的流（如 `Files.lines` 配合 try-with-resources、Spring Resource 接口自动关闭）。

**整改建议：** 一律使用 try-with-resources 自动关闭实现 `AutoCloseable`/`Closeable` 的资源。

---

### CHECK-07 线程不合理使用（代码缺陷 · 低风险；局部线程池未关闭：高风险）

**问题描述：** 直接 `new Thread` 创建过多线程、共享资源无同步控制、未使用（全局）线程池、run 中异常处理不当（仅 printStackTrace 吞 InterruptedException）、线程泄漏。方法级局部线程池且未关闭为高风险。

**宽筛模式（Java，漏报优先）：**
```
new\s+Thread\s*\(
Executors\.new(?:CachedThreadPool|FixedThreadPool|SingleThreadExecutor|ScheduledThreadPool|WorkStealingPool|VirtualThreadPerTaskExecutor)
new\s+ThreadPoolExecutor\s*\(
new\s+ThreadPoolTaskExecutor\s*\(
ThreadUtil\.execAsync|CompletableFuture\.(?:runAsync|supplyAsync)
```

**检测步骤：** 对每个命中处 Read 上下文：
1. 判定是否为 **方法级局部线程池**（在线程池）——`new ThreadPoolExecutor/ThreadPoolTaskExecutor` 出现在方法体内而非 `@Bean` 配置类中
2. 局部线程池是否调用 `shutdown()/close()/destroy()`——未关闭 → **高风险命中**
3. `new Thread` 直接创建 → 记录为低风险命中
4. 检查 run/Callable 中是否捕获 `InterruptedException` 后未恢复中断（应有 `Thread.currentThread().interrupt()` 或重新抛出）
5. 多线程访问共享集合/变量无 `synchronized`/`Lock`/并发容器 → 记录

**误报排除：** `@Bean` 方法中定义的全局共享线程池（通过）；`@Async` + Spring 管理的 `ThreadPoolTaskExecutor` Bean（通过）。

**整改建议：** 使用可共享的全局线程池，按用途划分，禁止局部线程池；共享资源用 synchronized/juc 锁；正确处理异常；确保线程正确终止。

---

### CHECK-08 内存泄露（代码缺陷 · 高风险）

**问题描述：** 持续创建对象并加入静态集合/缓存而从不释放（如 `private static final ArrayList<Object> leakedList` 持续 add），导致应用崩溃或性能下降。

**宽筛模式（Java，漏报优先）：**
```
(?i)static\s+(?:final\s+)?(?:\w+<[^>]+>\s+)?\w*(?:list|map|set|cache|queue|pool)\w*\s*=\s*new\s+
private\s+static\s+(?:final\s+)?(?:List|Map|Set|Collection|ArrayList|HashMap|HashSet|LinkedList|ConcurrentHashMap)\b
static\s+Map<|static\s+List<|static\s+Set<
```

**检测步骤：**
1. 对每个静态集合命中处，Grep 同文件内对该变量的 `.add(`、`.put(`、`.offer(` 调用与 `.remove(`、`.clear()`、`evict`、过期策略调用
2. 只增不减（无 remove/clear/淘汰机制且容器随业务增长）→ 命中
3. 注意检查 ConcurrentHashMap 用作本地缓存是否有容量上限/过期策略（如 Caffeine/Guava Cache 则通过）

**误报排除：** 有界缓存（Caffeine、Guava Cache、LRUMap）；容量固定且由常量控制；有定时清理任务。

**整改建议：** 不再使用的对象及时移除引用；缓存使用带淘汰策略的组件；用 VisualVM/MAT 检测验证。

---

### CHECK-09 数据库连接未释放 / 未池化 / 归还前未重置状态（代码缺陷 · 高风险）

**问题描述：** 1) `DriverManager.getConnection` 直接建连（未池化）且未关闭；2) 从连接池 `dataSource.getConnection()` 后未 `close()` 归还；3) 归还前改变了连接状态（如 `setAutoCommit(false)`）未重置（未还原 `setAutoCommit(true)`）。

**宽筛模式（Java，漏报优先）：**
```
DriverManager\.getConnection
\.getConnection\s*\(\s*\)
setAutoCommit\s*\(\s*false\s*\)
```

**检测步骤：**
1. `DriverManager.getConnection` 命中 → Read 确认：是否 finally 中 `close()`；生产代码直接建连未池化本身即命中（未池化）
2. `dataSource.getConnection()` 命中 → Read 确认 try-with-resources 或 finally 中 close
3. `setAutoCommit(false)` 命中 → Read 确认归还（close）前是否恢复 `setAutoCommit(true)`
4. 手动事务操作是否 commit/rollback 全路径覆盖

**误报排除：** Spring `@Transactional` 声明式事务（由框架管理连接归还与状态重置，通过）；JdbcTemplate/MyBatis 常规使用（框架自动管理）。

**整改建议：** 使用连接池（HikariCP 等）；池化连接用后必须 close 归还；归还前重置被修改的状态属性；finally 中确保关闭。

---

### CHECK-10 不合适的比较操作（代码缺陷 · 高风险）

**问题描述：** 1) 字符串比较用 `==`/`!=` 导致逻辑错误；2) `Integer` 与 `Long` 等不同类型用 equals 永远 false；3) 不同类型变量直接比较（int 与 String）；4) 包装类型 `==` 比较在缓存区间外结果不稳定。

**宽筛模式（Java，漏报优先）：**
```
(?:String|\w*[sS]tr)\s*\w+\s*==\s*"|==\s*"[^"]+"\s*[)&|)\n]|!=\s*"[^"]+"
(?:Integer|Long|Short|Byte)\s+(?:==|!=)
\w+\.equals\s*\(\s*(?:null|"|\d+\s*\+)
```

**检测步骤：**
1. `== "`/`!= "` 字符串字面量比较 → 直接命中
2. 包装类型变量 `==`/`!=` 比较（非 int 拆箱场景）→ Read 确认变量类型后判定
3. `.equals()` 比较两侧类型明显不一致（`Integer.equals(Long)`）→ Grep `Long\w*\)\s*\w+\.equals|\.equals\s*\(\s*\w*[lL]ong` 并 Read 人工确认
4. `int` 与 `String` 比较属于编译错误，Grep 无法直接命中，检查 `String\.valueOf\s*\(\s*\w+\s*\)\s*==|\.equals\s*\(\s*\w+\s*\+\s*""` 拼接比较反模式

**误报排除：** 基本类型 `int`/`long`/`char` 的 `==` 比较（合法）；枚举比较（合法）；`==` 比较的是对象引用且注释明确意图（少见于业务代码，人工确认）。

**整改建议：** 字符串一律 `equals`/`Objects.equals`；包装类型比较用 `equals` 或先统一类型；跨类型比较先做类型转换（`Integer.parseInt` 等）并处理 `NumberFormatException`。

---

### CHECK-11 未根据用户信息对业务数据访问权限控制（安全漏洞 · 高风险，国网通报最多问题，占比 80%+）

**问题描述：** 用户可通过 ID/编号直接访问、修改、删除、导出他人（其他租户/部门）的业务数据，即水平越权（IDOR）。此类问题为国网通报信通科技最多的产品安全缺陷。

**检测步骤：**
1. **识别项目数据权限机制**（用于判断是否被正确使用）：
   - Spring Security：`Grep: @PreAuthorize|@PostAuthorize|hasRole|hasAuthority`
   - Apache Shiro：`Grep: @RequiresPermissions|@RequiresRoles`
   - Sa-Token：`Grep: @SaCheckPermission|@SaCheckRole|StpUtil`
   - MyBatis Plus 数据权限/多租户：`Grep: DataPermissionHandler|TenantLineHandler|DataScope|@TableLogic`
   - 自研切面：`Grep: \*Aspect\.java|@Around|@Before` 限定 `**/aspect/**/*.java`，确认数据权限切面逻辑
2. **列出单实体操作接口**：`Grep: @(?:Get|Put|Post|Delete)Mapping\([^)]*\{(?:id|code|key|sn|no)[^)]*\)` 限定 Controller 类，追踪其 Service 实现，确认查询条件中包含当前用户/租户/部门维度过滤（`Grep: SecurityUtils|getCurrentUser|getUserId|getTenantId|getLoginUser` 且条件拼入 SQL/Wrapper）
3. **检查列表/分页查询**是否套用数据权限注解或手动过滤；对未套用的接口列出待人工确认
4. **检查权限绕过场景**：`Grep: permitAll|anonymous|ignoreAuth|@Inner|excludePathPatterns` 结合安全配置（`WebSecurityConfigurerAdapter`/`SecurityFilterChain`/拦截器注册），确认放行路径是否过度放行内部接口

**命中判定：** 按主键访问业务数据且无归属校验 → 命中；列表查询无数据权限控制 → 命中（或标注人工复核，需业务确认是否为公共数据）。

**误报排除：** 公共字典、配置类数据（设计上全量可读，需业务确认）；平台管理员专用接口且已做角色强校验；已通过数据权限注解/切面完成归属过滤的接口。

**整改建议：** 所有业务数据访问必须根据当前用户信息（用户/租户/部门）过滤；优先在 Service/DAO 层强制注入归属条件，而非依赖前端传参；对单实体操作做数据归属校验（先查归属再操作）。

---

## 审计流程

1. **建立上下文**：确认被审计项目的根目录；探测构建体系（Maven/Gradle）与模块列表；识别持久层框架（MyBatis/MyBatis-Plus/JPA/JdbcTemplate）与安全框架（Spring Security/Shiro/Sa-Token/自研），后续检查项判定时使用。
2. **逐项扫描（两阶段漏斗）**：按 CHECK-01 → CHECK-11 顺序，每项先执行宽筛/交叉模式快速检索收窄候选文件，再按检测步骤对候选 Read 精筛定案（详见"检索策略"）。**使用 TodoWrite 为 11 项检查各建一个任务，完成一项勾选一项，确保无遗漏。**
3. **人工复核**：Grep 命中仅为线索，必须 Read 命中文件上下文后按"命中判定/误报排除"定性。无法确定时标注"人工复核"而不是放过。
4. **输出报告**：按下方格式输出，并将报告保存至被审计项目根目录 `security-audit-report-YYYYMMDD.md`（用户指定其他位置时以用户为准）。

## 报告输出格式

```markdown
# 安全审计报告
- 审计时间：YYYY-MM-DD HH:mm
- 审计对象：<项目名>（构建体系、模块清单）
- 扫描范围：全部模块 Java 源码 + 配置文件 + Mapper XML
- 检查项总数：11（全部覆盖：CHECK-01 ~ CHECK-11）

## 汇总
| 编号 | 检查项 | 风险等级 | 宽筛命中 | 结论 | 确认命中数 |
|------|--------|----------|----------|------|------------|
| CHECK-01 | 服务端执行完整SQL | 高风险严格禁止 | 文件数/行数 | 通过/存在风险/人工复核 | N |
| ...（11 行全部列出；宽筛 0 命中且通过时在宽筛命中列写 0） |

## 详细发现
### CHECK-XX 检查项名 [风险等级]
- 状态：存在风险 / 人工复核
- 位置：`文件路径:行号`
- 代码片段：（引用命中代码）
- 问题说明：……
- 整改建议：……
（每个命中一条，无命中的检查项在汇总表中标注"通过"即可）

## 统计与结论
- 高风险命中：X 处；建议人工复核：Y 处；通过项：Z/11
- 优先整改顺序建议
```

## 注意事项

- 11 项检查**必须全部执行并在报告汇总表逐行体现**，即使某项结论为"通过"。
- 宽筛阶段禁止省略：每项检查的所有模式（宽筛 + 交叉）都要实际执行并记录命中数，报告汇总表的"宽筛命中"列即来源于此，用于事后复核是否有遗漏。
- Grep 模式可能因代码写法差异漏检，检测步骤中的 Read 复核环节不可省略；对 CHECK-03、CHECK-11 这类依赖业务语义的项，以人工复核结论为准。
- skill 为通用规则，不依赖任何特定项目的模块名、类名或目录结构；审计前先探测目标项目结构再套用规则。
- 报告只做检测与整改建议，未经用户要求不直接修改业务代码。
