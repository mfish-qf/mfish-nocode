package ${packageName}.controller;

import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import ${packageName}.entity.${entityName};
import ${packageName}.req.Req${entityName};
import ${packageName}.service.${entityName}Service;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

import java.io.IOException;

/**
 * @description: ${tableInfo.tableComment}
 * @author: mfish
 * @date: ${.now?string["yyyy-MM-dd"]}
 * @version: V2.2.0
 */
@Slf4j
@Tag(name = "${tableInfo.tableComment}")
@RestController
@RequestMapping("/${entityName?uncap_first}")
public class ${entityName}Controller {
    @Resource
    private ${entityName}Service ${entityName?uncap_first}Service;

    /**
     * 分页列表查询
     *
     * @param req${entityName} ${tableInfo.tableComment}请求参数
     * @param reqPage 分页参数
     * @return 返回${tableInfo.tableComment}-分页列表
     */
    @Operation(summary = "${tableInfo.tableComment}-分页列表查询", description = "${tableInfo.tableComment}-分页列表查询")
    @GetMapping
    @RequiresPermissions("${apiPrefix}:${entityName?uncap_first}:query")
    public Result<PageResult<${entityName}>> queryPageList(Req${entityName} req${entityName}, ReqPage reqPage) {
    	return ${entityName?uncap_first}Service.queryPageList(req${entityName}, reqPage);
    }

    /**
     * 添加
     *
     * @param ${entityName?uncap_first} ${tableInfo.tableComment}对象
     * @return 返回${tableInfo.tableComment}-添加结果
     */
    @Log(title = "${tableInfo.tableComment}-添加", operateType = OperateType.INSERT)
    @Operation(summary = "${tableInfo.tableComment}-添加")
    @PostMapping
    @RequiresPermissions("${apiPrefix}:${entityName?uncap_first}:insert")
    public Result<${entityName}> add(@RequestBody ${entityName} ${entityName?uncap_first}) {
    	return ${entityName?uncap_first}Service.add(${entityName?uncap_first});
    }

    /**
     * 编辑
     *
     * @param ${entityName?uncap_first} ${tableInfo.tableComment}对象
     * @return 返回${tableInfo.tableComment}-编辑结果
     */
    @Log(title = "${tableInfo.tableComment}-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "${tableInfo.tableComment}-编辑")
    @PutMapping
    @RequiresPermissions("${apiPrefix}:${entityName?uncap_first}:update")
    public Result<${entityName}> edit(@RequestBody ${entityName} ${entityName?uncap_first}) {
    	return ${entityName?uncap_first}Service.edit(${entityName?uncap_first});
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回${tableInfo.tableComment}-删除结果
     */
    @Log(title = "${tableInfo.tableComment}-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "${tableInfo.tableComment}-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("${apiPrefix}:${entityName?uncap_first}:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable ${tableInfo.idType} id) {
    	return ${entityName?uncap_first}Service.delete(id);
    }

    /**
     * 批量删除
     *
     * @param ids 批量ID
     * @return 返回${tableInfo.tableComment}-删除结果
     */
    @Log(title = "${tableInfo.tableComment}-批量删除", operateType = OperateType.DELETE)
    @Operation(summary = "${tableInfo.tableComment}-批量删除")
    @DeleteMapping("/batch/{ids}")
    @RequiresPermissions("${apiPrefix}:${entityName?uncap_first}:delete")
    public Result<Boolean> deleteBatch(@Parameter(name = "ids", description = "唯一性ID") @PathVariable String ids) {
    	return ${entityName?uncap_first}Service.deleteBatch(ids);
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回${tableInfo.tableComment}对象
     */
    @Operation(summary = "${tableInfo.tableComment}-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("${apiPrefix}:${entityName?uncap_first}:query")
    public Result<${entityName}> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable ${tableInfo.idType} id) {
    	return ${entityName?uncap_first}Service.queryById(id);
    }

    /**
     * 导出
	 *
     * @param req${entityName} ${tableInfo.tableComment}请求参数
     * @param reqPage 分页参数
     * @throws IOException IO异常
     */
    @Operation(summary = "导出${tableInfo.tableComment}", description = "导出${tableInfo.tableComment}")
    @GetMapping("/export")
    @RequiresPermissions("${apiPrefix}:${entityName?uncap_first}:export")
    public void export(Req${entityName} req${entityName}, ReqPage reqPage) throws IOException {
    	${entityName?uncap_first}Service.export(req${entityName}, reqPage);
    }
}