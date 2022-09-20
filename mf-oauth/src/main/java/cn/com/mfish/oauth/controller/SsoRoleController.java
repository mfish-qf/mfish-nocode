package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.entity.SsoRole;
import cn.com.mfish.oauth.req.ReqSsoRole;
import cn.com.mfish.oauth.service.SsoRoleService;
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
 * @Description: 角色信息表
 * @Author: mfish
 * @Date: 2022-09-20
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "角色信息表")
@RestController
@RequestMapping("/ssoRole")
public class SsoRoleController {
	@Resource
	private SsoRoleService ssoRoleService;

	/**
	 * 分页列表查询
	 *
	 * @param reqSsoRole
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@ApiOperation(value = "角色信息表-分页列表查询", notes = "角色信息表-分页列表查询")
	@GetMapping
	public Result<IPage<SsoRole>> queryPageList(ReqSsoRole reqSsoRole,
                                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		IPage<SsoRole> pageList = ssoRoleService.page(new Page<>(pageNo, pageSize));
		return Result.ok(pageList, "查询成功!");
	}

	/**
	 * 添加
	 *
	 * @param ssoRole
	 * @return
	 */
	@Log(title = "角色信息表-添加", operateType = OperateType.INSERT)
	@ApiOperation(value = "角色信息表-添加", notes = "角色信息表-添加")
	@PostMapping
	public Result<SsoRole> add(@RequestBody SsoRole ssoRole) {
		if (ssoRoleService.save(ssoRole)){
			return Result.ok(ssoRole, "添加成功!");
		}
        return Result.fail("错误:添加失败!");
	}

	/**
	 * 编辑
	 *
	 * @param ssoRole
	 * @return
	 */
	@Log(title = "角色信息表-编辑", operateType = OperateType.UPDATE)
	@ApiOperation(value = "角色信息表-编辑", notes = "角色信息表-编辑")
	@PutMapping
	public Result<?> edit(@RequestBody SsoRole ssoRole) {
		if (ssoRoleService.updateById(ssoRole)){
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
	@Log(title = "角色信息表-通过id删除", operateType = OperateType.DELETE)
	@ApiOperation(value = "角色信息表-通过id删除", notes = "角色信息表-通过id删除")
	@DeleteMapping("/{id}")
	public Result<?> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
		if (ssoRoleService.removeById(id)){
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
	@Log(title = "角色信息表-批量删除", operateType = OperateType.DELETE)
	@ApiOperation(value = "角色信息表-批量删除", notes = "角色信息表-批量删除")
	@DeleteMapping("/batch")
	public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
		if (this.ssoRoleService.removeByIds(Arrays.asList(ids.split(",")))){
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
	@ApiOperation(value = "角色信息表-通过id查询", notes = "角色信息表-通过id查询")
	@GetMapping("/{id}")
	public Result<SsoRole> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
		SsoRole ssoRole = ssoRoleService.getById(id);
		return Result.ok(ssoRole, "查询成功!");
	}
}
