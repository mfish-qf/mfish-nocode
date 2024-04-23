package cn.com.mfish.sys.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.dataset.datatable.MetaDataHeader;
import cn.com.mfish.common.dataset.datatable.MetaHeaderDataTable;
import cn.com.mfish.common.dblink.service.DbConnectService;
import cn.com.mfish.common.dblink.service.TableService;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.core.annotation.InnerUser;
import cn.com.mfish.sys.api.entity.DbConnect;
import cn.com.mfish.sys.api.entity.FieldInfo;
import cn.com.mfish.sys.api.entity.TableInfo;
import cn.com.mfish.sys.api.req.ReqDbConnect;
import cn.com.mfish.sys.entity.DBTreeNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 数据库连接
 * @author: mfish
 * @date: 2023-03-13
 * @version: V1.3.0
 */
@Slf4j
@Tag(name = "数据库连接")
@RestController
@RequestMapping("/dbConnect")
public class DbConnectController {
    @Resource
    private DbConnectService dbConnectService;
    @Resource
    private TableService tableService;

    /**
     * 分页列表查询
     *
     * @param reqDbConnect 数据库连接请求参数
     * @return 返回数据库连接-分页列表
     */
    @Operation(summary = "数据库连接-分页列表查询", description = "数据库连接-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:database:query")
    public Result<PageResult<DbConnect>> queryPageList(ReqDbConnect reqDbConnect, ReqPage reqPage) {
        return dbConnectService.queryPageList(reqDbConnect, reqPage);
    }

    @Operation(summary = "获取数据库表信息")
    @GetMapping("/tables")
    @RequiresPermissions("sys:database:query")
    @Parameters({
            @Parameter(name = "connectId", description = "数据库ID", required = true),
            @Parameter(name = "tableName", description = "表名")
    })
    public Result<PageResult<TableInfo>> getTableList(@RequestParam(name = "connectId") String connectId, @RequestParam(name = "tableName", required = false) String tableName, ReqPage reqPage) {
        return Result.ok(new PageResult<>(tableService.getTableList(connectId, tableName, reqPage)), "获取表列表成功");
    }

    @Operation(summary = "获取数据库树形结构信息-只能分层查询")
    @GetMapping("/tree")
    @Parameters({
            @Parameter(name = "parentId", description = "父节点ID 为空时查询数据库列表 有值时查询库下面的表列表")
    })
    public Result<List<DBTreeNode>> getDBTree(@RequestParam(name = "parentId", required = false) String parentId) {
        List<DBTreeNode> treeNodes = new ArrayList<>();
        if (StringUtils.isEmpty(parentId)) {
            List<DbConnect> list = dbConnectService.list();
            for (DbConnect connect : list) {
                treeNodes.add(new DBTreeNode().setCode(connect.getId()).setType(0).setLabel(connect.getDbTitle()));
            }
            return Result.ok(treeNodes, "查询数据库列表成功");
        }
        List<TableInfo> list = tableService.getTableList(parentId, null, null);
        for (TableInfo tableInfo : list) {
            String comment = StringUtils.isEmpty(tableInfo.getTableComment()) ? tableInfo.getTableName() : tableInfo.getTableName() + "[" + tableInfo.getTableComment() + "]";
            treeNodes.add(new DBTreeNode().setCode(tableInfo.getTableName()).setType(1).setLabel(comment));
        }
        return Result.ok(treeNodes, "查询标列表成功");
    }

    @Operation(summary = "获取表字段信息")
    @GetMapping("/fields")
    @RequiresPermissions("sys:database:query")
    @Parameters({
            @Parameter(name = "connectId", description = "数据库ID", required = true),
            @Parameter(name = "tableName", description = "表名")
    })
    public Result<PageResult<FieldInfo>> getFieldList(@RequestParam(name = "connectId") String connectId, @RequestParam(name = "tableName", required = false) String tableName, ReqPage reqPage) {
        return Result.ok(new PageResult<>(tableService.getFieldList(connectId, tableName, reqPage)), "获取字段列表成功");
    }

    @Operation(summary = "获取表数据")
    @GetMapping("/data")
    @Parameters({
            @Parameter(name = "connectId", description = "数据库ID", required = true),
            @Parameter(name = "tableName", description = "表名")
    })
    public Result<MetaHeaderDataTable> getDataTable(@RequestParam(name = "connectId") String connectId, @RequestParam(name = "tableName", required = false) String tableName, ReqPage reqPage) {
        return tableService.getHeaderDataTable(connectId, tableName, reqPage);
    }

    @Operation(summary = "获取表列头信息")
    @GetMapping("/headers")
    @Parameters({
            @Parameter(name = "connectId", description = "数据库ID", required = true),
            @Parameter(name = "tableName", description = "表名")
    })
    public Result<List<MetaDataHeader>> getDataHeaders(@RequestParam(name = "connectId") String connectId, @RequestParam(name = "tableName", required = false) String tableName, ReqPage reqPage) {
        List<MetaDataHeader> headers = tableService.getDataHeaders(connectId, tableName, reqPage);
        return Result.ok(headers, "获取表列头成功");
    }

    /**
     * 添加
     *
     * @param dbConnect 数据库连接对象
     * @return 返回数据库连接-添加结果
     */
    @Log(title = "数据库连接-添加", operateType = OperateType.INSERT)
    @Operation(summary = "数据库连接-添加")
    @PostMapping
    @RequiresPermissions("sys:database:insert")
    public Result<DbConnect> add(@RequestBody DbConnect dbConnect) {
        return dbConnectService.insertConnect(dbConnect);
    }

    /**
     * 编辑
     *
     * @param dbConnect 数据库连接对象
     * @return 返回数据库连接-编辑结果
     */
    @Log(title = "数据库连接-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "数据库连接-编辑")
    @PutMapping
    @RequiresPermissions("sys:database:update")
    public Result<DbConnect> edit(@RequestBody DbConnect dbConnect) {
        return dbConnectService.updateConnect(dbConnect);
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回数据库连接-删除结果
     */
    @Log(title = "数据库连接-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "数据库连接-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:database:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        if (dbConnectService.removeById(id)) {
            return Result.ok(true, "数据库连接-删除成功!");
        }
        return Result.fail(false, "错误:数据库连接-删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回数据库连接对象
     */
    @Operation(summary = "数据库连接-通过id查询(接口只允许内部访问)")
    @GetMapping("/{id}")
    @InnerUser
    public Result<DbConnect> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return dbConnectService.queryById(id);
    }

    @Log(title = "测试数据库连接", operateType = OperateType.OTHER)
    @Operation(summary = "测试数据库库连接")
    @PostMapping("/test")
    @RequiresPermissions("sys:database:query")
    public Result<Boolean> testConnect(@RequestBody DbConnect dbConnect) {
        return dbConnectService.testConnect(dbConnect);
    }
}
