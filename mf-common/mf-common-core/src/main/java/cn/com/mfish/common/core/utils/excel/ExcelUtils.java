package cn.com.mfish.common.core.utils.excel;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.FileUtils;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @description: excel处理
 * @author: mfish
 * @date: 2023/5/12 19:40
 */
@Slf4j
public class ExcelUtils {
    private static final String SUFFIX = ".xlsx";

    private ExcelUtils() {
    }

    /**
     * excel导出下载
     *
     * @param fileName 文件名称（不带后缀默认为xlsx）
     * @param list     数据
     * @throws IOException IO异常
     */
    public static <T> void write(String fileName, List<T> list) throws IOException {
        write(fileName, list, null);
    }

    /**
     * excel 导出下载（无数据时excel可展示列头）
     *
     * @param fileName 文件名称（不带后缀默认为xlsx）
     * @param list     数据
     * @param cls      泛型类型
     * @param <T>      泛型
     * @throws IOException IO异常
     */
    public static <T> void write(String fileName, List<T> list, Class<T> cls) throws IOException {
        //swagger调用会用问题，使用postman测试
        HttpServletResponse response = ServletUtils.getResponse();
        try {
            assert response != null;
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            if (StringUtils.isEmpty(fileName)) {
                fileName = "export";
            } else {
                fileName = FileUtils.encodeFileName(fileName);
            }
            response.setHeader("Content-disposition", "attachment;filename*=utf-8'zh_cn'" + fileName + SUFFIX);
            if (list == null || list.isEmpty()) {
                if (cls != null) {
                    list = new ArrayList<>();
                    list.add(cls.getDeclaredConstructor().newInstance());
                } else {
                    IOUtils.write(new byte[]{}, response.getOutputStream());
                    return;
                }
            }
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream(), list.get(0).getClass())
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .autoCloseStream(Boolean.FALSE).sheet("sheet1")
                    .doWrite(list);
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().println(JSON.toJSONString(Result.fail(null, "错误:下载文件失败")));
        }
    }

    /**
     * 模板文件本地写入
     * 注意：如果导出报create workbook failure可能是由于文件模板为xls格式，请先另存为xlsx类型
     *
     * @param templatePath 文件模板路径
     * @param filePath     文件路径（不包含名称）
     * @param fileName     文件名称（不包含后缀默认为xlsx）
     * @param map          数据
     * @throws IOException
     */
    public static void write(String templatePath, String fileName, String filePath, Map<String, Object> map) throws IOException {
        ExcelWriter excelWriter = EasyExcel.write(filePath + "/" + fileName + SUFFIX).withTemplate(new ClassPathResource(templatePath).getInputStream()).build();
        write(excelWriter, map);
    }

    /**
     * 模板文件流写入
     * 注意：如果导出报create workbook failure可能是由于文件模板为xls格式，请先另存为xlsx类型
     *
     * @param templatePath 文件模板路径
     * @param outputStream 文件流
     * @param map          数据
     * @throws IOException
     */
    public static void write(String templatePath, OutputStream outputStream, Map<String, Object> map) throws IOException {
        ExcelWriter excelWriter = EasyExcel.write(outputStream).withTemplate(new ClassPathResource(templatePath).getInputStream()).build();
        write(excelWriter, map);
    }

    /**
     * 模板文件写入
     * 注意：如果导出报create workbook failure可能是由于文件模板为xls格式，请先另存为xlsx类型
     *
     * @param excelWriter excel写入
     * @param map         数据
     */
    public static void write(ExcelWriter excelWriter, Map<String, Object> map) {
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        // 这里注意 入参用了forceNewRow 代表在写入list的时候不管list下面有没有空行 都会创建一行，然后下面的数据往后移动。默认 是false，会直接使用下一行，如果没有则创建。
        // forceNewRow 如果设置了true,有个缺点 就是他会把所有的数据都放到内存了，所以慎用
        // 简单的说 如果你的模板有list,且list不是最后一行，下面还有数据需要填充 就必须设置 forceNewRow=true 但是这个就会把所有数据放到内存 会很耗内存
        // 如果数据量大 list不是最后一行 参照下一个
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        try {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof List) {
                    excelWriter.fill(new FillWrapper(entry.getKey(), (List) entry.getValue()), fillConfig, writeSheet);
                }
            }
            excelWriter.fill(map, writeSheet);
        } finally {
            excelWriter.finish();
        }
    }

    /**
     * 模板文件web中的写入
     * 注意：如果导出报create workbook failure可能是由于文件模板为xls格式，请先另存为xlsx类型
     *
     * @param templatePath 模板路径
     * @param fileName     文件名称（不包含后缀默认为xlsx）
     * @param map          数据
     * @throws IOException
     */
    public static void write(String templatePath, String fileName, Map<String, Object> map) throws IOException {
        if (StringUtils.isEmpty(fileName)) {
            int index = templatePath.replace("\\", "/").lastIndexOf("/");
            if (index > 0) {
                fileName = FileUtils.encodeFileName(templatePath.substring(index + 1));
            } else {
                fileName = "export";
            }
        } else {
            fileName = FileUtils.encodeFileName(fileName);
        }
        HttpServletResponse response = ServletUtils.getResponse();
        try {
            assert response != null;
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8'zh_cn'" + fileName + SUFFIX);
            write(templatePath, response.getOutputStream(), map);
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().println(JSON.toJSONString(Result.fail(null, "错误:下载文件失败")));
        }
    }

    /**
     * 读取excel头
     *
     * @param filePath 文件路径
     * @return
     */
    public static LinkedHashMap<Integer, String> readHeader(String filePath) {
        File file = new File(filePath);
        return readHeader(file);
    }

    /**
     * 读取excel头
     *
     * @param file 文件
     * @return
     */
    public static LinkedHashMap<Integer, String> readHeader(File file) {
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            return readHeader(inputStream);
        } catch (FileNotFoundException e) {
            log.error("错误:读取头失败", e);
            throw new MyRuntimeException(e);
        }
    }

    /**
     * 读取excel头
     *
     * @param stream 文件流
     * @return
     */
    public static LinkedHashMap<Integer, String> readHeader(InputStream stream) {
        try {
            LinkedHashMap<Integer, String> listMap = new LinkedHashMap<>();
            Consumer<Map<Integer, String>> consumer = listMap::putAll;
            HeadReadListener listener = new HeadReadListener(consumer);
            EasyExcel.read(stream, listener).sheet().head(new ArrayList<>()).doRead();
            return listMap;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                log.error("文件流关闭异常", e);
            }
        }
    }

    /**
     * 通用分页读取excel数据（excel必须一行为行头）
     *
     * @param filePath 文件路径
     * @param reqPage  分页
     * @return
     */
    public static PageResult<Map<String, String>> read(String filePath, ReqPage reqPage) {
        File file = new File(filePath);
        return read(file, reqPage);
    }

    /**
     * 通用分页读取excel数据（excel必须一行为行头）
     *
     * @param file    文件
     * @param reqPage 分页
     * @return
     */
    public static PageResult<Map<String, String>> read(File file, ReqPage reqPage) {
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            return read(inputStream, reqPage);
        } catch (FileNotFoundException e) {
            log.error("错误:读取头失败", e);
            throw new MyRuntimeException(e);
        }
    }

    /**
     * 通用分页读取excel数据（excel必须一行为行头）
     *
     * @param stream  文件流
     * @param reqPage 分页
     * @return
     */
    public static PageResult<Map<String, String>> read(InputStream stream, ReqPage reqPage) {
        try {
            List<Map<String, String>> listMap = new ArrayList<>();
            Consumer<List<Map<String, String>>> consumer = listMap::addAll;
            PageHelperReadListener listener = new PageHelperReadListener(reqPage.getPageSize(), consumer);
            int start = reqPage.getPageNum() > 0 ? (reqPage.getPageNum() - 1) * 10 + 1 : 1;
            EasyExcel.read(stream, listener).sheet().headRowNumber(start).head(new ArrayList<>()).doRead();
            return new PageResult<>(listMap, reqPage.getPageNum(), reqPage.getPageSize(), listener.getTotal());
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                log.error("文件流关闭异常", e);
            }
        }
    }

    /**
     * 通用读取excel数据（excel必须一行为行头）
     *
     * @param filePath 文件路径
     * @return
     */
    public static List<Map<String, String>> read(String filePath) {
        File file = new File(filePath);
        return read(file);
    }

    /**
     * 通用读取excel数据（excel必须一行为行头）
     *
     * @param file 文件
     * @return
     */
    public static List<Map<String, String>> read(File file) {
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            return read(inputStream);
        } catch (FileNotFoundException e) {
            log.error("错误:读取头失败", e);
            throw new MyRuntimeException(e);
        }
    }

    /**
     * 通用读取excel数据（excel必须一行为行头）
     *
     * @param stream 文件流
     * @return
     */
    public static List<Map<String, String>> read(InputStream stream) {
        try {
            List<Map<String, String>> listMap = new ArrayList<>();
            Consumer<List<Map<String, String>>> consumer = listMap::addAll;
            PageHelperReadListener listener = new PageHelperReadListener(consumer);
            EasyExcel.read(stream, listener).sheet().head(new ArrayList<>()).doRead();
            return listMap;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                log.error("文件流关闭异常", e);
            }
        }
    }
}