package cn.com.mfish.sys.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.entity.CodeBuild;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @description: 代码构建
 * @author: mfish
 * @date: 2023-04-11
 * @version: V1.0.0
 */
public interface CodeBuildService extends IService<CodeBuild> {
    Result<CodeBuild> insertCodeBuild(CodeBuild codeBuild);
}
