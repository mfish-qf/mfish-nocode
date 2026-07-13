package cn.com.mfish.common.ai.service;

import cn.com.mfish.ai.api.entity.AiModelConfig;
import cn.com.mfish.ai.api.req.ReqAiModelConfig;
import cn.com.mfish.ai.api.vo.AiModelConfigVo;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.util.List;

/**
 * @description: AI模型配置信息
 * @author: mfish
 * @date: 2026-07-03
 * @version: V2.4.0
 */
public interface AiModelConfigService extends IService<AiModelConfig> {
    /**
     * 分页列表查询
     *
     * @param reqAiModelConfig AI模型配置信息请求参数
     * @param reqPage 分页参数
     * @return 返回AI模型配置信息-分页列表
     */
    Result<PageResult<AiModelConfigVo>> queryPageList(ReqAiModelConfig reqAiModelConfig, ReqPage reqPage);

    /**
     * 列表查询
     *
     * @return 返回AI模型配置信息-列表
     */
    List<AiModelConfig> queryList(ReqAiModelConfig reqAiModelConfig);

    /**
     * 添加
     *
     * @param aiModelConfig AI模型配置信息对象
     * @return 返回AI模型配置信息-添加结果
     */
    Result<AiModelConfig> add(AiModelConfig aiModelConfig);

    /**
     * 编辑
     *
     * @param aiModelConfig AI模型配置信息对象
     * @return 返回AI模型配置信息-编辑结果
     */
    Result<AiModelConfig> edit(AiModelConfig aiModelConfig);

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回AI模型配置信息-删除结果
     */
    Result<Boolean> delete(String id);

    /**
     * 批量删除
     *
     * @param ids 批量ID
     * @return 返回AI模型配置信息-删除结果
     */
    Result<Boolean> deleteBatch(String ids);

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回AI模型配置信息对象
     */
    Result<AiModelConfig> queryById(String id);

    /**
     * 通过id查询VO
     *
     * @param id 唯一ID
     * @return 返回AI模型配置信息VO对象
     */
    Result<AiModelConfigVo> queryByIdVo(String id);

    /**
     * 导出
     *
     * @param reqAiModelConfig AI模型配置信息请求参数
     * @param reqPage 分页参数
     * @throws IOException IO异常
     */
    void export(ReqAiModelConfig reqAiModelConfig, ReqPage reqPage) throws IOException;
}
