package cn.com.mfish.test;

import cn.com.mfish.common.core.utils.excel.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 单实例测试类
 * @author: mfish
 * @date: 2024/6/20
 */
@Slf4j
@SpringBootTest
@ComponentScan(basePackages = "cn.com.mfish")
@RunWith(SpringRunner.class)
public class BootTest {
    @Test
    public void excelExport() throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("traderName", "刘德华有限公司");
        map.put("orderCode", "No88888888");
        map.put("signDate", "2024-06-20 15:33:11");
        map.put("totalAmount", 90);
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("num", 1);
        map1.put("materialName", "物料1");
        map1.put("standard", "规格1");
        map1.put("quantity", 10);
        map1.put("price", 5);
        map1.put("amount", 50);
        list.add(map1);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("num", 2);
        map2.put("materialName", "物料2");
        map2.put("standard", "规格2");
        map2.put("quantity", 8);
        map2.put("price", 5);
        map2.put("amount", 40);
        list.add(map2);
        map.put("detail", list);
        ExcelUtils.write("/excel/采购订单.xlsx", "采购订单", "C:/Users/秋风/Desktop", map);
    }

    @Test
    public void downloadRemoteFile() throws IOException {
        URL url = new URL("http://localhost:8888/storage/file/24b9a3000aaa461b8544ce3504133fe8.xlsx?access_token=aad48e1595ab44af818c1ed087188cc9");
        try (InputStream in = url.openStream();
             FileOutputStream out = new FileOutputStream("C:\\Users\\秋风\\Desktop\\24b9a3000aaa461b8544ce3504133fe8.xlsx")) {
            byte[] buffer = new byte[4096];
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
        }
    }

    @Test
    public String downloadAndUpload(String fileUrl, String uploadUrl) throws Exception {
        // 第一步：下载远程文件到内存
        URL url = new URL("http://localhost:8888/storage/file/24b9a3000aaa461b8544ce3504133fe8.xlsx?access_token=aad48e1595ab44af818c1ed087188cc9");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(10000);

        String fileName = getFileNameFromUrl(fileUrl); // 自定义解析文件名方法
        byte[] fileBytes;
        try (InputStream is = conn.getInputStream()) {
            fileBytes = is.readAllBytes();
        }

        // 第二步：构造 multipart/form-data 请求体上传到另一个接口
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource resource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return fileName;  // 必须设置文件名，否则服务端可能接收不到
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource); // 参数名根据后端 B 要求改，如 file、uploadFile 等

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uploadUrl, requestEntity, String.class);
        return response.getBody();
    }

    private String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}
