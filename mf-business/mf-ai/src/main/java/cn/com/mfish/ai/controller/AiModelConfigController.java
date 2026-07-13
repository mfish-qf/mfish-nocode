package cn.com.mfish.ai.controller;

import cn.com.mfish.ai.api.entity.AiModelConfig;
import cn.com.mfish.ai.api.req.ReqAiModelConfig;
import cn.com.mfish.ai.api.vo.AiModelConfigVo;
import cn.com.mfish.common.ai.service.AiModelConfigService;
import cn.com.mfish.common.core.annotation.InnerUser;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.DataScope;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.common.DataScopeType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @description: AI模型配置信息
 * @author: mfish
 * @date: 2026-07-03
 * @version: V2.4.0
 */
@Slf4j
@Tag(name = "AI模型配置信息")
@RestController
@RequestMapping("/aiModelConfig")
public class AiModelConfigController {
    @Resource
    private AiModelConfigService aiModelConfigService;

    /**
     * 分页列表查询（脱敏处理）
     *
     * @param reqAiModelConfig AI模型配置信息请求参数
     * @param reqPage          分页参数
     * @return 返回AI模型配置信息-分页列表
     */
    @Operation(summary = "AI模型配置信息-分页列表查询", description = "AI模型配置信息-分页列表查询")
    @GetMapping
    @RequiresPermissions("ai:aiModelConfig:query")
    @DataScope(table = "ai_model_config", type = DataScopeType.Tenant, excludes = {"tenant_id = '1'"}, superIgnore = true)
    public Result<PageResult<AiModelConfigVo>> queryPageList(ReqAiModelConfig reqAiModelConfig, ReqPage reqPage) {
        return aiModelConfigService.queryPageList(reqAiModelConfig, reqPage);
    }

    /**
     * 内部列表查询（不脱敏）
     *
     * @param reqAiModelConfig AI模型配置信息请求参数
     * @return 返回AI模型配置信息-列表
     */
    @Operation(summary = "AI模型配置信息-列表查询", description = "AI模型配置信息-列表查询")
    @GetMapping("/list")
    @InnerUser
    public Result<List<AiModelConfig>> queryList(ReqAiModelConfig reqAiModelConfig) {
        return Result.ok(aiModelConfigService.queryList(reqAiModelConfig));
    }


    /**
     * 添加
     *
     * @param aiModelConfig AI模型配置信息对象
     * @return 返回AI模型配置信息-添加结果
     */
    @Log(title = "AI模型配置信息-添加", operateType = OperateType.INSERT)
    @Operation(summary = "AI模型配置信息-添加")
    @PostMapping
    @RequiresPermissions("ai:aiModelConfig:insert")
    public Result<AiModelConfig> add(@RequestBody AiModelConfig aiModelConfig) {
        return aiModelConfigService.add(aiModelConfig);
    }

    /**
     * 编辑
     *
     * @param aiModelConfig AI模型配置信息对象
     * @return 返回AI模型配置信息-编辑结果
     */
    @Log(title = "AI模型配置信息-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "AI模型配置信息-编辑")
    @PutMapping
    @RequiresPermissions("ai:aiModelConfig:update")
    public Result<AiModelConfig> edit(@RequestBody AiModelConfig aiModelConfig) {
        return aiModelConfigService.edit(aiModelConfig);
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回AI模型配置信息-删除结果
     */
    @Log(title = "AI模型配置信息-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "AI模型配置信息-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("ai:aiModelConfig:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return aiModelConfigService.delete(id);
    }

    /**
     * 批量删除
     *
     * @param ids 批量ID
     * @return 返回AI模型配置信息-删除结果
     */
    @Log(title = "AI模型配置信息-批量删除", operateType = OperateType.DELETE)
    @Operation(summary = "AI模型配置信息-批量删除")
    @DeleteMapping("/batch/{ids}")
    @RequiresPermissions("ai:aiModelConfig:delete")
    public Result<Boolean> deleteBatch(@Parameter(name = "ids", description = "唯一性ID") @PathVariable String ids) {
        return aiModelConfigService.deleteBatch(ids);
    }

    /**
     * 通过id查询（不脱敏）
     *
     * @param id 唯一ID
     * @return 返回AI模型配置信息对象
     */
    @Operation(summary = "AI模型配置信息-通过id查询")
    @GetMapping("/{id}")
    @InnerUser
    public Result<AiModelConfig> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return aiModelConfigService.queryById(id);
    }

    /**
     * 通过id查询（脱敏处理）
     *
     * @param id 唯一ID
     * @return 返回AI模型配置信息对象
     */
    @Operation(summary = "AI模型配置信息-通过id查询")
    @GetMapping("/mask/{id}")
    @RequiresPermissions("ai:aiModelConfig:query")
    @DataScope(table = "ai_model_config", type = DataScopeType.Tenant, excludes = {"tenant_id = '1'"}, superIgnore = true)
    public Result<AiModelConfigVo> queryByIdVo(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return aiModelConfigService.queryByIdVo(id);
    }

    /**
     * 导出
     *
     * @param reqAiModelConfig AI模型配置信息请求参数
     * @param reqPage          分页参数
     * @throws IOException IO异常
     */
    @Operation(summary = "导出AI模型配置信息", description = "导出AI模型配置信息")
    @GetMapping("/export")
    @RequiresPermissions("ai:aiModelConfig:export")
    public void export(ReqAiModelConfig reqAiModelConfig, ReqPage reqPage) throws IOException {
        aiModelConfigService.export(reqAiModelConfig, reqPage);
    }
}
