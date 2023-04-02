package cn.com.mfish.common.dblink.page.dialect;

import cn.com.mfish.common.dblink.query.BaseQuery;

/**
 * @description: PG相关方言
 * @author: mfish
 * @date: 2023/3/21 23:04
 */
public class PostgreDialect extends MysqlDialect {

    public PostgreDialect(BaseQuery baseQuery) {
        super(baseQuery);
    }
}