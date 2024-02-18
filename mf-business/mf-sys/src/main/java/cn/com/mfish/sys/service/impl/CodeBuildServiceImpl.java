package cn.com.mfish.sys.service.impl;

import cn.com.mfish.common.code.req.ReqCode;
import cn.com.mfish.common.code.req.ReqSearch;
import cn.com.mfish.common.code.vo.CodeVo;
import cn.com.mfish.common.code.common.FreemarkerUtils;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.entity.CodeBuild;
import cn.com.mfish.sys.mapper.CodeBuildMapper;
import cn.com.mfish.sys.service.CodeBuildService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @description: 代码构建
 * @author: mfish
 * @date: 2023-04-11
 * @version: V1.2.0
 */
@Service
public class CodeBuildServiceImpl extends ServiceImpl<CodeBuildMapper, CodeBuild> implements CodeBuildService {
    @Resource
    FreemarkerUtils freemarkerUtils;

    @Override
    @Transactional
    public Result<CodeBuild> insertCodeBuild(CodeBuild codeBuild) {
        Result<CodeBuild> result = validateCodeBuild(codeBuild);
        if (!result.isSuccess()) {
            return result;
        }
        if (save(codeBuild)) {
            ReqCode reqCode = new ReqCode();
            BeanUtils.copyProperties(codeBuild, reqCode);
            if (!freemarkerUtils.saveCode(reqCode)) {
                throw new MyRuntimeException("错误:代码构建-失败");
            }
            return Result.ok(codeBuild, "代码构建-添加成功!");
        }
        throw new MyRuntimeException("错误:代码生成失败");
    }

    @Override
    public Result<List<CodeVo>> getCode(String id) {
        return Result.ok(freemarkerUtils.getCode(buildReqCode(id)), "获取代码成功");
    }

    @Override
    public void downloadCode(String id, HttpServletResponse response) throws IOException {
        ReqCode reqCode = buildReqCode(id);
        response.reset();
        byte[] data;
        data = freemarkerUtils.downloadCode(reqCode);
        response.setHeader("Content-Disposition", "attachment;filename=" + reqCode.getTableName() + ".zip");
        response.addHeader("Content-Length", data.length + "");
        response.setContentType("application/x-zip-compressed; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }

    private Result<CodeBuild> validateCodeBuild(CodeBuild codeBuild) {
        if (StringUtils.isEmpty(codeBuild.getConnectId()) || StringUtils.isEmpty(codeBuild.getTableName())) {
            return Result.fail(codeBuild, "错误:请选择数据库和表");
        }
        return Result.ok(codeBuild, "校验成功");
    }

    /**
     * 构建请求参数
     *
     * @param id 代码唯一id
     * @return 请求参数
     */
    private ReqCode buildReqCode(String id) {
        CodeBuild codeBuild = baseMapper.selectById(id);
        ReqCode reqCode = new ReqCode();
        BeanUtils.copyProperties(codeBuild, reqCode);
        if (!StringUtils.isEmpty(codeBuild.getQueryParams())) {
            reqCode.setSearches(JSON.parseArray(codeBuild.getQueryParams(), ReqSearch.class));
        }
        return reqCode;
    }
}
