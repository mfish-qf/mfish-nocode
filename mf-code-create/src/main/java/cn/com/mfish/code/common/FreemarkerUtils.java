package cn.com.mfish.code.common;

import cn.com.mfish.code.config.FreemarkerProperties;
import cn.com.mfish.code.entity.CodeInfo;
import cn.com.mfish.code.entity.FieldInfo;
import cn.com.mfish.code.entity.TableInfo;
import cn.com.mfish.code.req.ReqCode;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: mfish
 * @description：代码生成工具类
 * @date: 2022/8/24 15:30
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

    /**
     * 初始化请求参数
     *
     * @param reqCode
     * @return
     */
    private ReqCode initReqCode(ReqCode reqCode) {
        if (StringUtils.isEmpty(reqCode.getSchema())) {
            throw new MyRuntimeException("错误:库名不允许为空");
        }
        if (StringUtils.isEmpty(reqCode.getTableName())) {
            throw new MyRuntimeException("错误:表名不允许为空");
        }
        if (StringUtils.isEmpty(reqCode.getPackageName())) {
            reqCode.setPackageName("cn.com.mfish.web");
        }
        if (StringUtils.isEmpty(reqCode.getTableComment())) {
            reqCode.setTableComment(reqCode.getTableName());
            TableInfo tableInfo = mysqlTableService.getTableInfo(reqCode.getSchema(), reqCode.getTableName());
            if (tableInfo != null && !StringUtils.isEmpty(tableInfo.getTableComment())) {
                reqCode.setTableComment(tableInfo.getTableComment());
            }
        }
        if (StringUtils.isEmpty(reqCode.getApiPrefix())) {
            int index = reqCode.getPackageName().lastIndexOf(".");
            if (index >= 0) {
                reqCode.setApiPrefix(reqCode.getPackageName().substring(index + 1));
            } else {
                reqCode.setApiPrefix(reqCode.getPackageName());
            }
        }
        if (StringUtils.isEmpty(reqCode.getEntityName())) {
            reqCode.setEntityName(reqCode.getTableName());
        }
        reqCode.setEntityName(StringUtils.toCamelBigCase(reqCode.getEntityName()));
        return reqCode;
    }

    /**
     * 获取代码
     *
     * @param reqCode
     * @return
     */
    public List<CodeVo> getCode(ReqCode reqCode) {
        initReqCode(reqCode);
        CodeInfo codeInfo = new CodeInfo();
        codeInfo.setPackageName(reqCode.getPackageName());
        codeInfo.setEntityName(reqCode.getEntityName());
        codeInfo.setApiPrefix(reqCode.getApiPrefix());
        List<FieldInfo> list = mysqlTableService.getColumns(reqCode.getSchema(), reqCode.getTableName());
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
        codeInfo.setTableInfo(new TableInfo().setColumns(list).setTableName(reqCode.getTableName()).setTableComment(reqCode.getTableComment()).setIdType(idType));
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
        Map<String, String> regexMap = buildRegexMap(codeInfo);
        for (String key : freemarkerProperties.getKeys()) {
            CodeVo codeVo = new CodeVo();
            String path = replaceVariable(key, regexMap);
            int index = path.lastIndexOf("/");
            if (path.length() <= index) {
                continue;
            }
            String tempName = path.substring(index + 1).replace(".ftl", "");
            codeVo.setName(tempName).setPath(path.substring(0, index));
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
     * 构建正则替换Map
     *
     * @param codeInfo
     * @return
     */
    private Map<String, String> buildRegexMap(CodeInfo codeInfo) {
        Map<String, String> map = new HashMap<>();
        map.put("\\$\\{entityName\\#?(?<param>.*?)}", codeInfo.getEntityName());
        map.put("\\$\\{apiPrefix\\#?(?<param>.*?)}", codeInfo.getApiPrefix());
        String path = codeInfo.getPackageName().replace(".", "/");
        if (!path.endsWith("/")) {
            path += "/";
        }
        map.put("\\$\\{packageName\\#?(?<param>.*?)}", path);
        return map;
    }

    /**
     * 参数替换
     *
     * @param key
     * @return
     */
    private String replaceVariable(String key, Map<String, String> mapValue) {
        for (Map.Entry<String, String> kv : mapValue.entrySet()) {
            Pattern pattern = Pattern.compile(kv.getKey());
            Matcher matcher = pattern.matcher(key);
            while (matcher.find()) {
                String value = matcher.group();
                if (StringUtils.isEmpty(value)) {
                    continue;
                }
                int index = value.indexOf("#");
                if (index <= 0) {
                    key = key.replace(value, MatchType.不处理.dealVariable(kv.getValue()));
                    continue;
                }
                key = key.replace(value, MatchType.getMatchType(value.substring(index + 1).replace("}", "")).dealVariable(kv.getValue()));
            }
        }
        return key;
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
