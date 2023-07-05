package cn.com.mfish.sys.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.dataset.datatable.MetaDataTable;
import cn.com.mfish.common.dataset.datatable.MetaHeaderDataTable;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.web.annotation.InnerUser;
import cn.com.mfish.sys.api.entity.DbConnect;
import cn.com.mfish.sys.api.entity.FieldInfo;
import cn.com.mfish.sys.api.entity.TableInfo;
import cn.com.mfish.sys.api.req.ReqDbConnect;
import cn.com.mfish.sys.entity.DBTreeNode;
import cn.com.mfish.sys.service.DbConnectService;
import cn.com.mfish.sys.service.TableService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @description: 数据库连接
 * @author: mfish
 * @date: 2023-03-13
 * @version: V1.1.0
 */
@Slf4j
@Api(tags = "数据库连接")
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
    @ApiOperation(value = "数据库连接-分页列表查询", notes = "数据库连接-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:database:query")
    public Result<PageResult<DbConnect>> queryPageList(ReqDbConnect reqDbConnect, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        List<DbConnect> list = dbConnectService.list(new LambdaQueryWrapper<DbConnect>()
                .like(reqDbConnect.getDbTitle() != null, DbConnect::getDbTitle, reqDbConnect.getDbTitle())
                .eq(reqDbConnect.getDbType() != null, DbConnect::getDbType, reqDbConnect.getDbType())
                .like(reqDbConnect.getDbName() != null, DbConnect::getDbName, reqDbConnect.getDbName())
                .like(reqDbConnect.getHost() != null, DbConnect::getHost, reqDbConnect.getHost())
                .orderByDesc(DbConnect::getCreateTime));
        //密码不返回查询界面
        for (DbConnect connect : list) {
            connect.setPassword(null);
        }
        return Result.ok(new PageResult<>(list), "数据库连接-查询成功!");
    }

    @ApiOperation("获取数据库表信息")
    @GetMapping("/tables")
    @RequiresPermissions("sys:database:query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "connectId", value = "数据库ID", paramType = "query", required = true),
            @ApiImplicitParam(name = "tableName", value = "表名", paramType = "query")
    })
    public Result<PageResult<TableInfo>> getTableList(@RequestParam(name = "connectId") String connectId, @RequestParam(name = "tableName", required = false) String tableName, ReqPage reqPage) {
        return Result.ok(new PageResult<>(tableService.getTableList(connectId, tableName, reqPage)), "获取表列表成功");
    }

    @ApiOperation("获取数据库树形结构信息-只能分层查询")
    @GetMapping("/tree")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "父节点ID 为空时查询数据库列表 有值时查询库下面的表列表", paramType = "query")
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

    @ApiOperation("获取表字段信息")
    @GetMapping("/fields")
    @RequiresPermissions("sys:database:query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "connectId", value = "数据库ID", paramType = "query", required = true),
            @ApiImplicitParam(name = "tableName", value = "表名", paramType = "query")
    })
    public Result<PageResult<FieldInfo>> getFieldList(@RequestParam(name = "connectId") String connectId, @RequestParam(name = "tableName", required = false) String tableName, ReqPage reqPage) {
        return Result.ok(new PageResult<>(tableService.getFieldList(connectId, tableName, reqPage)), "获取表列表成功");
    }

    @ApiOperation("获取表数据")
    @GetMapping("/data")
    @RequiresPermissions("sys:database:query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "connectId", value = "数据库ID", paramType = "query", required = true),
            @ApiImplicitParam(name = "tableName", value = "表名", paramType = "query")
    })
    public Result<MetaHeaderDataTable> getDataTable(@RequestParam(name = "connectId") String connectId, @RequestParam(name = "tableName", required = false) String tableName, ReqPage reqPage) {
        MetaDataTable table = tableService.getDataTable(connectId, tableName, reqPage);
        return Result.ok(new MetaHeaderDataTable(table), "获取表数据成功");
    }

    /**
     * 添加
     *
     * @param dbConnect 数据库连接对象
     * @return 返回数据库连接-添加结果
     */
    @Log(title = "数据库连接-添加", operateType = OperateType.INSERT)
    @ApiOperation("数据库连接-添加")
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
    @ApiOperation("数据库连接-编辑")
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
    @ApiOperation("数据库连接-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:database:delete")
    public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        if (dbConnectService.removeById(id)) {
            return Result.ok(true, "数据库连接-删除成功!");
        }
        return Result.fail(false, "错误:数据库连接-删除失败!");
    }

    /**
     * 批量删除
     *
     * @param ids 批量ID
     * @return 返回数据库连接-删除结果
     */
    @Log(title = "数据库连接-批量删除", operateType = OperateType.DELETE)
    @ApiOperation("数据库连接-批量删除")
    @DeleteMapping("/batch")
    @RequiresPermissions("sys:database:delete")
    public Result<Boolean> deleteBatch(@RequestParam(name = "ids") String ids) {
        if (this.dbConnectService.removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok(true, "数据库连接-批量删除成功!");
        }
        return Result.fail(false, "错误:数据库连接-批量删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回数据库连接对象
     */
    @ApiOperation("数据库连接-通过id查询(接口只允许内部访问)")
    @GetMapping("/{id}")
    @InnerUser
    public Result<DbConnect> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        return dbConnectService.queryById(id);
    }

    @Log(title = "测试数据库连接", operateType = OperateType.OTHER)
    @ApiOperation("测试数据库库连接")
    @PostMapping("/test")
    @RequiresPermissions("sys:database:query")
    public Result<Boolean> testConnect(@RequestBody DbConnect dbConnect) {
        return dbConnectService.testConnect(dbConnect);
    }
}
