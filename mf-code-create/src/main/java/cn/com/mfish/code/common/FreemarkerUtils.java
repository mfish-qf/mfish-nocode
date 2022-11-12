package cn.com.mfish.code.common;

import cn.com.mfish.code.config.FreemarkerProperties;
import cn.com.mfish.code.entity.CodeInfo;
import cn.com.mfish.code.entity.FieldInfo;
import cn.com.mfish.code.entity.TableInfo;
import cn.com.mfish.code.service.MysqlTableService;
import cn.com.mfish.code.vo.CodeVo;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    @Value("${code.savePath}")
    String savePath;

    public List<CodeVo> getCode(String schema, String tableName) {
        return getCode(schema, tableName, "cn.com.mfish.code");
    }

    /**
     * @param schema      库名
     * @param tableName   表名
     * @param packageName 项目包名
     * @return
     */
    public List<CodeVo> getCode(String schema, String tableName, String packageName) {
        TableInfo tableInfo = mysqlTableService.getTableInfo(schema, tableName);
        String tableComment = "";
        if (tableInfo != null) {
            tableComment = StringUtils.isEmpty(tableInfo.getTableComment()) ? "" : tableInfo.getTableComment();
        }
        return getCode(schema, tableName, tableComment, packageName);
    }

    /**
     * @param schema       库名
     * @param tableName    表名
     * @param tableComment 表描述
     * @param packageName  项目包名
     * @return
     */
    public List<CodeVo> getCode(String schema, String tableName, String tableComment, String packageName) {
        return getCode(schema, tableName, tableComment, packageName, null);
    }

    /**
     * @param schema       库名
     * @param tableName    表名
     * @param tableComment 表描述
     * @param packageName  项目包名
     * @param entityName   entity属性名
     * @return
     */
    public List<CodeVo> getCode(String schema, String tableName, String tableComment, String packageName, String entityName) {
        CodeInfo codeInfo = new CodeInfo();
        codeInfo.setPackageName(packageName);
        if (StringUtils.isEmpty(entityName)) {
            entityName = StringUtils.toCamelBigCase(tableName);
        }
        codeInfo.setEntityName(entityName);
        List<FieldInfo> list = mysqlTableService.getColumns(schema, tableName);
        String idType = "";
        //缺省字段字段不需要生成,获取列时过滤掉
        for (int i = 0; i < list.size(); i++) {
            FieldInfo fieldInfo = list.get(i);
            String fieldName = fieldInfo.getFieldName().toLowerCase(Locale.ROOT);
            if (DefaultField.values.contains(fieldName)) {
                //设置唯一ID类型
                if ("id".equals(fieldName)) {
                    idType = fieldInfo.getType();
                }
                list.remove(i--);
                continue;
            }
            fieldInfo.setFieldName(StringUtils.toCamelCase(fieldName));
        }
        codeInfo.setTableInfo(new TableInfo().setColumns(list).setTableName(tableName).setTableComment(tableComment).setIdType(idType));
        return getCode(codeInfo);
    }

    /**
     * 获取生成的代码并返回前端
     *
     * @param codeInfo 代码参数信息
     * @return
     */
    public List<CodeVo> getCode(CodeInfo codeInfo) {
        List<CodeVo> list = new ArrayList<>();
        for (String key : freemarkerProperties.getKeys()) {
            CodeVo codeVo = new CodeVo();
            String tempName = freemarkerProperties.getTemplateName().get(key);
            tempName = tempName.replace(".ftl", "")
                    .replace("${entityName}", codeInfo.getEntityName());
            codeVo.setName(tempName).setPath(key);
//            if (key.contains("xml")) {
//                //xml需要转义后返回
//                codeVo.setCode(StringUtil.XMLEnc(buildCode(key, codeInfo)));
//                continue;
//            }
            codeVo.setCode(buildCode(key, codeInfo));
            list.add(codeVo);
        }
        return list;
    }

    /**
     * 模版代码构建
     *
     * @param template
     * @param codeInfo
     * @return
     */
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

    /**
     * 保存代码到本地
     *
     * @param list
     * @return
     */
    public boolean saveCode(List<CodeVo> list) {
        savePath = savePath.replace("\\", "/");
        if (StringUtils.isEmpty(savePath)) {
            savePath = "/mfish/";
        } else if (!savePath.endsWith("/")) {
            savePath += "/";
        }
        for (CodeVo code : list) {
            try {
                FileUtils.writeStringToFile(new File(savePath + code.getPath(), code.getName())
                        , code.getCode(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.error("错误:文件保存异常", e);
            }
        }
        return true;
    }
}
