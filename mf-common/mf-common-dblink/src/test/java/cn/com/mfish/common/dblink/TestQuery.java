package cn.com.mfish.common.dblink;

import cn.com.mfish.common.dblink.datatable.MetaDataTable;
import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.common.dblink.enums.DBType;
import cn.com.mfish.common.dblink.enums.PoolType;
import cn.com.mfish.common.dblink.query.BaseQuery;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

/**
 * @description: 测试查询
 * @author: mfish
 * @date: 2023/3/21
 */
public class TestQuery {
    @Test
    public void query() {
        DataSourceOptions options = new DataSourceOptions();
        options.setUser("root");
        options.setPassword("123456");
        options.setJdbcUrl("jdbc:mysql://localhost:3306/mf_oauth?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8");
        options.setDbType(DBType.mysql);
        options.setPoolType(PoolType.Hikari);
        BaseQuery query = new BaseQuery(options);
        MetaDataTable table = query.query("select * from sso_menu");
        System.out.println(JSON.toJSONString(table));
    }
}
