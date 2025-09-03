package cn.com.mfish.oauth.tools;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoMenu;
import cn.com.mfish.common.oauth.req.ReqSsoMenu;
import cn.com.mfish.common.oauth.service.SsoMenuService;
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
    private SsoMenuService ssoMenuService;

    @Tool(description = "查询菜单树结构")
    public Result<List<SsoMenu>> queryList() {
        return ssoMenuService.queryMenuTree(new ReqSsoMenu().setMenuType(1), null);
    }

    @Tool(description = "查询子菜单信息")
    public Result<List<SsoMenu>> queryChild(@ToolParam(description = "菜单名称") String menuName) {
        return ssoMenuService.queryMenuTree(new ReqSsoMenu().setMenuName(menuName), null);
    }
}
