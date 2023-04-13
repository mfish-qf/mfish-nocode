package cn.com.mfish.sys.service.impl;

import cn.com.mfish.common.code.api.remote.RemoteCodeService;
import cn.com.mfish.common.code.api.req.ReqCode;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.entity.CodeBuild;
import cn.com.mfish.sys.mapper.CodeBuildMapper;
import cn.com.mfish.sys.service.CodeBuildService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @description: 代码构建
 * @author: mfish
 * @date: 2023-04-11
 * @version: V1.0.0
 */
@Service
public class CodeBuildServiceImpl extends ServiceImpl<CodeBuildMapper, CodeBuild> implements CodeBuildService {
    @Resource
    private RemoteCodeService remoteCodeService;

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
            if (!remoteCodeService.saveCode(reqCode).isSuccess()) {
                throw new MyRuntimeException("错误:代码生成失败");
            }
            return Result.ok(codeBuild, "代码构建-添加成功!");
        }
        throw new MyRuntimeException("错误:代码生成失败");
    }

    private Result<CodeBuild> validateCodeBuild(CodeBuild codeBuild) {
        if (StringUtils.isEmpty(codeBuild.getConnectId()) || StringUtils.isEmpty(codeBuild.getTableName())) {
            return Result.fail(codeBuild, "错误:请选择数据库和表");
        }
        return Result.ok(codeBuild, "校验成功");
    }
}
