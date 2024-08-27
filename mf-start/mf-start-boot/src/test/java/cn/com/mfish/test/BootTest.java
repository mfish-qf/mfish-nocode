package cn.com.mfish.test;

import cn.com.mfish.common.core.utils.excel.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
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
        ExcelUtils.write("/excel/采购订单.xlsx", "采购订单","C:/Users/秋风/Desktop", map);
    }
}
