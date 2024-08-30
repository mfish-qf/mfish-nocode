package cn.com.mfish.sys.service;

import cn.com.mfish.common.code.vo.CodeVo;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoMenu;
import cn.com.mfish.sys.entity.CodeBuild;
import cn.com.mfish.sys.req.ReqMenuCreate;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * @description: 代码构建
 * @author: mfish
 * @date: 2023-04-11
 * @version: V1.3.1
 */
public interface CodeBuildService extends IService<CodeBuild> {
    Result<CodeBuild> insertCodeBuild(CodeBuild codeBuild);

    Result<CodeBuild> updateCodeBuild(CodeBuild codeBuild);

    Result<List<CodeVo>> getCode(Long id);

    void downloadCode(Long id, HttpServletResponse response) throws IOException;

    Result<Boolean> saveLocal(Long id);

    Result<SsoMenu> createMenu(ReqMenuCreate reqMenuCreate);
}
