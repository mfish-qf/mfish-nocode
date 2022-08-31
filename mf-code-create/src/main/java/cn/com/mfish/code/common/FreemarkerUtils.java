package cn.com.mfish.code.common;

import cn.com.mfish.code.config.FreemarkerProperties;
import cn.com.mfish.code.entity.CodeInfo;
import cn.com.mfish.code.entity.FieldInfo;
import cn.com.mfish.code.entity.TableInfo;
import cn.com.mfish.code.service.MysqlTableService;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.utility.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：qiufeng
 * @description：代码生成工具类
 * @date ：2022/8/24 15:30
 */
@Component
@Slf4j
public class FreemarkerUtils {
    @Resource
    Configuration fmConfig;
    @Resource
    FreemarkerProperties freemarkerProperties;
    @Resource
    MysqlTableService mysqlTableService;

    public Map<String, String> getCode(String schema, String tableName, String packageName) {
        //TODO 表描述从数据库获取预留
        String tableDesc = "";
        return getCode(schema, tableName, tableDesc, packageName, null);
    }
    /**
     * @param schema      库名
     * @param tableName   表名
     * @param tableDesc   表描述
     * @param packageName 项目包名
     *
     * @return
     */
    public Map<String, String> getCode(String schema, String tableName, String tableDesc, String packageName) {
        return getCode(schema, tableName, tableDesc, packageName, null);
    }

    /**
     * @param schema      库名
     * @param tableName   表名
     * @param tableDesc   表描述
     * @param packageName 项目包名
     * @param entityName  entity属性名
     * @return
     */
    public Map<String, String> getCode(String schema, String tableName, String tableDesc, String packageName, String entityName) {
        CodeInfo codeInfo = new CodeInfo();
        codeInfo.setPackageName(packageName);
        if (StringUtils.isEmpty(entityName)) {
            entityName = StringUtils.toCamelBigCase(tableName);
        }
        codeInfo.setEntityName(entityName);
        List<FieldInfo> list = mysqlTableService.getColumns(schema, tableName);
        codeInfo.setTableInfo(new TableInfo().setColumns(list).setTableName(tableName).setTableDesc(tableDesc));
        return getCode(codeInfo);
    }

    /**
     * 获取生成的代码并返回前端
     *
     * @param codeInfo
     * @return
     */
    public Map<String, String> getCode(CodeInfo codeInfo) {
        Map<String, String> map = new HashMap<>();
        for (String key : freemarkerProperties.getKeys()) {
            if (key.contains("xml")) {
                //xml需要转义后返回
                map.put(key, StringUtil.XMLEnc(buildCode(key, codeInfo)));
                continue;
            }
            map.put(key, buildCode(key, codeInfo));
        }
        return map;
    }

    public String buildCode(String template, CodeInfo codeInfo) {
        StringWriter stringWriter = new StringWriter();
        try (Writer out = new BufferedWriter(stringWriter)) {
            Template temp = fmConfig.getTemplate(template, "utf-8");
            temp.process(codeInfo, out);
            out.flush();
        } catch (IOException | TemplateException e) {
            log.error("生成代码异常:" + e.getMessage(), e);
            throw new MyRuntimeException(e);
        }
        return stringWriter.toString();
    }
}
