package cn.com.mfish.common.dblink;

import cn.com.mfish.common.core.enums.DataType;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.dataset.datatable.MetaDataRow;
import cn.com.mfish.common.dataset.datatable.MetaDataTable;
import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.common.dblink.entity.QueryParam;
import cn.com.mfish.common.dblink.enums.DBType;
import cn.com.mfish.common.dblink.enums.PoolType;
import cn.com.mfish.common.dblink.page.MfPageHelper;
import cn.com.mfish.common.dblink.query.QueryHandler;
import com.alibaba.fastjson2.JSON;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
        MetaDataTable table = QueryHandler.query(options, "select * from sso_menu");
        System.out.println(JSON.toJSONString(table.getColHeaders()));
        System.out.println(JSON.toJSONString(table));
    }

    @Test
    public void pageQuery() {
        DataSourceOptions options = new DataSourceOptions();
        options.setUser("root");
        options.setPassword("123456");
        options.setJdbcUrl("jdbc:mysql://localhost:3306/mf_oauth?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8");
        options.setDbType(DBType.mysql);
        options.setPoolType(PoolType.Hikari);
        MfPageHelper.startPage(1, 10);
        PageResult<MetaDataRow> table = new PageResult<>(QueryHandler.query(options, "select * from sso_menu"));
        System.out.println(JSON.toJSONString(table));
    }

    @Test
    public void pageQueryParams() {
        DataSourceOptions options = new DataSourceOptions();
        options.setUser("root");
        options.setPassword("123456");
        options.setJdbcUrl("jdbc:mysql://localhost:3306/mf_oauth?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8");
        options.setDbType(DBType.mysql);
        options.setPoolType(PoolType.Hikari);
        MfPageHelper.startPage(1, 10);
        List<QueryParam> params = new ArrayList<>();
        params.add(new QueryParam().setValue("聊天").setType(DataType.STRING));
        PageResult<MetaDataRow> table = new PageResult<>(QueryHandler.query(options, "select * from sso_menu where menu_name=?", params));
        System.out.println(JSON.toJSONString(table));
    }

}
