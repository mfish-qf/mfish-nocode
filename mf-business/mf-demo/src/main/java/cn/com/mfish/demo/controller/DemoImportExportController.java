package cn.com.mfish.demo.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.excel.ExcelUtils;
import cn.com.mfish.common.core.utils.excel.UploadDataListener;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.api.vo.TenantVo;
import cn.com.mfish.common.oauth.common.OauthUtils;
import cn.com.mfish.demo.entity.DemoImportExport;
import cn.com.mfish.demo.entity.DemoOrderDetail;
import cn.com.mfish.demo.req.ReqDemoImportExport;
import cn.com.mfish.demo.service.DemoImportExportService;
import cn.com.mfish.demo.service.DemoOrderDetailService;
import cn.idev.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 导入导出Demo
 * @author: mfish
 * @date: 2024-09-02
 * @version: V2.0.1
 */
@Slf4j
@Tag(name = "导入导出Demo")
@RestController
@RequestMapping("/demoImportExport")
public class DemoImportExportController {
    @Resource
    private DemoImportExportService demoImportExportService;
    @Resource
    private DemoOrderDetailService demoOrderDetailService;

    /**
     * 分页列表查询
     *
     * @param reqDemoImportExport 导入导出Demo请求参数
     * @param reqPage             分页参数
     * @return 返回导入导出Demo-分页列表
     */
    @Operation(summary = "导入导出Demo-分页列表查询", description = "导入导出Demo-分页列表查询")
    @GetMapping
    @RequiresPermissions("demo:demoImportExport:query")
    public Result<PageResult<DemoImportExport>> queryPageList(ReqDemoImportExport reqDemoImportExport, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqDemoImportExport, reqPage)), "导入导出Demo-查询成功!");
    }

    /**
     * 获取列表
     *
     * @param reqDemoImportExport 导入导出Demo请求参数
     * @param reqPage             分页参数
     * @return 返回导入导出Demo-分页列表
     */
    private List<DemoImportExport> queryList(ReqDemoImportExport reqDemoImportExport, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper<DemoImportExport> lambdaQueryWrapper = new LambdaQueryWrapper<DemoImportExport>()
                .like(!StringUtils.isEmpty(reqDemoImportExport.getUserName()), DemoImportExport::getUserName, reqDemoImportExport.getUserName())
                .eq(null != reqDemoImportExport.getOrderStatus(), DemoImportExport::getOrderStatus, reqDemoImportExport.getOrderStatus())
                .eq(null != reqDemoImportExport.getPayType(), DemoImportExport::getPayType, reqDemoImportExport.getPayType())
                .eq(null != reqDemoImportExport.getDeliveryType(), DemoImportExport::getDeliveryType, reqDemoImportExport.getDeliveryType());
        return demoImportExportService.list(lambdaQueryWrapper);
    }

    /**
     * 添加
     *
     * @param demoImportExport 导入导出Demo对象
     * @return 返回导入导出Demo-添加结果
     */
    @Log(title = "导入导出Demo-添加", operateType = OperateType.INSERT)
    @Operation(summary = "导入导出Demo-添加")
    @PostMapping
    @RequiresPermissions("demo:demoImportExport:insert")
    public Result<DemoImportExport> add(@RequestBody DemoImportExport demoImportExport) {
        if (demoImportExportService.save(demoImportExport)) {
            return Result.ok(demoImportExport, "导入导出Demo-添加成功!");
        }
        return Result.fail(demoImportExport, "错误:导入导出Demo-添加失败!");
    }

    /**
     * 编辑
     *
     * @param demoImportExport 导入导出Demo对象
     * @return 返回导入导出Demo-编辑结果
     */
    @Log(title = "导入导出Demo-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "导入导出Demo-编辑")
    @PutMapping
    @RequiresPermissions("demo:demoImportExport:update")
    public Result<DemoImportExport> edit(@RequestBody DemoImportExport demoImportExport) {
        if (demoImportExportService.updateById(demoImportExport)) {
            return Result.ok(demoImportExport, "导入导出Demo-编辑成功!");
        }
        return Result.fail(demoImportExport, "错误:导入导出Demo-编辑失败!");
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回导入导出Demo-删除结果
     */
    @Log(title = "导入导出Demo-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "导入导出Demo-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("demo:demoImportExport:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        if (demoImportExportService.removeById(id)) {
            return Result.ok(true, "导入导出Demo-删除成功!");
        }
        return Result.fail(false, "错误:导入导出Demo-删除失败!");
    }

    /**
     * 批量删除
     *
     * @param ids 批量ID
     * @return 返回导入导出Demo-删除结果
     */
    @Log(title = "导入导出Demo-批量删除", operateType = OperateType.DELETE)
    @Operation(summary = "导入导出Demo-批量删除")
    @DeleteMapping("/batch/{ids}")
    @RequiresPermissions("demo:demoImportExport:delete")
    public Result<Boolean> deleteBatch(@Parameter(name = "ids", description = "唯一性ID") @PathVariable String ids) {
        if (this.demoImportExportService.removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok(true, "导入导出Demo-批量删除成功!");
        }
        return Result.fail(false, "错误:导入导出Demo-批量删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回导入导出Demo对象
     */
    @Operation(summary = "导入导出Demo-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("demo:demoImportExport:query")
    public Result<DemoImportExport> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        DemoImportExport demoImportExport = demoImportExportService.getById(id);
        return Result.ok(demoImportExport, "导入导出Demo-查询成功!");
    }

    /**
     * 导出
     *
     * @param reqDemoImportExport 导入导出Demo请求参数
     * @param reqPage             分页参数
     * @throws IOException IO异常
     */
    @Operation(summary = "普通表格导出", description = "普通表格导出")
    @GetMapping("/export")
    @RequiresPermissions("demo:demoImportExport:export")
    public void export(ReqDemoImportExport reqDemoImportExport, ReqPage reqPage) throws IOException {
        //swagger调用会用问题，使用postman测试
        ExcelUtils.write("普通表格导出", queryList(reqDemoImportExport, reqPage));
    }

    @Operation(summary = "模板导出Demo", description = "模板导出Demo")
    @GetMapping("/export/{id}")
    @RequiresPermissions("demo:demoImportExport:export")
    public void templateExport(@PathVariable String id) throws IOException {
        DemoImportExport demoImportExport = demoImportExportService.getById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        TenantVo tenantVo = OauthUtils.getCurrentTenant();
        if (tenantVo != null) {
            map.put("tenantName", tenantVo.getName());
            map.put("tenantAddress", tenantVo.getAddress());
            map.put("sales", OauthUtils.getUser().getNickname());
            map.put("tenantPhone", tenantVo.getPhone());
        }
        map.put("userName", demoImportExport.getUserName());
        map.put("userAddress", demoImportExport.getUserAddress());
        map.put("userPhone", demoImportExport.getUserPhone());
        map.put("totalAmount", demoImportExport.getTotalAmount());
        map.put("createTime", demoImportExport.getCreateTime());
        List<DemoOrderDetail> list = demoOrderDetailService.list(new LambdaQueryWrapper<DemoOrderDetail>().eq(DemoOrderDetail::getOrderId, id));
        map.put("detail", list);
        ExcelUtils.write("/excel/销售订单.xlsx", demoImportExport.getId(), map);
    }

    @Operation(summary = "通用模板导入", description = "通用模板导入")
    @PostMapping("/import")
    @RequiresPermissions("demo:demoImportExport:import")
    public Result<Boolean> generalImport(MultipartFile file) throws IOException {
        //todo 注意excel对象头部不要添加@Accessors(chain = true)注解，会造成excel数据读取为空
        EasyExcel.read(file.getInputStream(), DemoImportExport.class, new UploadDataListener<DemoImportExport>((list, extraProp) -> {
                    //此处可以加入数据校验逻辑
                    //批量导入map建议继承BatchBaseMapper，采用批量插入
                    demoImportExportService.insertBatchSomeColumn(list);
                }, 1000))
//                .extraRead(CellExtraTypeEnum.MERGE) 需要额外读取合并单元格信息设置 默认不读取
                .sheet()
//                .headRowNumber(3) 指定初始读取行 默认从2开始
                .doRead();

        return Result.ok(true, "模板导入成功");
    }
}
