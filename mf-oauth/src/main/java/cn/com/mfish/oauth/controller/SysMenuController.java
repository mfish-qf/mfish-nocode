package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.entity.SysMenu;
import cn.com.mfish.oauth.req.ReqSysMenu;
import cn.com.mfish.oauth.service.SysMenuService;
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
 * @Description: 菜单权限表
 * @Author: mfish
 * @Date: 2022-09-20
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "菜单权限表")
@RestController
@RequestMapping("/sysMenu")
public class SysMenuController {
	@Resource
	private SysMenuService sysMenuService;

	/**
	 * 分页列表查询
	 *
	 * @param reqSysMenu
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@ApiOperation(value = "菜单权限表-分页列表查询", notes = "菜单权限表-分页列表查询")
	@GetMapping
	public Result<IPage<SysMenu>> queryPageList(ReqSysMenu reqSysMenu,
                                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		IPage<SysMenu> pageList = sysMenuService.page(new Page<>(pageNo, pageSize));
		return Result.ok(pageList, "查询成功!");
	}

	/**
	 * 添加
	 *
	 * @param sysMenu
	 * @return
	 */
	@Log(title = "菜单权限表-添加", operateType = OperateType.INSERT)
	@ApiOperation(value = "菜单权限表-添加", notes = "菜单权限表-添加")
	@PostMapping
	public Result<SysMenu> add(@RequestBody SysMenu sysMenu) {
		if (sysMenuService.save(sysMenu)){
			return Result.ok(sysMenu, "添加成功!");
		}
        return Result.fail("错误:添加失败!");
	}

	/**
	 * 编辑
	 *
	 * @param sysMenu
	 * @return
	 */
	@Log(title = "菜单权限表-编辑", operateType = OperateType.UPDATE)
	@ApiOperation(value = "菜单权限表-编辑", notes = "菜单权限表-编辑")
	@PutMapping
	public Result<?> edit(@RequestBody SysMenu sysMenu) {
		if (sysMenuService.updateById(sysMenu)){
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
	@Log(title = "菜单权限表-通过id删除", operateType = OperateType.DELETE)
	@ApiOperation(value = "菜单权限表-通过id删除", notes = "菜单权限表-通过id删除")
	@DeleteMapping("/{id}")
	public Result<?> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
		if (sysMenuService.removeById(id)){
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
	@Log(title = "菜单权限表-批量删除", operateType = OperateType.DELETE)
	@ApiOperation(value = "菜单权限表-批量删除", notes = "菜单权限表-批量删除")
	@DeleteMapping("/batch")
	public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
		if (this.sysMenuService.removeByIds(Arrays.asList(ids.split(",")))){
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
	@ApiOperation(value = "菜单权限表-通过id查询", notes = "菜单权限表-通过id查询")
	@GetMapping("/{id}")
	public Result<SysMenu> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
		SysMenu sysMenu = sysMenuService.getById(id);
		return Result.ok(sysMenu, "查询成功!");
	}
}
