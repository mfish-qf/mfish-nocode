package cn.com.mfish.ai.service.impl;

import cn.com.mfish.ai.api.entity.AiModelConfig;
import cn.com.mfish.ai.api.req.ReqAiModelConfig;
import cn.com.mfish.ai.api.vo.AiModelConfigVo;
import cn.com.mfish.ai.event.AiModelChangedEvent;
import cn.com.mfish.ai.mapper.AiModelConfigMapper;
import cn.com.mfish.common.ai.service.AiModelConfigService;
import cn.com.mfish.common.core.secret.SM4Utils;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.excel.ExcelUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @description: AI模型配置信息
 * @author: mfish
 * @date: 2026-07-03
 * @version: V2.4.0
 */
@Service
public class AiModelConfigServiceImpl extends ServiceImpl<AiModelConfigMapper, AiModelConfig> implements AiModelConfigService {

    @Value("${ai.api-key.hex-key:}")
    private String hexApiKey;
    private final ApplicationEventPublisher eventPublisher;

    public AiModelConfigServiceImpl(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 模型配置发生变更后发布事件，触发 LlmModelRouter 重新加载模型并通知助手重建 chatClient。
     */
    private void publishModelChanged() {
        eventPublisher.publishEvent(new AiModelChangedEvent(this));
    }

    /**
     * 分页列表查询
     *
     * @param reqAiModelConfig AI模型配置信息请求参数
     * @param reqPage          分页参数
     * @return 返回AI模型配置信息-分页列表
     */
    @Override
    public Result<PageResult<AiModelConfigVo>> queryPageList(ReqAiModelConfig reqAiModelConfig, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqAiModelConfig, reqPage)), "AI模型配置信息-查询成功!");
    }

    /**
     * 获取列表
     *
     * @param reqAiModelConfig AI模型配置信息请求参数
     * @param reqPage          分页参数
     * @return 返回AI模型配置信息-分页列表
     */
    private List<AiModelConfigVo> queryList(ReqAiModelConfig reqAiModelConfig, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        List<AiModelConfig> list = queryList(reqAiModelConfig);
        List<AiModelConfigVo> voList = new ArrayList<>();
        for (AiModelConfig entity : list) {
            AiModelConfigVo vo = new AiModelConfigVo();
            BeanUtils.copyProperties(entity, vo);
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public List<AiModelConfig> queryList(ReqAiModelConfig reqAiModelConfig) {
        LambdaQueryWrapper<AiModelConfig> lambdaQueryWrapper = new LambdaQueryWrapper<AiModelConfig>()
                .eq(!StringUtils.isEmpty(reqAiModelConfig.getModelName()), AiModelConfig::getModelName, reqAiModelConfig.getModelName())
                .eq(null != reqAiModelConfig.getEnabled(), AiModelConfig::getEnabled, reqAiModelConfig.getEnabled())
                .eq(!StringUtils.isEmpty(reqAiModelConfig.getProtocol()), AiModelConfig::getProtocol, reqAiModelConfig.getProtocol())
                .orderByAsc(AiModelConfig::getSortOrder);
        List<AiModelConfig> list = list(lambdaQueryWrapper);
        for (int i = 0; i < list.size(); i++) {
            AiModelConfig entity = list.get(i);
            if (StringUtils.isEmpty(entity.getApiKey())) {
                continue;
            }
            try {
                entity.setApiKey(SM4Utils.decryptEcb(hexApiKey, entity.getApiKey()));
            } catch (Exception e) {
                log.error("解密异常" + e.getMessage(), e);
                list.remove(i--);
            }
        }
        return list;
    }

    /**
     * 添加
     *
     * @param aiModelConfig AI模型配置信息对象
     * @return 返回AI模型配置信息-添加结果
     */
    @Override
    public Result<AiModelConfig> add(AiModelConfig aiModelConfig) {
        aiModelConfig.setTenantId(AuthInfoUtils.getCurrentTenantId());
        if (StringUtils.isNotEmpty(aiModelConfig.getApiKey())) {
            aiModelConfig.setApiKey(SM4Utils.encryptEcb(hexApiKey, aiModelConfig.getApiKey()));
        }
        if (save(aiModelConfig)) {
            publishModelChanged();
            return Result.ok(aiModelConfig, "AI模型配置信息-添加成功!");
        }
        return Result.fail(aiModelConfig, "错误:AI模型配置信息-添加失败!");
    }

    /**
     * 编辑
     *
     * @param aiModelConfig AI模型配置信息对象
     * @return 返回AI模型配置信息-编辑结果
     */
    @Override
    public Result<AiModelConfig> edit(AiModelConfig aiModelConfig) {
        if (StringUtils.isNotEmpty(aiModelConfig.getApiKey())) {
            //判断是否包含****
            if (aiModelConfig.getApiKey().contains("****")) {
                aiModelConfig.setApiKey(null);
            } else {
                aiModelConfig.setApiKey(SM4Utils.encryptEcb(hexApiKey, aiModelConfig.getApiKey()));
            }
        }
        if (updateById(aiModelConfig)) {
            publishModelChanged();
            return Result.ok(aiModelConfig, "AI模型配置信息-编辑成功!");
        }
        return Result.fail(aiModelConfig, "错误:AI模型配置信息-编辑失败!");
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回AI模型配置信息-删除结果
     */
    @Override
    public Result<Boolean> delete(String id) {
        if (removeById(id)) {
            publishModelChanged();
            return Result.ok(true, "AI模型配置信息-删除成功!");
        }
        return Result.fail(false, "错误:AI模型配置信息-删除失败!");
    }

    /**
     * 批量删除
     *
     * @param ids 批量ID
     * @return 返回AI模型配置信息-删除结果
     */
    @Override
    public Result<Boolean> deleteBatch(String ids) {
        if (removeByIds(Arrays.asList(ids.split(",")))) {
            publishModelChanged();
            return Result.ok(true, "AI模型配置信息-批量删除成功!");
        }
        return Result.fail(false, "错误:AI模型配置信息-批量删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回AI模型配置信息对象
     */
    @Override
    public Result<AiModelConfig> queryById(String id) {
        AiModelConfig aiModelConfig = getById(id);
        return Result.ok(aiModelConfig, "AI模型配置信息-查询成功!");
    }

    @Override
    public Result<AiModelConfigVo> queryByIdVo(String id) {
        AiModelConfig entity = getById(id);
        if (entity == null) {
            return Result.fail(null, "错误:AI模型配置信息-未找到对应数据!");
        }
        AiModelConfigVo vo = new AiModelConfigVo();
        BeanUtils.copyProperties(entity, vo);
        return Result.ok(vo, "AI模型配置信息-查询成功!");
    }

    /**
     * 导出
     *
     * @param reqAiModelConfig AI模型配置信息请求参数
     * @param reqPage          分页参数
     * @throws IOException IO异常
     */
    @Override
    public void export(ReqAiModelConfig reqAiModelConfig, ReqPage reqPage) throws IOException {
        //swagger调用会用问题，使用postman测试
        ExcelUtils.write("AI模型配置信息_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()), queryList(reqAiModelConfig, reqPage));
    }
}
