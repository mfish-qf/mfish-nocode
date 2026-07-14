package cn.com.mfish.ai.controller;

import cn.com.mfish.ai.api.entity.AiModelConfig;
import cn.com.mfish.ai.api.req.ReqAiModelConfig;
import cn.com.mfish.ai.api.vo.AiModelConfigVo;
import cn.com.mfish.common.ai.service.AiModelConfigService;
import cn.com.mfish.common.core.annotation.InnerUser;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.FileUtils;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.DataScope;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.common.DataScopeType;
import cn.idev.excel.EasyExcel;
import cn.idev.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @description: AI模型配置信息（兼容Servlet与WebFlux双栈）
 * <p>
 * 所有CRUD方法返回 Mono，通过 subscribeOn(Schedulers.boundedElastic) 将阻塞式JDBC调用
 * 隔离到弹性线程池，避免在WebFlux下阻塞Netty事件循环；在Servlet下也能正常工作。
 * </p>
 * @author: mfish
 * @date: 2026-07-03
 * @version: V2.4.1
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
     */
    @Operation(summary = "AI模型配置信息-分页列表查询", description = "AI模型配置信息-分页列表查询")
    @GetMapping
    @RequiresPermissions("ai:aiModelConfig:query")
    @DataScope(table = "ai_model_config", type = DataScopeType.Tenant, excludes = {"tenant_id = '1'"}, superIgnore = true)
    public Mono<Result<PageResult<AiModelConfigVo>>> queryPageList(ReqAiModelConfig reqAiModelConfig, ReqPage reqPage) {
        return Mono.fromCallable(() -> aiModelConfigService.queryPageList(reqAiModelConfig, reqPage))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 内部列表查询（不脱敏）
     */
    @Operation(summary = "AI模型配置信息-列表查询", description = "AI模型配置信息-列表查询")
    @GetMapping("/list")
    @InnerUser
    public Mono<Result<List<AiModelConfig>>> queryList(ReqAiModelConfig reqAiModelConfig) {
        return Mono.fromCallable(() -> Result.ok(aiModelConfigService.queryList(reqAiModelConfig)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 添加
     */
    @Log(title = "AI模型配置信息-添加", operateType = OperateType.INSERT)
    @Operation(summary = "AI模型配置信息-添加")
    @PostMapping
    @RequiresPermissions("ai:aiModelConfig:insert")
    public Mono<Result<AiModelConfig>> add(@RequestBody AiModelConfig aiModelConfig) {
        return Mono.fromCallable(() -> aiModelConfigService.add(aiModelConfig))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 编辑
     */
    @Log(title = "AI模型配置信息-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "AI模型配置信息-编辑")
    @PutMapping
    @RequiresPermissions("ai:aiModelConfig:update")
    public Mono<Result<AiModelConfig>> edit(@RequestBody AiModelConfig aiModelConfig) {
        return Mono.fromCallable(() -> aiModelConfigService.edit(aiModelConfig))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 通过id删除
     */
    @Log(title = "AI模型配置信息-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "AI模型配置信息-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("ai:aiModelConfig:delete")
    public Mono<Result<Boolean>> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return Mono.fromCallable(() -> aiModelConfigService.delete(id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 批量删除
     */
    @Log(title = "AI模型配置信息-批量删除", operateType = OperateType.DELETE)
    @Operation(summary = "AI模型配置信息-批量删除")
    @DeleteMapping("/batch/{ids}")
    @RequiresPermissions("ai:aiModelConfig:delete")
    public Mono<Result<Boolean>> deleteBatch(@Parameter(name = "ids", description = "唯一性ID") @PathVariable String ids) {
        return Mono.fromCallable(() -> aiModelConfigService.deleteBatch(ids))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 通过id查询（不脱敏）
     */
    @Operation(summary = "AI模型配置信息-通过id查询")
    @GetMapping("/{id}")
    @InnerUser
    public Mono<Result<AiModelConfig>> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return Mono.fromCallable(() -> aiModelConfigService.queryById(id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 通过id查询（脱敏处理）
     */
    @Operation(summary = "AI模型配置信息-通过id查询")
    @GetMapping("/mask/{id}")
    @RequiresPermissions("ai:aiModelConfig:query")
    @DataScope(table = "ai_model_config", type = DataScopeType.Tenant, excludes = {"tenant_id = '1'"}, superIgnore = true)
    public Mono<Result<AiModelConfigVo>> queryByIdVo(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return Mono.fromCallable(() -> aiModelConfigService.queryByIdVo(id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 导出（双栈兼容）
     * WebFlux环境：在弹性线程池生成Excel字节流，通过ServerHttpResponse写出
     * Servlet环境：直接调用service.export写HttpServletResponse
     */
    @Operation(summary = "导出AI模型配置信息", description = "导出AI模型配置信息")
    @GetMapping("/export")
    @RequiresPermissions("ai:aiModelConfig:export")
    public Mono<Void> export(ReqAiModelConfig reqAiModelConfig, ReqPage reqPage) {
        // WebFlux路径
        ServerWebExchange exchange = ServletUtils.getExchange();
        if (exchange != null) {
            return exportReactive(exchange, reqAiModelConfig, reqPage);
        }
        // Servlet路径：复用原同步导出逻辑
        return Mono.fromRunnable(() -> {
            try {
                aiModelConfigService.export(reqAiModelConfig, reqPage);
            } catch (IOException e) {
                throw new MyRuntimeException("错误:导出失败", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    /**
     * WebFlux环境下的Excel导出
     * 在弹性线程池生成字节流，再通过响应式写回
     */
    private Mono<Void> exportReactive(ServerWebExchange exchange, ReqAiModelConfig reqAiModelConfig, ReqPage reqPage) {
        return Mono.fromCallable(() -> {
                    List<AiModelConfigVo> data = aiModelConfigService.queryExportList(reqAiModelConfig, reqPage);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    EasyExcel.write(baos)
                            .head(AiModelConfigVo.class)
                            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                            .sheet("sheet1")
                            .doWrite(data);
                    return baos.toByteArray();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(bytes -> {
                    String fileName = "AI模型配置信息_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    String encodedName = FileUtils.encodeFileName(fileName);
                    ServerHttpResponse response = exchange.getResponse();
                    response.getHeaders().setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                    response.getHeaders().set("Content-disposition", "attachment;filename*=utf-8'zh_cn'" + encodedName + ".xlsx");
                    return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
                });
    }
}
