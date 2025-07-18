package ${packageName}.service;

import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import ${packageName}.entity.${entityName};
import ${packageName}.req.Req${entityName};
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;

/**
 * @description: ${tableInfo.tableComment}
 * @author: mfish
 * @date: ${.now?string["yyyy-MM-dd"]}
 * @version: V2.0.1
 */
public interface ${entityName}Service extends IService<${entityName}> {
    /**
     * 分页列表查询
     *
     * @param req${entityName} ${tableInfo.tableComment}请求参数
     * @param reqPage 分页参数
     * @return 返回${tableInfo.tableComment}-分页列表
     */
    Result<PageResult<${entityName}>> queryPageList(Req${entityName} req${entityName}, ReqPage reqPage);

    /**
     * 添加
     *
     * @param ${entityName?uncap_first} ${tableInfo.tableComment}对象
     * @return 返回${tableInfo.tableComment}-添加结果
     */
    Result<${entityName}> add(${entityName} ${entityName?uncap_first});

    /**
     * 编辑
     *
     * @param ${entityName?uncap_first} ${tableInfo.tableComment}对象
     * @return 返回${tableInfo.tableComment}-编辑结果
     */
    Result<${entityName}> edit(${entityName} ${entityName?uncap_first});

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回${tableInfo.tableComment}-删除结果
     */
    Result<Boolean> delete(${tableInfo.idType} id);

    /**
     * 批量删除
     *
     * @param ids 批量ID
     * @return 返回${tableInfo.tableComment}-删除结果
     */
    Result<Boolean> deleteBatch(String ids);

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回${tableInfo.tableComment}对象
     */
    Result<${entityName}> queryById(${tableInfo.idType} id);

    /**
     * 导出
     *
     * @param req${entityName} ${tableInfo.tableComment}请求参数
     * @param reqPage 分页参数
     * @throws IOException IO异常
     */
    void export(Req${entityName} req${entityName}, ReqPage reqPage) throws IOException;
}