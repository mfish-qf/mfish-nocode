package cn.com.mfish.ai.tools;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoMenu;
import cn.com.mfish.common.oauth.api.remote.RemoteMenuService;
import cn.com.mfish.common.oauth.api.req.ReqSsoMenu;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: 菜单工具
 * @author: mfish
 * @date: 2025/8/23
 */
@Description("菜单工具")
@Component
public class MenuTools {
    @Resource
    private RemoteMenuService remoteMenuService;

    @Tool(description = "查询菜单树结构")
    public Result<List<SsoMenu>> queryList() {
        return remoteMenuService.queryMenuTree(RPCConstants.INNER, new ReqSsoMenu().setMenuType(1));
    }

    @Tool(description = "查询子菜单信息")
    public Result<List<SsoMenu>> queryChild(@ToolParam(description = "菜单名称") String menuName) {
        return remoteMenuService.queryMenuTree(RPCConstants.INNER, new ReqSsoMenu().setMenuName(menuName));
    }
}
