package ${packageName}.service.impl;

import cn.com.mfish.common.core.utils.excel.ExcelUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import ${packageName}.entity.${entityName};
import ${packageName}.req.Req${entityName};
import ${packageName}.mapper.${entityName}Mapper;
import ${packageName}.service.${entityName}Service;
<#if searchList?size!=0>
import cn.com.mfish.common.core.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
</#if>
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.io.IOException;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
* @description: ${tableInfo.tableComment}
* @author: mfish
* @date: ${.now?string["yyyy-MM-dd"]}
* @version: V2.0.0
*/
@Service
public class ${entityName}ServiceImpl extends ServiceImpl<${entityName}Mapper, ${entityName}> implements ${entityName}Service {
    /**
     * 分页列表查询
     *
     * @param req${entityName} ${tableInfo.tableComment}请求参数
     * @param reqPage 分页参数
     * @return 返回${tableInfo.tableComment}-分页列表
     */
    @Override
    public Result<PageResult<${entityName}>> queryPageList(Req${entityName} req${entityName}, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(req${entityName}, reqPage)), "${tableInfo.tableComment}-查询成功!");
    }

    /**
     * 获取列表
     *
     * @param req${entityName} ${tableInfo.tableComment}请求参数
     * @param reqPage 分页参数
     * @return 返回${tableInfo.tableComment}-分页列表
     */
    private List<${entityName}> queryList(Req${entityName} req${entityName}, ReqPage reqPage) {
    PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
    <#if searchList?size!=0>
        LambdaQueryWrapper<${entityName}> lambdaQueryWrapper = new LambdaQueryWrapper<${entityName}>()
        <#list searchList as search>
            <#if search.fieldInfo.type =='String'>
                .${search.condition}(!StringUtils.isEmpty(req${entityName}.get${search.field}()), ${entityName}::get${search.field}, req${entityName}.get${search.field}())
            <#else>
                .${search.condition}(null != req${entityName}.get${search.field}(), ${entityName}::get${search.field}, req${entityName}.get${search.field}())
            </#if>
        </#list>;
        return list(lambdaQueryWrapper);
    <#else>
        return list();
    </#if>
    }

    /**
     * 添加
     *
     * @param ${entityName?uncap_first} ${tableInfo.tableComment}对象
     * @return 返回${tableInfo.tableComment}-添加结果
     */
    @Override
    public Result<${entityName}> add(${entityName} ${entityName?uncap_first}) {
        if (save(${entityName?uncap_first})) {
            return Result.ok(${entityName?uncap_first}, "${tableInfo.tableComment}-添加成功!");
        }
        return Result.fail(${entityName?uncap_first}, "错误:${tableInfo.tableComment}-添加失败!");
    }

    /**
     * 编辑
     *
     * @param ${entityName?uncap_first} ${tableInfo.tableComment}对象
     * @return 返回${tableInfo.tableComment}-编辑结果
     */
    @Override
    public Result<${entityName}> edit(${entityName} ${entityName?uncap_first}) {
        if (updateById(${entityName?uncap_first})) {
            return Result.ok(${entityName?uncap_first}, "${tableInfo.tableComment}-编辑成功!");
        }
        return Result.fail(${entityName?uncap_first}, "错误:${tableInfo.tableComment}-编辑失败!");
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回${tableInfo.tableComment}-删除结果
     */
    @Override
    public Result<Boolean> delete(${tableInfo.idType} id) {
        if (removeById(id)) {
            return Result.ok(true, "${tableInfo.tableComment}-删除成功!");
        }
        return Result.fail(false, "错误:${tableInfo.tableComment}-删除失败!");
    }

    /**
     * 批量删除
     *
     * @param ids 批量ID
     * @return 返回${tableInfo.tableComment}-删除结果
     */
    @Override
    public Result<Boolean> deleteBatch(String ids) {
        if (removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok(true, "${tableInfo.tableComment}-批量删除成功!");
        }
        return Result.fail(false, "错误:${tableInfo.tableComment}-批量删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回${tableInfo.tableComment}对象
     */
    @Override
    public Result<${entityName}> queryById(${tableInfo.idType} id) {
        ${entityName} ${entityName?uncap_first} = getById(id);
        return Result.ok(${entityName?uncap_first}, "${tableInfo.tableComment}-查询成功!");
    }

    /**
     * 导出
     *
     * @param req${entityName} ${tableInfo.tableComment}请求参数
     * @param reqPage 分页参数
     * @throws IOException IO异常
     */
    @Override
    public void export(Req${entityName} req${entityName}, ReqPage reqPage) throws IOException {
        //swagger调用会用问题，使用postman测试
        <#if tableInfo.tableComment??>
        ExcelUtils.write("${tableInfo.tableComment}_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()), queryList(req${entityName}, reqPage));
        <#else>
        ExcelUtils.write("${entityName}_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()), queryList(req${entityName}, reqPage));
        </#if>
    }
}
