package cn.com.mfish.common.nocode.service;

import cn.com.mfish.common.core.entity.WorkflowCompleteResult;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.nocode.entity.ScreenResourceImg;
import com.baomidou.mybatisplus.spring.service.IService;

/**
 * @description: 大屏图片资源信息
 * @author: mfish
 * @date: 2026-09-04
 * @version: V2.4.1
 */
public interface ScreenResourceImgService extends IService<ScreenResourceImg> {
    /**
     * 新增图片资源（用户上传，未发布状态）
     *
     * @param screenResourceImg 图片资源信息
     * @return 返回新增结果
     */
    Result<ScreenResourceImg> insertImageResource(ScreenResourceImg screenResourceImg);

    /**
     * 发布图片资源（提交审批流程）
     *
     * @param screenResourceImg 图片资源信息（含分类、名称、描述）
     * @return 返回发布结果
     */
    Result<ScreenResourceImg> publishImageResource(ScreenResourceImg screenResourceImg);

    /**
     * 删除图片资源（仅允许删除本人资源，已发布资源需先撤回）
     *
     * @param id 图片资源唯一ID
     * @return 返回删除结果
     */
    Result<Boolean> deleteImageResource(String id);

    /**
     * 撤回已发布的图片资源（从系统资源下架，恢复为未发布状态）
     *
     * @param id 图片资源唯一ID
     * @return 返回撤回结果
     */
    Result<Boolean> withdrawImageResource(String id);

    /**
     * 图片资源审核操作（通过、驳回、撤销）
     *
     * @param id         图片资源唯一ID
     * @param auditState 审核状态（1：通过，2：驳回，null：撤销）
     * @param result     工作流审批结果
     * @return 返回审核结果
     */
    Result<String> audit(String id, Integer auditState, WorkflowCompleteResult result);

    /**
     * 更新使用次数
     *
     * @param id 图片资源唯一ID
     * @return 返回更新结果
     */
    Result<Boolean> addUseCount(String id);
}
