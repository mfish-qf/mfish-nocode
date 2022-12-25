package cn.com.mfish.sys.controller;

import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.entity.Dict;
import cn.com.mfish.sys.req.ReqDict;
import cn.com.mfish.sys.service.DictService;
import cn.com.mfish.common.web.page.PageResult;
import cn.com.mfish.common.web.page.ReqPage;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @Description: 字典类型
 * @Author: mfish
 * @Date: 2022-12-25
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "字典类型")
@RestController
@RequestMapping("/dict")
public class DictController {
	@Resource
	private DictService dictService;

	/**
	 * 分页列表查询
	 *
	 * @param reqDict
	 * @return
	 */
	@ApiOperation(value = "字典类型-分页列表查询", notes = "字典类型-分页列表查询")
	@GetMapping
	public Result<PageResult<Dict>> queryPageList(ReqDict reqDict, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
	    return Result.ok(new PageResult<>(dictService.list()), "字典类型-查询成功!");
	}

	/**
	 * 添加
	 *
	 * @param dict
	 * @return
	 */
	@Log(title = "字典类型-添加", operateType = OperateType.INSERT)
	@ApiOperation(value = "字典类型-添加", notes = "字典类型-添加")
	@PostMapping
	public Result<Dict> add(@RequestBody Dict dict) {
		if (dictService.save(dict)) {
			return Result.ok(dict, "字典类型-添加成功!");
		}
        return Result.fail("错误:字典类型-添加失败!");
	}

	/**
	 * 编辑
	 *
	 * @param dict
	 * @return
	 */
	@Log(title = "字典类型-编辑", operateType = OperateType.UPDATE)
	@ApiOperation(value = "字典类型-编辑", notes = "字典类型-编辑")
	@PutMapping
	public Result<Dict> edit(@RequestBody Dict dict) {
		if (dictService.updateById(dict)) {
		    return Result.ok(dict, "字典类型-编辑成功!");
		}
		return Result.fail("错误:字典类型-编辑失败!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@Log(title = "字典类型-通过id删除", operateType = OperateType.DELETE)
	@ApiOperation(value = "字典类型-通过id删除", notes = "字典类型-通过id删除")
	@DeleteMapping("/{id}")
	public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
		if (dictService.removeById(id)) {
			return Result.ok(true, "字典类型-删除成功!");
		}
		return Result.fail(false, "错误:字典类型-删除失败!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@Log(title = "字典类型-批量删除", operateType = OperateType.DELETE)
	@ApiOperation(value = "字典类型-批量删除", notes = "字典类型-批量删除")
	@DeleteMapping("/batch")
	public Result<Boolean> deleteBatch(@RequestParam(name = "ids") String ids) {
		if (this.dictService.removeByIds(Arrays.asList(ids.split(",")))) {
		    return Result.ok(true, "字典类型-批量删除成功!");
		}
		return Result.fail(false, "错误:字典类型-批量删除失败!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "字典类型-通过id查询", notes = "字典类型-通过id查询")
	@GetMapping("/{id}")
	public Result<Dict> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
		Dict dict = dictService.getById(id);
		return Result.ok(dict, "字典类型-查询成功!");
	}
}
