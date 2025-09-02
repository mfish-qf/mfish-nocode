package cn.com.mfish.sys.tools;

import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.sys.service.DictItemService;
import cn.com.mfish.sys.api.entity.DictItem;
import cn.com.mfish.sys.entity.Dict;
import cn.com.mfish.sys.req.ReqDict;
import cn.com.mfish.sys.service.DictService;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: 字典工具
 * @author: mfish
 * @date: 2025/8/23
 */
@Component
public class DictTools {
    @Resource
    private DictService dictService;
    @Resource
    private DictItemService dictItemService;

    @Tool(description = "查询字典编码")
    public List<Dict> queryCodeByName(@ToolParam(description = "字典名称") String dictName) {
        return dictService.queryList(new ReqDict().setDictName(dictName), new ReqPage(1, 100));
    }

    @Tool(description = "查询字典项")
    public Result<List<DictItem>> queryByCode(@ToolParam(description = "字典编码") String dictCode) {
        return dictItemService.queryByCode(dictCode);
    }
}
