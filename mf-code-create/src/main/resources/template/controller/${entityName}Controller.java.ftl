package ${packageName}.controller;

import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import ${packageName}.entity.${entityName};
import ${packageName}.req.Req${entityName};
import ${packageName}.service.${entityName}Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @Description: ${tableInfo.tableComment}
 * @Author: mfish
 * @Date: ${.now?string["yyyy-MM-dd"]}
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "${tableInfo.tableComment}")
@RestController
@RequestMapping("/${entityName?uncap_first}")
public class ${entityName}Controller {
	@Resource
	private ${entityName}Service ${entityName?uncap_first}Service;

	/**
	 * 分页列表查询
	 *
	 * @param req${entityName}
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@ApiOperation(value = "${tableInfo.tableComment}-分页列表查询", notes = "${tableInfo.tableComment}-分页列表查询")
	@GetMapping
	public Result<IPage<${entityName}>> queryPageList(Req${entityName} req${entityName},
                                                           @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		IPage<${entityName}> pageList = ${entityName?uncap_first}Service.page(new Page<>(pageNum, pageSize));
		return Result.ok(pageList, "${tableInfo.tableComment}-查询成功!");
	}

	/**
	 * 添加
	 *
	 * @param ${entityName?uncap_first}
	 * @return
	 */
	@Log(title = "${tableInfo.tableComment}-添加", operateType = OperateType.INSERT)
	@ApiOperation(value = "${tableInfo.tableComment}-添加", notes = "${tableInfo.tableComment}-添加")
	@PostMapping
	public Result<${entityName}> add(@RequestBody ${entityName} ${entityName?uncap_first}) {
		if (${entityName?uncap_first}Service.save(${entityName?uncap_first})){
			return Result.ok(${entityName?uncap_first}, "${tableInfo.tableComment}-添加成功!");
		}
        return Result.fail("错误:${tableInfo.tableComment}-添加失败!");
	}

	/**
	 * 编辑
	 *
	 * @param ${entityName?uncap_first}
	 * @return
	 */
	@Log(title = "${tableInfo.tableComment}-编辑", operateType = OperateType.UPDATE)
	@ApiOperation(value = "${tableInfo.tableComment}-编辑", notes = "${tableInfo.tableComment}-编辑")
	@PutMapping
	public Result<${entityName}> edit(@RequestBody ${entityName} ${entityName?uncap_first}) {
		if (${entityName?uncap_first}Service.updateById(${entityName?uncap_first})){
		    return Result.ok(${entityName?uncap_first}, "${tableInfo.tableComment}-编辑成功!");
		}
		return Result.fail("错误:${tableInfo.tableComment}-编辑失败!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@Log(title = "${tableInfo.tableComment}-通过id删除", operateType = OperateType.DELETE)
	@ApiOperation(value = "${tableInfo.tableComment}-通过id删除", notes = "${tableInfo.tableComment}-通过id删除")
	@DeleteMapping("/{id}")
	public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
		if (${entityName?uncap_first}Service.removeById(id)){
			return Result.ok(true, "${tableInfo.tableComment}-删除成功!");
		}
		return Result.fail(false, "错误:${tableInfo.tableComment}-删除失败!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@Log(title = "${tableInfo.tableComment}-批量删除", operateType = OperateType.DELETE)
	@ApiOperation(value = "${tableInfo.tableComment}-批量删除", notes = "${tableInfo.tableComment}-批量删除")
	@DeleteMapping("/batch")
	public Result<Boolean> deleteBatch(@RequestParam(name = "ids") String ids) {
		if (this.${entityName?uncap_first}Service.removeByIds(Arrays.asList(ids.split(",")))){
		    return Result.ok(true, "${tableInfo.tableComment}-批量删除成功!");
		}
		return Result.fail(false, "错误:${tableInfo.tableComment}-批量删除失败!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "${tableInfo.tableComment}-通过id查询", notes = "${tableInfo.tableComment}-通过id查询")
	@GetMapping("/{id}")
	public Result<${entityName}> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
		${entityName} ${entityName?uncap_first} = ${entityName?uncap_first}Service.getById(id);
		return Result.ok(${entityName?uncap_first}, "${tableInfo.tableComment}-查询成功!");
	}
}
