package cn.com.mfish.common.core.utils.excel;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.FileUtils;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
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
            response.setHeader("Content-disposition", "attachment;filename*=utf-8'zh_cn'" + fileName + ".xlsx");
            if (list == null || list.isEmpty()) {
                IOUtils.write(new byte[]{}, response.getOutputStream());
                return;
            }
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream(), list.get(0).getClass()).autoCloseStream(Boolean.FALSE).sheet("sheet1")
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