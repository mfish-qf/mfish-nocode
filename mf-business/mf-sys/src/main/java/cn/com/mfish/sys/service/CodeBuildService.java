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
 * @version: V2.3.1
 */
public interface CodeBuildService extends IService<CodeBuild> {
    /**
     * 新增代码构建配置
     *
     * @param codeBuild 代码构建配置对象
     * @return 操作结果
     */
    Result<CodeBuild> insertCodeBuild(CodeBuild codeBuild);

    /**
     * 修改代码构建配置
     *
     * @param codeBuild 代码构建配置对象
     * @return 操作结果
     */
    Result<CodeBuild> updateCodeBuild(CodeBuild codeBuild);

    /**
     * 获取代码生成信息
     *
     * @param id 代码构建 ID
     * @return 代码生成信息列表
     */
    Result<List<CodeVo>> getCode(Long id);

    /**
     * 下载代码
     *
     * @param id 代码构建 ID
     * @param response HTTP 响应对象
     * @throws IOException IO 异常
     */
    void downloadCode(Long id, HttpServletResponse response) throws IOException;

    /**
     * 保存代码到本地
     *
     * @param id 代码构建 ID
     * @return 操作结果
     */
    Result<Boolean> saveLocal(Long id);

    /**
     * 创建菜单
     *
     * @param reqMenuCreate 菜单创建请求参数
     * @return 创建的菜单对象
     */
    Result<SsoMenu> createMenu(ReqMenuCreate reqMenuCreate);
}
