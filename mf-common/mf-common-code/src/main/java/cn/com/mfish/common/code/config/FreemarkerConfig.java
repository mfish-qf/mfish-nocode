package cn.com.mfish.common.code.config;

import cn.com.mfish.common.code.config.properties.FreemarkerProperties;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import freemarker.cache.StringTemplateLoader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.Resource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: mfish
 * @description: freemark配置类
 * @date: 2022/8/24 15:22
 */
@Configuration
@Slf4j
@Data
public class FreemarkerConfig {

    @Resource
    FreemarkerProperties properties;

    /**
     * 初始化Freemarker配置
     * <p>
     * 本方法负责配置Freemarker模板引擎，包括设置模板加载器等
     *
     * @return freemarker.template.Configuration 返回配置好的Freemarker配置实例
     */
    @Lazy
    @Bean(name = "fmConfig")
    public freemarker.template.Configuration initConfig() {
        freemarker.template.Configuration config = new freemarker.template.Configuration(properties.getVersion());
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        Map<String, InputStream> map = getFiles();
        for (Map.Entry<String, InputStream> entry : map.entrySet()) {
            stringTemplateLoader.putTemplate(entry.getKey(), getTemplate(entry.getValue()));
        }
        config.setTemplateLoader(stringTemplateLoader);
        return config;
    }

    /**
     * 通过模版文件获取模版信息
     * 本方法通过输入流读取模版文件内容，主要用于动态加载模版，避免硬编码模版字符串
     *
     * @param inputStream 模版文件的输入流，解释了模版的结构和内容
     * @return 模版信息的字符串表示
     */
    private String getTemplate(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());// 保持原有换行格式
            }
        } catch (IOException e) {
            log.error("读取模版异常:{}", e.getMessage(), e);
            throw new MyRuntimeException(e);
        }
        return sb.toString();
    }

    /**
     * 获取模版目录下所有模版文件
     *
     * @return 包含所有模版文件的映射，键为模版文件名，值为文件输入流
     */
    private Map<String, InputStream> getFiles() {
        Map<String, InputStream> map = new HashMap<>();
        try {
            for (String key : properties.getKeys()) {
                ClassPathResource cpr = new ClassPathResource(properties.getPath() + "/" + key);
                map.put(key, cpr.getInputStream());
            }
        } catch (IOException e) {
            log.error("获取文件模版异常:{}", e.getMessage(), e);
            throw new MyRuntimeException(e);
        }
        return map;
    }
}
