---
name: crud-generator
description: 为 mfish-nocode-pro 项目生成标准增删改查（CRUD）代码，包括 Entity、Req、Mapper、Service、ServiceImpl、Controller 六层结构。当用户说"帮我生成增删改查"、"新增一个模块"、"生成CRUD代码"时使用此 skill。
---

# CRUD 代码生成器

## 项目架构概览

```
mf-api/mf-xxx-api/
  └── src/main/java/cn/com/mfish/xxx/api/entity/   # API 层实体（跨服务共享）

mf-business/mf-xxx/
  └── src/main/java/cn/com/mfish/xxx/
      ├── controller/   # Controller 层
      ├── entity/       # 业务实体
      ├── mapper/       # Mapper 接口
      ├── req/          # 请求参数类
      └── service/
          ├── XxxService.java
          └── impl/XxxServiceImpl.java
```

## 技术栈约定

- ORM: **MyBatis-Plus**（`BaseMapper<T>`、`ServiceImpl<M,T>`、`IService<T>`）
- 分页: **PageHelper**（`PageHelper.startPage(pageNum, pageSize)`）
- 权限: `@RequiresPermissions("模块:功能:操作")`（insert/update/delete/query/export）
- 日志: `@Log(title = "xxx-操作", operateType = OperateType.INSERT/UPDATE/DELETE)`
- 返回值: `Result<T>`、`Result<PageResult<T>>`、`Result<Boolean>`
- 文档: SpringDoc `@Tag`、`@Operation`、`@Parameter`
- ID类型: String UUID → `@TableId(type = IdType.ASSIGN_UUID)`；数值自增 → `@TableId(type = IdType.AUTO)`
- 基类: `BaseEntity<T>`（含 id、createBy、createTime、updateBy、updateTime），T 与 ID 类型一致
- 导出: `ExcelUtils.write(fileName, list)`（来自 `cn.com.mfish.common.core.utils.excel.ExcelUtils`）
- 字符串判空：`StringUtils.isEmpty()`（来自 `cn.com.mfish.common.core.utils.StringUtils`）
- **Swagger 注解**: 所有 Entity、Req 类必须添加 `@Schema` 注解（类级和字段级）

---

## 生成步骤

### 第一步：收集信息

询问用户（如未提供）：
1. **模块名**（如：order、product）
2. **中文名称**（如：订单、商品）
3. **数据表名**（如：sys_order）
4. **字段列表**：字段名、类型、中文描述、是否必填
5. **权限前缀**（如：`sys:order`）
6. **包路径**（如：`cn.com.mfish.sys`）
7. **数据权限**（可选）：是否需要按租户/用户/角色/组织过滤数据，表中需有对应字段

---

### 第二步：生成各层代码

按以下顺序生成，文件路径基于 `mf-business/mf-xxx/src/main/java/` 目录。

#### 1. Entity 实体类

```java
package {包路径}.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import cn.idev.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
// 有 Date 类型字段时引入：
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
// 有 BigDecimal 类型字段时引入：
import java.math.BigDecimal;

/**
 * @description: {中文名称}
 * @author: mfish
 * @date: {当前日期}
 * @version: V2.3.1
 */
@Data
@TableName("{表名}")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "{表名}对象 {中文名称}")
public class {类名} extends BaseEntity<String> {
    // String UUID 主键：
    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    @Accessors(chain = true)
    private String id;

    // 数值自增主键（替换上面的 id 声明）：
    // @TableId(type = IdType.AUTO)
    // @Accessors(chain = true)
    // private Integer id;

    // 普通字段：
    @ExcelProperty("{字段注释}")
    @Schema(description = "{字段注释}")
    private {类型} {字段名};

    // 日期字段（DATE 类型）：
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty("{字段注释}")
    @Schema(description = "{字段注释}")
    private Date {字段名};

    // 日期时间字段（DATETIME 类型）：
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("{字段注释}")
    @Schema(description = "{字段注释}")
    private Date {字段名};
}
```

#### 2. Req 请求参数类

```java
package {包路径}.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
// 有 Date 类型搜索字段时引入：
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

/**
 * @description: {中文名称}
 * @author: mfish
 * @date: {当前日期}
 * @version: V2.3.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "{中文名称}请求参数")
public class Req{类名} {
    // 普通搜索字段：
    @Schema(description = "{字段注释}")
    private {类型} {字段名};

    // DATE 类型搜索字段：
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "{字段注释}")
    private Date {字段名};

    // DATETIME 类型搜索字段：
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "{字段注释}")
    private Date {字段名};
}
```

#### 3. Mapper 接口 + XML

```java
package {包路径}.mapper;

import {包路径}.entity.{类名};
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @description: {中文名称}
 * @author: mfish
 * @date: {当前日期}
 * @version: V2.3.1
 */
public interface {类名}Mapper extends BaseMapper<{类名}> {
    // 如有复杂查询，声明自定义方法，并对应 XML
}
```

对应 XML 文件（`resources/mapper/{类名}Mapper.xml`）：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="{包路径}.mapper.{类名}Mapper">

</mapper>
```

#### 4. Service 接口

```java
package {包路径}.service;

import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import {包路径}.entity.{类名};
import {包路径}.req.Req{类名};
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;

/**
 * @description: {中文名称}
 * @author: mfish
 * @date: {当前日期}
 * @version: V2.3.1
 */
public interface {类名}Service extends IService<{类名}> {

    /** 分页列表查询 */
    Result<PageResult<{类名}>> queryPageList(Req{类名} req{类名}, ReqPage reqPage);

    /** 添加 */
    Result<{类名}> add({类名} {变量名});

    /** 编辑 */
    Result<{类名}> edit({类名} {变量名});

    /** 通过id删除 */
    Result<Boolean> delete(String id);

    /** 批量删除（ids 逗号分隔） */
    Result<Boolean> deleteBatch(String ids);

    /** 通过id查询 */
    Result<{类名}> queryById(String id);

    /** 导出 */
    void export(Req{类名} req{类名}, ReqPage reqPage) throws IOException;
}
```

> 若 ID 为数值型（如 `Integer`），将 `String id` 改为对应类型。

#### 5. ServiceImpl 实现类

```java
package {包路径}.service.impl;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.excel.ExcelUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import {包路径}.entity.{类名};
import {包路径}.mapper.{类名}Mapper;
import {包路径}.req.Req{类名};
import {包路径}.service.{类名}Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @description: {中文名称}
 * @author: mfish
 * @date: {当前日期}
 * @version: V2.3.1
 */
@Service
public class {类名}ServiceImpl extends ServiceImpl<{类名}Mapper, {类名}> implements {类名}Service {

    @Override
    public Result<PageResult<{类名}>> queryPageList(Req{类名} req{类名}, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(req{类名}, reqPage)), "{中文名称}-查询成功!");
    }

    private List<{类名}> queryList(Req{类名} req{类名}, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper<{类名}> lambdaQueryWrapper = new LambdaQueryWrapper<{类名}>()
            // String 类型字段用 StringUtils.isEmpty 判断：
            .like(!StringUtils.isEmpty(req{类名}.get{字段}()), {类名}::get{字段}, req{类名}.get{字段}())
            // 非 String 类型字段用 null != xxx 判断：
            .eq(null != req{类名}.get{字段}(), {类名}::get{字段}, req{类名}.get{字段}());
        return list(lambdaQueryWrapper);
    }

    @Override
    public Result<{类名}> add({类名} {变量名}) {
        if (save({变量名})) {
            return Result.ok({变量名}, "{中文名称}-添加成功!");
        }
        return Result.fail({变量名}, "错误:{中文名称}-添加失败!");
    }

    @Override
    public Result<{类名}> edit({类名} {变量名}) {
        if (updateById({变量名})) {
            return Result.ok({变量名}, "{中文名称}-编辑成功!");
        }
        return Result.fail({变量名}, "错误:{中文名称}-编辑失败!");
    }

    @Override
    public Result<Boolean> delete(String id) {
        if (removeById(id)) {
            return Result.ok(true, "{中文名称}-删除成功!");
        }
        return Result.fail(false, "错误:{中文名称}-删除失败!");
    }

    @Override
    public Result<Boolean> deleteBatch(String ids) {
        if (removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok(true, "{中文名称}-批量删除成功!");
        }
        return Result.fail(false, "错误:{中文名称}-批量删除失败!");
    }

    @Override
    public Result<{类名}> queryById(String id) {
        {类名} {变量名} = getById(id);
        return Result.ok({变量名}, "{中文名称}-查询成功!");
    }

    @Override
    public void export(Req{类名} req{类名}, ReqPage reqPage) throws IOException {
        // swagger 调用有问题，使用 postman 测试
        ExcelUtils.write("{中文名称}_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()), queryList(req{类名}, reqPage));
    }
}
```

**搜索条件 LambdaQueryWrapper 规则：**
- `String` 类型字段 → `.like(!StringUtils.isEmpty(req.getXxx()), Entity::getXxx, req.getXxx())`（模糊） 或 `.eq(!StringUtils.isEmpty(req.getXxx()), Entity::getXxx, req.getXxx())`（精确）
- 非 `String` 类型字段 → `.eq(null != req.getXxx(), Entity::getXxx, req.getXxx())`

#### 6. Controller 控制器

```java
package {包路径}.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import {包路径}.entity.{类名};
import {包路径}.req.Req{类名};
import {包路径}.service.{类名}Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @description: {中文名称}
 * @author: mfish
 * @date: {当前日期}
 * @version: V2.3.1
 */
@Slf4j
@Tag(name = "{中文名称}")
@RestController
@RequestMapping("/{变量名}")
public class {类名}Controller {

    @Resource
    private {类名}Service {变量名}Service;

    /**
     * 分页列表查询
     */
    @Operation(summary = "{中文名称}-分页列表查询", description = "{中文名称}-分页列表查询")
    @GetMapping
    @RequiresPermissions("{权限前缀}:query")
    // 需要数据权限时加：@DataScope(table = "{表名}", type = DataScopeType.Tenant)
    public Result<PageResult<{类名}>> queryPageList(Req{类名} req{类名}, ReqPage reqPage) {
        return {变量名}Service.queryPageList(req{类名}, reqPage);
    }

    /**
     * 添加
     */
    @Log(title = "{中文名称}-添加", operateType = OperateType.INSERT)
    @Operation(summary = "{中文名称}-添加")
    @PostMapping
    @RequiresPermissions("{权限前缀}:insert")
    public Result<{类名}> add(@RequestBody {类名} {变量名}) {
        return {变量名}Service.add({变量名});
    }

    /**
     * 编辑
     */
    @Log(title = "{中文名称}-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "{中文名称}-编辑")
    @PutMapping
    @RequiresPermissions("{权限前缀}:update")
    public Result<{类名}> edit(@RequestBody {类名} {变量名}) {
        return {变量名}Service.edit({变量名});
    }

    /**
     * 通过id删除
     */
    @Log(title = "{中文名称}-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "{中文名称}-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("{权限前缀}:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return {变量名}Service.delete(id);
    }

    /**
     * 批量删除
     */
    @Log(title = "{中文名称}-批量删除", operateType = OperateType.DELETE)
    @Operation(summary = "{中文名称}-批量删除")
    @DeleteMapping("/batch/{ids}")
    @RequiresPermissions("{权限前缀}:delete")
    public Result<Boolean> deleteBatch(@Parameter(name = "ids", description = "唯一性ID") @PathVariable String ids) {
        return {变量名}Service.deleteBatch(ids);
    }

    /**
     * 通过id查询
     */
    @Operation(summary = "{中文名称}-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("{权限前缀}:query")
    // 需要数据权限时加：@DataScope(table = "{表名}", type = DataScopeType.Tenant)
    public Result<{类名}> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return {变量名}Service.queryById(id);
    }

    /**
     * 导出
     */
    @Operation(summary = "导出{中文名称}", description = "导出{中文名称}")
    @GetMapping("/export")
    @RequiresPermissions("{权限前缀}:export")
    public void export(Req{类名} req{类名}, ReqPage reqPage) throws IOException {
        {变量名}Service.export(req{类名}, reqPage);
    }
}
```

> **Controller 核心原则**：Controller 只做路由转发，**所有业务逻辑在 Service 中实现**，Controller 方法体直接 `return xxxService.方法()`。

---

## 命名约定

| 占位符 | 说明 | 示例 |
|--------|------|------|
| `{类名}` | PascalCase 类名 | `SysOrder` |
| `{变量名}` | camelCase 变量名（也用作 @RequestMapping 路径） | `sysOrder` |
| `{表名}` | 数据库表名（下划线） | `sys_order` |
| `{权限前缀}` | 权限标识符（`{apiPrefix}:{变量名}`） | `sys:sysOrder` |
| `{包路径}` | Java 包名 | `cn.com.mfish.sys` |

---

## 注意事项

1. **业务逻辑分层**：Controller 只做路由转发，`return xxxService.方法()` 即可，业务实现全在 ServiceImpl
2. **有业务校验逻辑时**：在 ServiceImpl 中提取 `verifyXxx()` 私有方法，并在 add/edit 中调用
3. **有关联删除时**：在 ServiceImpl 中用 `@Transactional` 处理级联操作
4. **复杂查询**：在 Mapper 中声明方法，在 `resources/mapper/` 下创建对应 XML 文件
5. **模块位置**：根据功能归属放到对应的 `mf-business/mf-xxx` 子模块
6. **API 层实体**：如需跨服务调用，将实体放到 `mf-api/mf-xxx-api` 模块
7. **软删除场景**：实体增加 `delFlag` 字段，删除时 `updateById(new Xxx().setId(id).setDelFlag(1))`，查询时 `.eq(Xxx::getDelFlag, 0)`
8. **租户隔离场景**：实体增加 `tenantId` 字段，Controller 的写操作加 `@DataScope(table="表名", type=DataScopeType.Tenant)` 注解，Service 中用 `AuthInfoUtils.getCurrentTenantId()` 写入，修改时 `setTenantId(null)` 避免覆盖
9. **数值型主键**：`BaseEntity<Integer>`，`@TableId(type = IdType.AUTO)`，Service/Controller 中 `id` 参数类型相应改为 `Integer`
10. **Swagger 注解强制要求**：所有 Entity、Req 类必须添加 `@Schema` 注解，包括：
    - **类级别**：`@Schema(description = "描述信息", name = "类名")`
    - **字段级别**：每个字段都需添加 `@Schema(description = "字段描述")`
    - 便于生成完整的 API 文档和 Swagger UI 展示
11. **异常处理规范**：人为抛出的业务异常统一采用 `MyRuntimeException` 处理
    - **引入包**：`import cn.com.mfish.common.core.exception.MyRuntimeException;`
    - **使用场景**：业务校验失败、数据不存在、权限不足等业务异常情况
    - **示例代码**：
      ```java
      // 数据不存在校验
      if (entity == null) {
          throw new MyRuntimeException("错误：记录不存在!");
      }
      
      // 重复性校验
      if (baseMapper.exists(new LambdaQueryWrapper<Entity>()
              .eq(Entity::getField, value))) {
          throw new MyRuntimeException("错误:已存在，不能重复提交!");
      }
      
      // 状态校验
      if (!"active".equals(entity.getStatus())) {
          throw new MyRuntimeException("错误：记录状态不正确!");
      }
      ```
    - **消息格式**：建议以 `"错误:"` 开头，便于前端统一处理和识别
    - **不要使用**：避免直接使用 `RuntimeException` 或其他自定义异常
12. **安全规范 - 异常信息不暴露给前端**：
    - **核心原则**：返回给前端的错误消息必须是友好的、通用的提示，不能包含具体的异常堆栈或技术细节
    - **错误示例**：`return Result.fail(false, "错误：配置不正确，" + e.getMessage());` ❌
    - **正确示例**：`return Result.fail(false, "错误：配置不正确，请检查配置是否完整且符合规范");` ✅
    - **日志记录**：详细的异常信息应通过 `log.error()` 记录到日志文件中，便于开发人员排查
    - **实现模式**：
      ```java
      try {
          // 业务逻辑
          someOperation();
      } catch (Exception e) {
          // 详细异常信息记录到日志
          log.error("操作失败：{}", e.getMessage(), e);
          // 返回给前端的是友好的提示信息
          return Result.fail(false, "错误：操作失败，请检查配置是否正确");
      }
      ```
    - **适用范围**：所有 catch 块中返回给前端的错误消息都必须遵循此规范

---

## 数据权限控制

数据权限通过 `@DataScope` / `@DataScopes` 注解在 **Controller 查询方法** 上声明，框架自动在 SQL 中追加过滤条件。

> **重要约束**
> - 注解只能用于**查询方法**，不能用于新增/修改/删除
> - 注解加在 **Controller 层**，不在 Service 层
> - 表中须有对应的权限字段：租户 `tenant_id`、用户 `user_id`、角色 `role_id`、组织 `org_id`

### DataScopeType 权限类型

| 类型 | 说明 | 表中需要字段 |
|------|------|-----------|
| `DataScopeType.Tenant` | 按当前租户过滤 | `tenant_id` |
| `DataScopeType.User` | 按当前用户过滤 | `user_id` |
| `DataScopeType.Role` | 按当前角色过滤 | `role_id` |
| `DataScopeType.Org` | 按当前组织及下级过滤 | `org_id` |
| `DataScopeType.None` | 不过滤（默认） | — |

### 常用场景示例

**1. 单表租户过滤（最常用）**
```java
@GetMapping
@RequiresPermissions("{权限前缀}:query")
@DataScope(table = "{表名}", type = DataScopeType.Tenant)
public Result<PageResult<{类名}>> queryPageList(Req{类名} req, ReqPage reqPage) { ... }
```

**2. 单表组织过滤**
```java
@DataScope(table = "{表名}", type = DataScopeType.Org)
```

**3. 固定角色值过滤（指定具体角色编码）**
```java
@DataScope(table = "{表名}", type = DataScopeType.Role, values = {"manage", "superAdmin"})
```

**4. 排除公开数据（满足排除条件的记录不被过滤，始终可查）**
```java
@DataScope(table = "{表名}", type = DataScopeType.Tenant, excludes = "is_public=1")
```

**5. 忽略条件（优先级最高，满足时其他权限条件全部失效）**
```java
// 变量值从 ServletRequest.getParameter 中取，为空时不使用忽略条件
@DataScope(table = "{表名}", ignores = "share_token=#{_shareToken} and share_end_time>=now()")
```

**6. 多表组合权限（使用 @DataScopes）**
```java
import cn.com.mfish.common.oauth.annotation.DataScopes;

@DataScopes({
    @DataScope(table = "{主表名}", type = DataScopeType.Tenant),
    @DataScope(table = "{关联表名}", type = DataScopeType.Tenant, excludes = "is_public=1")
})
```

**7. 租户 + 角色组合过滤**
```java
@DataScopes({
    @DataScope(table = "{表名}", type = DataScopeType.Tenant),
    @DataScope(table = "{表名}", type = DataScopeType.Role, values = {"manage", "superAdmin"})
})
```

### 所需 Import

```java
import cn.com.mfish.common.oauth.annotation.DataScope;
import cn.com.mfish.common.oauth.annotation.DataScopes; // 多条件时引入
import cn.com.mfish.common.oauth.common.DataScopeType;
```

### 参考实现

- 完整样例：[DemoDataScopeController.java](mf-business/mf-demo/src/main/java/cn/com/mfish/demo/controller/DemoDataScopeController.java)
- 排除条件示例：[DbConnectController.java](mf-business/mf-sys/src/main/java/cn/com/mfish/sys/controller/DbConnectController.java)
- 忽略条件示例：[MfApiController.java](mf-business/mf-nocode/src/main/java/cn/com/mfish/nocode/controller/MfApiController.java)

---

## 相关参考

- 参考现有实现：[DictController.java](mf-business/mf-sys/src/main/java/cn/com/mfish/sys/controller/DictController.java)
- 基础实体类：[BaseEntity.java](mf-common/mf-common-core/src/main/java/cn/com/mfish/common/core/entity/BaseEntity.java)
- 返回值规范：[Result.java](mf-common/mf-common-core/src/main/java/cn/com/mfish/common/core/web/Result.java)
- 代码生成模板：[mf-common-code/template/src/main/java/](mf-common/mf-common-code/src/main/resources/template/src/main/java/)
- 工作流审批集成请使用 **workflow-audit** skill
- 前端页面生成请使用 **frontend-crud** skill
