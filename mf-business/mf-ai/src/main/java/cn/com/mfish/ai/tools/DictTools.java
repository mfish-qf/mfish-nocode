package cn.com.mfish.ai.tools;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.api.entity.Dict;
import cn.com.mfish.sys.api.entity.DictItem;
import cn.com.mfish.sys.api.remote.RemoteDictService;
import cn.com.mfish.sys.api.req.ReqDict;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 字典工具
 * @author: mfish
 * @date: 2025/8/23
 */
@Description("字典工具")
@Component
public class DictTools {
    @Resource
    private RemoteDictService remoteDictService;

    /**
     * 根据字典名称查询字典编码
     *
     * @param dictName 字典名称
     * @return 字典列表
     */
    @Tool(description = "查询字典编码", returnDirect = true)
    public List<Dict> queryCodeByName(@ToolParam(description = "字典名称") String dictName) {
        return remoteDictService.queryList(RPCConstants.INNER, new ReqDict().setDictName(dictName)).getData();
    }

    /**
     * 根据字典编码查询字典项列表
     *
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    @Tool(description = "查询字典项", returnDirect = true)
    public List<DictItem> queryByCode(@ToolParam(description = "字典编码") String dictCode) {
        Result<List<DictItem>> result = remoteDictService.queryByCode(RPCConstants.INNER, dictCode);
        if (result.isSuccess()) {
            return result.getData();
        }
        return new ArrayList<>();
    }
}
