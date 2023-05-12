package cn.com.mfish.common.core.utils.excel;

import cn.com.mfish.common.core.utils.FileUtils;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @description: excel处理
 * @author: mfish
 * @date: 2023/5/12 19:40
 */
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
}
