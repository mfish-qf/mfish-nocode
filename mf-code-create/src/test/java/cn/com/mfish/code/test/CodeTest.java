package cn.com.mfish.code.test;

import cn.com.mfish.code.common.FreemarkerUtils;
import cn.com.mfish.code.entity.CodeInfo;
import cn.com.mfish.code.entity.FieldInfo;
import cn.com.mfish.code.entity.TableInfo;
import cn.com.mfish.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: mfish
 * @description: 代码生成测试
 * @date: 2022/8/24 16:02
 */
@Slf4j
@SpringBootTest
@ComponentScan(basePackages = "cn.com.mfish.code")
public class CodeTest {
    @Resource
    FreemarkerUtils freemarkerUtils;

    @Test
    public void testBuildCode() {
        CodeInfo codeInfo = new CodeInfo();
        codeInfo.setPackageName("cn.com.mfish.code");
        codeInfo.setEntityName(StringUtils.toCamelBigCase("test_code"));
        List<FieldInfo> list = new ArrayList<>();
        list.add(new FieldInfo().setFieldName("F1").setDbType("VARCHAR").setType("String").setComment("字段1").setIsPrimary(true));
        list.add(new FieldInfo().setFieldName("F2").setDbType("INT").setType("Integer").setComment("字段2"));
        list.add(new FieldInfo().setFieldName("F3").setDbType("INT").setType("Integer").setComment("字段3"));
        codeInfo.setTableInfo(new TableInfo().setTableName("test_table").setTableComment("测试").setColumns(list));
        String aaa = freemarkerUtils.buildCode("entity", codeInfo);
        System.out.println(aaa);
    }
    @Test
    public void testGetCode() {
        CodeInfo codeInfo = new CodeInfo();
        codeInfo.setPackageName("cn.com.mfish.code");
        codeInfo.setEntityName(StringUtils.toCamelBigCase("test_code"));
        List<FieldInfo> list = new ArrayList<>();
        list.add(new FieldInfo().setFieldName("F1").setDbType("VARCHAR").setType("String").setComment("字段1").setIsPrimary(true));
        list.add(new FieldInfo().setFieldName("F2").setDbType("INT").setType("Integer").setComment("字段2"));
        list.add(new FieldInfo().setFieldName("F3").setDbType("INT").setType("Integer").setComment("字段3"));
        codeInfo.setTableInfo(new TableInfo().setTableName("test_table").setTableComment("测试").setColumns(list));
        freemarkerUtils.getCode(codeInfo);
    }

}
