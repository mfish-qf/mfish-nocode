package cn.com.mfish.sys.tools;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.sys.service.DictItemService;
import cn.com.mfish.sys.api.entity.DictItem;
import cn.com.mfish.sys.entity.Dict;
import cn.com.mfish.sys.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<Dict>()
                .like(dictName != null, Dict::getDictName, dictName)
                .orderByDesc(true, Dict::getCreateTime);
        return dictService.list(queryWrapper);
    }

    @Tool(description = "查询字典项")
    public Result<List<DictItem>> queryByCode(@ToolParam(description = "字典编码") String dictCode) {
        return dictItemService.queryByCode(dictCode);
    }
}
