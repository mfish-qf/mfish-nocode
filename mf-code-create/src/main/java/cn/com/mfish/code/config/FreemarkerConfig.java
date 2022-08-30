package cn.com.mfish.code.config;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import freemarker.cache.StringTemplateLoader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：qiufeng
 * @description：freemark配置类
 * @date ：2022/8/24 15:22
 */
@Configuration
@Slf4j
@Data
public class FreemarkerConfig {

    @Resource
    FreemarkerProperties properties;

    /**
     * 初始化freemarker配置
     *
     * @return
     */
    @Lazy
    @Bean(name = "fmConfig")
    public freemarker.template.Configuration initConfig() {
        freemarker.template.Configuration config = new freemarker.template.Configuration(properties.getVersion());
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        List<File> list = getFiles();
        for (File file : list) {
            String parent = file.getParent();
            int index = parent.lastIndexOf(properties.getPath());
            //使用模版路径作为模版KEY值
            String pName = parent.replace("\\", "/").substring(index + properties.getPath().length() + 1);
            stringTemplateLoader.putTemplate(pName, getTemplate(file));
        }
        config.setTemplateLoader(stringTemplateLoader);
        return config;
    }

    /**
     * 通过模版文件获取模版信息
     *
     * @param file
     * @return
     */
    private String getTemplate(File file) {
        StringBuffer sb = new StringBuffer();
        try (FileInputStream stream = new FileInputStream(file);
             BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(System.getProperty("line.separator"));// 保持原有换行格式
            }
        } catch (IOException e) {
            log.error("读取模版异常:" + e.getMessage(), e);
            throw new MyRuntimeException(e);
        }
        return sb.toString();
    }

    /**
     * 获取模版目录下所有模版文件
     *
     * @return
     * @throws FileNotFoundException
     */
    private List<File> getFiles() {
        List<File> list = new ArrayList<>();
        try {
            getFiles(ResourceUtils.getFile("classpath:" + properties.getPath()), list);
        } catch (FileNotFoundException e) {
            log.error("获取文件模版异常:" + e.getMessage(), e);
            throw new MyRuntimeException(e);
        }
        return list;
    }

    /**
     * 递归获取目录下所有文件
     *
     * @param file
     * @param list
     */
    private void getFiles(File file, List<File> list) {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            list.add(file);
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                getFiles(f, list);
            }
        }
    }
}
