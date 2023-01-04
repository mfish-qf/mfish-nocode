package cn.com.mfish.code.controller;

import cn.com.mfish.code.common.FreemarkerUtils;
import cn.com.mfish.code.req.ReqCode;
import cn.com.mfish.code.vo.CodeVo;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import freemarker.template.utility.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: mfish
 * @description：代码生成控制器
 * @date: 2022/8/18 16:45
 */
@Api(tags = "代码生成")
@RestController
@RequestMapping("/code")
@Slf4j
public class CodeController {
    @Resource
    FreemarkerUtils freemarkerUtils;

    @Log(title = "生成代码", operateType = OperateType.QUERY)
    @ApiOperation("代码生成")
    @GetMapping
    public Result<List<CodeVo>> getCode(ReqCode reqCode) {
        try {
            List<CodeVo> list = freemarkerUtils.getCode(reqCode);
            for (CodeVo code : list) {
                if (code.getName().contains("xml")) {
                    //xml需要转义后返回
                    code.setCode(StringUtil.XMLEnc(code.getCode()));
                    continue;
                }
            }
            return Result.ok(list, "生成代码成功");
        } catch (Exception ex) {
            log.error("错误:生成代码失败", ex);
            return Result.fail("错误:生成代码失败");
        }
    }

    @Log(title = "代码生成并保存到本地", operateType = OperateType.QUERY)
    @ApiOperation("代码生成并保存到本地")
    @GetMapping("/save")
    public Result<String> saveCode(ReqCode reqCode) {
        try {
            List<CodeVo> list = freemarkerUtils.getCode(reqCode);
            if (freemarkerUtils.saveCode(list)) {
                return Result.ok("保存成功");
            }
            return Result.fail("保存失败");
        } catch (Exception ex) {
            log.error("错误:生成代码失败", ex);
            return Result.fail("错误:生成代码失败");
        }
    }
}
