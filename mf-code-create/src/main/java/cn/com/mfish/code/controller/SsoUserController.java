package cn.com.mfish.code.controller;

import cn.com.mfish.common.core.annotation.Log;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.code.entity.SsoUser;
import cn.com.mfish.code.req.ReqSsoUser;
import cn.com.mfish.code.service.SsoUserService;
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
 * @Description: 用户信息
 * @Author: mfish
 * @Date: 2022-09-01
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "用户信息")
@RestController
@RequestMapping("/ssoUser")
public class SsoUserController {
	@Resource
	private SsoUserService ssoUserService;

	/**
	 * 分页列表查询
	 *
	 * @param reqSsoUser
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@ApiOperation(value = "用户信息-分页列表查询", notes = "用户信息-分页列表查询")
	@GetMapping
	public Result<IPage<SsoUser>> queryPageList(ReqSsoUser reqSsoUser,
                                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		IPage<SsoUser> pageList = ssoUserService.page(new Page<>(pageNo, pageSize));
		return Result.ok(pageList, "查询成功!");
	}

	/**
	 * 添加
	 *
	 * @param ssoUser
	 * @return
	 */
	@ApiOperation(value = "用户信息-添加", notes = "用户信息-添加")
	@PostMapping
	public Result<SsoUser> add(@RequestBody SsoUser ssoUser) {
		if (ssoUserService.save(ssoUser)){
			return Result.ok(ssoUser, "添加成功!");
		}
        return Result.fail("错误:添加失败!");
	}

	/**
	 * 编辑
	 *
	 * @param ssoUser
	 * @return
	 */
	@Log(title = "用户信息-编辑", operateType = OperateType.UPDATE)
	@ApiOperation(value = "用户信息-编辑", notes = "用户信息-编辑")
	@PutMapping
	public Result<?> edit(@RequestBody SsoUser ssoUser) {
		if (ssoUserService.updateById(ssoUser)){
		    return Result.ok("编辑成功!");
		}
		return Result.fail("错误:编辑失败!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@Log(title = "用户信息-通过id删除", operateType = OperateType.DELETE)
	@ApiOperation(value = "用户信息-通过id删除", notes = "用户信息-通过id删除")
	@DeleteMapping("/{id}")
	public Result<?> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
		if (ssoUserService.removeById(id)){
			return Result.ok("删除成功!");
		}
		return Result.fail("错误:删除失败!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@Log(title = "用户信息-批量删除", operateType = OperateType.DELETE)
	@ApiOperation(value = "用户信息-批量删除", notes = "用户信息-批量删除")
	@DeleteMapping("/batch")
	public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
		if (this.ssoUserService.removeByIds(Arrays.asList(ids.split(",")))){
		    return Result.ok("批量删除成功!");
		}
		return Result.fail("错误:批量删除失败!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "用户信息-通过id查询", notes = "用户信息-通过id查询")
	@GetMapping("/{id}")
	public Result<SsoUser> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
		SsoUser ssoUser = ssoUserService.getById(id);
		return Result.ok(ssoUser, "查询成功!");
	}
}
