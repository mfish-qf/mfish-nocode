package cn.com.mfish.sys.service;

import cn.com.mfish.common.code.vo.CodeVo;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.entity.CodeBuild;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @description: 代码构建
 * @author: mfish
 * @date: 2023-04-11
 * @version: V1.2.0
 */
public interface CodeBuildService extends IService<CodeBuild> {
    Result<CodeBuild> insertCodeBuild(CodeBuild codeBuild);

    Result<List<CodeVo>> getCode(String id);
    void downloadCode(String id, HttpServletResponse response) throws IOException;
}
