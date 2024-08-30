package cn.com.mfish.common.code.common;

import cn.com.mfish.common.code.config.properties.FreemarkerProperties;
import cn.com.mfish.common.code.entity.CodeInfo;
import cn.com.mfish.common.code.entity.FieldExpand;
import cn.com.mfish.common.code.entity.SearchInfo;
import cn.com.mfish.common.code.entity.TableExpand;
import cn.com.mfish.common.code.req.ReqCode;
import cn.com.mfish.common.code.req.ReqSearch;
import cn.com.mfish.common.code.vo.CodeVo;
import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.entity.DefaultField;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.api.entity.FieldInfo;
import cn.com.mfish.sys.api.entity.TableInfo;
import cn.com.mfish.sys.api.remote.RemoteDbConnectService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author: mfish
 * @description: 代码生成工具类
 * @date: 2022/8/24 15:30
 */
@Component
@Slf4j
@RefreshScope
public class FreemarkerUtils {
    @Resource
    Configuration fmConfig;
    @Resource
    FreemarkerProperties freemarkerProperties;
    @Resource
    RemoteDbConnectService remoteDbConnectService;
    @Value("${code.savePath.front}")
    String frontPath;
    @Value("${code.savePath.back}")
    String backPath;

    /**
     * 初始化请求参数
     * 该方法用于在请求处理前初始化请求参数，确保所有必要的参数都已正确设置
     * 特别地，该方法会检查一些参数是否为空，如果为空则设置默认值，如果格式不正确则抛出异常
     * 此外，该方法还会根据参数的特定规则修改参数值，例如将下划线命名转换为驼峰命名
     *
     * @param reqCode 请求参数对象，包含数据库连接ID、表名、包名、接口前缀、实体类名和表注释等信息
     * @throws MyRuntimeException 如果关键参数为空或格式不正确，则抛出运行时异常
     */
    public void initReqCode(ReqCode reqCode) {
        if (StringUtils.isEmpty(reqCode.getConnectId())) {
            throw new MyRuntimeException("错误:库名不允许为空");
        }
        if (StringUtils.isEmpty(reqCode.getTableName())) {
            throw new MyRuntimeException("错误:表名不允许为空");
        }
        if (StringUtils.isEmpty(reqCode.getPackageName())) {
            reqCode.setPackageName("cn.com.mfish.web");
        } else if (!StringUtils.isMatch("^([A-Za-z]{1}[A-Za-z\\d_]*\\.?)+[A-Za-z][A-Za-z\\d_]*$", reqCode.getPackageName())) {
            throw new MyRuntimeException("错误:包名格式不规范");
        }
        if (StringUtils.isEmpty(reqCode.getApiPrefix())) {
            int index = reqCode.getPackageName().lastIndexOf(".");
            if (index >= 0) {
                reqCode.setApiPrefix(reqCode.getPackageName().substring(index + 1));
            } else {
                reqCode.setApiPrefix(reqCode.getPackageName());
            }
        } else if (!StringUtils.isMatch("^[A-Za-z]+[A-Za-z\\d_]*$", reqCode.getApiPrefix())) {
            throw new MyRuntimeException("错误:接口前缀格式不规范");
        } else {
            reqCode.setApiPrefix(reqCode.getApiPrefix().toLowerCase());
        }
        if (StringUtils.isEmpty(reqCode.getEntityName())) {
            reqCode.setEntityName(reqCode.getTableName());
        } else if (!StringUtils.isMatch("^[A-Za-z]+[A-Za-z\\d_]*$", reqCode.getEntityName())) {
            throw new MyRuntimeException("错误:实体类名格式不规范");
        }
        //如果entityName带下划线，转为首字母大写驼峰，否则其他不变首字母大写
        if (reqCode.getEntityName().indexOf("_") > 0) {
            reqCode.setEntityName(StringUtils.toCamelBigCase(reqCode.getEntityName()));
        } else {
            reqCode.setEntityName(StringUtils.firstUpperCase(reqCode.getEntityName()));
        }
        if (StringUtils.isEmpty(reqCode.getTableComment())) {
            reqCode.setTableComment(reqCode.getTableName());
            Result<PageResult<TableInfo>> result = remoteDbConnectService.getTableList(RPCConstants.INNER, reqCode.getConnectId(), reqCode.getTableName(), new ReqPage().setPageNum(1).setPageSize(10000));
            if (!result.isSuccess()) {
                throw new MyRuntimeException(result.getMsg());
            }
            if (result.getData() == null || result.getData().getList() == null || result.getData().getList().isEmpty()) {
                throw new MyRuntimeException("错误:未获取到表信息");
            }
            TableInfo tableInfo = result.getData().getList().getFirst();
            if (tableInfo != null && !StringUtils.isEmpty(tableInfo.getTableComment())) {
                reqCode.setTableComment(tableInfo.getTableComment());
            }
        }
    }

    /**
     * 获取代码
     *
     * @param reqCode 请求参数，用于指定需要生成代码的配置信息
     * @return 返回生成的代码列表
     * @throws MyRuntimeException 如果获取字段信息失败或未获取到字段信息，则抛出运行时异常
     */
    public List<CodeVo> getCode(ReqCode reqCode) {
        initReqCode(reqCode);
        CodeInfo codeInfo = new CodeInfo();
        codeInfo.setPackageName(reqCode.getPackageName());
        codeInfo.setEntityName(reqCode.getEntityName());
        codeInfo.setApiPrefix(reqCode.getApiPrefix());
        Result<PageResult<FieldInfo>> result = remoteDbConnectService.getFieldList(RPCConstants.INNER, reqCode.getConnectId(), reqCode.getTableName(), new ReqPage().setPageNum(1).setPageSize(10000));
        if (!result.isSuccess()) {
            throw new MyRuntimeException(result.getMsg());
        }
        if (result.getData() == null || result.getData().getList() == null || result.getData().getList().isEmpty()) {
            throw new MyRuntimeException("错误:未获取到字段信息");
        }
        List<FieldInfo> list = result.getData().getList();
        codeInfo.setSearchList(buildSearch(reqCode.getSearches(), list));
        String idType = "";
        List<FieldExpand> expandList = new ArrayList<>();
        //缺省字段字段不需要生成,获取列时过滤掉
        for (int i = 0; i < list.size(); i++) {
            FieldInfo fieldInfo = list.get(i);
            String fieldName = fieldInfo.getFieldName();
            if (StringUtils.isEmpty(fieldName)) {
                continue;
            }
            fieldName = fieldName.toLowerCase(Locale.ROOT);
            if (DefaultField.values.contains(fieldName)) {
                //设置唯一ID类型
                if ("id".equals(fieldName)) {
                    idType = fieldInfo.getType();
                }
                list.remove(i--);
                continue;
            }
            final String finalFieldName = fieldName;
            Optional<SearchInfo> searchInfo = codeInfo.getSearchList().stream().filter((search) ->
                            search.getFieldInfo().getFieldName().equals(finalFieldName) && search.getComponent() != null && search.getComponent().size() == 2)
                    .findFirst();
            fieldInfo.setFieldName(StringUtils.toCamelCase(fieldName));
            FieldExpand fieldExpand = new FieldExpand().setFieldInfo(fieldInfo);
            //设置字典组件列表
            searchInfo.ifPresent(info -> fieldExpand.setDictComponent(info.getComponent()));
            expandList.add(fieldExpand);
        }
        TableExpand tableExpand = new TableExpand();
        tableExpand.setColumns(list).setTableName(reqCode.getTableName())
                .setTableComment(reqCode.getTableComment()).setIdType(idType);
        codeInfo.setTableInfo(tableExpand.setFieldExpands(expandList));
        return getCode(codeInfo);
    }

    private List<SearchInfo> buildSearch(List<ReqSearch> list, List<FieldInfo> fields) {
        List<SearchInfo> searchInfos = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return searchInfos;
        }
        for (ReqSearch reqSearch : list) {
            Optional<FieldInfo> optional = fields.stream().filter((field) -> field.getFieldName().equals(reqSearch.getField())).findFirst();
            if (Optional.empty().equals(optional) || optional.isEmpty()) {
                continue;
            }
            SearchInfo searchInfo = new SearchInfo();
            searchInfo.setFieldInfo(optional.get())
                    .setField(StringUtils.toCamelBigCase(reqSearch.getField()))
                    .setCondition(reqSearch.getCondition())
                    .setComponent(reqSearch.getComponent());
            searchInfos.add(searchInfo);
        }
        return searchInfos;
    }

    /**
     * 获取生成的代码并返回前端
     *
     * @param codeInfo 代码参数信息
     * @return 返回生成的代码列表
     */
    public List<CodeVo> getCode(CodeInfo codeInfo) {
        List<CodeVo> list = new ArrayList<>();
        Map<String, String> regexMap = buildRegexMap(codeInfo);
        for (String key : freemarkerProperties.getKeys()) {
            CodeVo codeVo = new CodeVo();
            String path = replaceVariable(key, regexMap);
            int index = path.lastIndexOf("/");
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
     * @param codeInfo 编码信息
     * @return 返回构建的正则替换Map
     */
    private Map<String, String> buildRegexMap(CodeInfo codeInfo) {
        Map<String, String> map = new HashMap<>();
        map.put("\\$\\{entityName\\#?(?<param>.*?)}", codeInfo.getEntityName());
        map.put("\\$\\{apiPrefix\\#?(?<param>.*?)}", codeInfo.getApiPrefix());
        String path = codeInfo.getPackageName().replace(".", "/");
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        map.put("\\$\\{packageName\\#?(?<param>.*?)}", path);
        return map;
    }

    /**
     * 参数替换
     * 根据提供的映射表，对字符串中的变量进行替换
     *
     * @param key 待处理的原始字符串，可能包含待替换的变量
     * @param mapValue 包含变量和其对应值的映射表
     * @return 替换变量后的字符串
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
     * 本函数通过Freemarker模版引擎，将给定的模版和数据信息结合，生成最终的代码文本
     *
     * @param template 模版文件路径，用于指定生成代码所需的模版
     * @param codeInfo CodeInfo对象，包含生成代码所需的数据和信息
     * @return 返回生成的代码文本字符串
     */
    public String buildCode(String template, CodeInfo codeInfo) {
        StringWriter stringWriter = new StringWriter();
        try (Writer out = new BufferedWriter(stringWriter)) {
            Template temp = fmConfig.getTemplate(template, "utf-8");
            temp.process(codeInfo, out);
            out.flush();
        } catch (IOException | TemplateException e) {
            log.error("生成代码异常:{}", e.getMessage(), e);
            throw new MyRuntimeException(e);
        }
        return stringWriter.toString();
    }

    /**
     * 保存代码
     *
     * @param reqCode 代码参数，包含待保存代码的信息
     * @return 保存操作是否成功
     */
    public boolean saveCode(ReqCode reqCode) {
        List<CodeVo> list = getCode(reqCode);
        List<CodeVo> listBack = list.stream().filter((codeVo -> codeVo.getPath().startsWith("src/main/java"))).toList();
        List<CodeVo> listFront = list.stream().filter((codeVo -> !codeVo.getPath().startsWith("src/main/java"))).toList();
        return saveCode(listBack, backPath) && saveCode(listFront, frontPath);

    }

    /**
     * 保存代码到本地
     *
     * @param list 代码列表，包含要保存的代码信息
     * @param savePath 保存路径，用于指定代码保存的位置
     * @return boolean 表示保存是否成功
     */
    public boolean saveCode(List<CodeVo> list, String savePath) {
        savePath = savePath.replace("\\", "/");
        if (StringUtils.isEmpty(savePath)) {
            savePath = "/mfish/";
        } else if (!savePath.endsWith("/")) {
            savePath += "/";
        }
        for (CodeVo code : list) {
            try {
                File file = new File(savePath + "/" + code.getPath(), code.getName());
                FileUtils.writeStringToFile(file, code.getCode(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.error("错误:文件保存异常", e);
            }
        }
        //压缩样例，暂不压缩
        //ZipUtils.toZip(path, true);
        return true;
    }

    /**
     * 下载代码
     *
     * @param reqCode 请求参数，用于指定下载代码的条件
     * @return 返回一个字节数组，包含压缩后的代码
     * <p>
     * 该方法根据请求参数从指定位置获取代码文件，将这些代码文件打包成一个压缩文件（.zip格式）并返回。
     * 使用 ByteArrayOutputStream 和 ZipOutputStream 来创建压缩文件流，遍历代码文件列表，将每个文件
     * 的内容写入到压缩流中。完成后，关闭所有流并返回压缩文件的字节数组表示。
     */
    public byte[] downloadCode(ReqCode reqCode) {
        List<CodeVo> list = getCode(reqCode);
        ZipOutputStream zos;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            zos = new ZipOutputStream(outputStream);
            for (CodeVo codeVo : list) {
                zos.putNextEntry(new ZipEntry(codeVo.getPath() + "/" + codeVo.getName()));
                IOUtils.write(codeVo.getCode(), zos, StandardCharsets.UTF_8);
                zos.flush();
                zos.closeEntry();
            }
            //需要主动关闭后，再返回outputStream否则返回文件不全
            IOUtils.closeQuietly(zos);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new MyRuntimeException("错误:下载代码失败");
        }
    }
}