package cn.com.mfish.code.dialect;

import cn.com.mfish.common.dblink.enums.DBType;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 代码构建适配不同数据库
 * @author: mfish
 * @date: 2023/3/23 19:58
 */
public class CodeBuildAdapter {
    private CodeBuildAdapter() {

    }

    private static Map<DBType, CodeBuild> dialectMap = new HashMap<>();

    static {
        dialectMap.put(DBType.mysql, new MysqlCodeBuild());
    }

    public static CodeBuild getCodeBuild(DBType dbType) {
        return dialectMap.get(dbType);
    }
}
