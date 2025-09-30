package cn.com.mfish.sys.service.impl;

import cn.com.mfish.common.code.req.ReqCode;
import cn.com.mfish.common.code.req.ReqSearch;
import cn.com.mfish.common.code.vo.CodeVo;
import cn.com.mfish.common.code.common.FreemarkerUtils;
import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoMenu;
import cn.com.mfish.common.oauth.api.remote.RemoteMenuService;
import cn.com.mfish.sys.entity.CodeBuild;
import cn.com.mfish.sys.mapper.CodeBuildMapper;
import cn.com.mfish.sys.req.ReqMenuCreate;
import cn.com.mfish.sys.service.CodeBuildService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * @description: 代码构建
 * @author: mfish
 * @date: 2023-04-11
 * @version: V2.2.0
 */
@Service
public class CodeBuildServiceImpl extends ServiceImpl<CodeBuildMapper, CodeBuild> implements CodeBuildService {
    @Resource
    FreemarkerUtils freemarkerUtils;
    @Resource
    private RemoteMenuService remoteMenuService;

    @Override
    @Transactional
    public Result<CodeBuild> insertCodeBuild(CodeBuild codeBuild) {
        Result<CodeBuild> result = validateCodeBuild(codeBuild);
        if (!result.isSuccess()) {
            return result;
        }
        if (save(codeBuild)) {
            return Result.ok(codeBuild, "代码构建-添加成功!");
        }
        throw new MyRuntimeException("错误:代码生成失败");
    }

    @Override
    public Result<CodeBuild> updateCodeBuild(CodeBuild codeBuild) {
        Result<CodeBuild> result = validateCodeBuild(codeBuild);
        if (!result.isSuccess()) {
            return result;
        }
        if (updateById(codeBuild)) {
            return Result.ok(codeBuild, "代码构建-更新成功!");
        }
        throw new MyRuntimeException("错误:代码生成更新失败");
    }

    @Override
    public Result<List<CodeVo>> getCode(Long id) {
        return Result.ok(freemarkerUtils.getCode(buildReqCode(id)), "获取代码成功");
    }

    @Override
    public void downloadCode(Long id, HttpServletResponse response) throws IOException {
        ReqCode reqCode = buildReqCode(id);
        response.reset();
        byte[] data;
        data = freemarkerUtils.downloadCode(reqCode);
        response.setHeader("Content-Disposition", "attachment;filename=" + reqCode.getTableName() + ".zip");
        response.addHeader("Content-Length", data.length + "");
        response.setContentType("application/x-zip-compressed; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }

    @Override
    public Result<Boolean> saveLocal(Long id) {
        if (!freemarkerUtils.saveCode(buildReqCode(id))) {
            return Result.fail(false, "错误：代码保存本地失败");
        }
        return Result.ok(true, "代码保存成功，请重新启动后端");
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
    private ReqCode buildReqCode(Long id) {
        CodeBuild codeBuild = baseMapper.selectById(id);
        ReqCode reqCode = new ReqCode();
        BeanUtils.copyProperties(codeBuild, reqCode);
        String[] tableName = codeBuild.getTableName().split("\\.");
        //如果表名包含前缀，拆分
        if (tableName.length > 1) {
            reqCode.setTableSchema(tableName[0]);
            reqCode.setTableName(tableName[1]);
        }
        if (!StringUtils.isEmpty(codeBuild.getQueryParams())) {
            reqCode.setSearches(JSON.parseArray(codeBuild.getQueryParams(), ReqSearch.class));
        }
        return reqCode;
    }

    @Override
    public Result<SsoMenu> createMenu(ReqMenuCreate reqMenuCreate) {
        ReqCode reqCode = buildReqCode(reqMenuCreate.getId());
        freemarkerUtils.initReqCode(reqCode);
        String routePath = "/" + StringUtils.toKebabCase(reqCode.getEntityName());
        if (remoteMenuService.routeExist(RPCConstants.INNER, routePath, reqMenuCreate.getParentId()).getData()) {
            return Result.fail(null, "错误：菜单路由已存在，请勿重复操作");
        }
        SsoMenu ssoMenu = new SsoMenu();
        ssoMenu.setMenuName(reqCode.getTableComment())
                .setRoutePath(routePath)
                .setComponent("/" + reqCode.getApiPrefix() + routePath + "/index.vue")
                .setMenuIcon("ant-design:bars-outlined")
                .setMenuSort(999).setMenuType(1).setIsExternal(0).setIsKeepalive(1).setIsVisible(1)
                .setParentId(reqMenuCreate.getParentId());
        Result<SsoMenu> result = remoteMenuService.add(RPCConstants.INNER, ssoMenu);
        if (!result.isSuccess()) {
            return result;
        }
        remoteMenuService.add(RPCConstants.INNER, buildButtonMenu(result.getData().getId(), reqCode, OperateType.QUERY, 1));
        remoteMenuService.add(RPCConstants.INNER, buildButtonMenu(result.getData().getId(), reqCode, OperateType.INSERT, 2));
        remoteMenuService.add(RPCConstants.INNER, buildButtonMenu(result.getData().getId(), reqCode, OperateType.UPDATE, 3));
        remoteMenuService.add(RPCConstants.INNER, buildButtonMenu(result.getData().getId(), reqCode, OperateType.DELETE, 4));
        return result;
    }

    private SsoMenu buildButtonMenu(String id, ReqCode reqCode, OperateType type, int sort) {
        SsoMenu menu = new SsoMenu();
        menu.setParentId(id)
                .setMenuName(type.toString())
                .setMenuType(2)
                .setMenuSort(sort);
        if (type == OperateType.QUERY) {
            menu.setPermissions(reqCode.getApiPrefix() + ":" + StringUtils.firstLowerCase(reqCode.getEntityName())
                    + ":" + StringUtils.toRootLowerCase(type.name()));
        } else {
            String lowerCaseEntity = StringUtils.firstLowerCase(reqCode.getEntityName());
            menu.setPermissions(reqCode.getApiPrefix() + ":" + lowerCaseEntity
                    + ":" + StringUtils.toRootLowerCase(OperateType.QUERY.name()) + "," +
                    reqCode.getApiPrefix() + ":" + lowerCaseEntity
                    + ":" + StringUtils.toRootLowerCase(type.name()));
        }
        return menu;
    }
}
