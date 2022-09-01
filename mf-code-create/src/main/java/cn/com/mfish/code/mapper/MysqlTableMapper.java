package cn.com.mfish.code.mapper;

import cn.com.mfish.code.entity.FieldInfo;
import cn.com.mfish.code.entity.TableInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ：qiufeng
 * @description：mysql数据库表
 * @date ：2022/8/31 22:27
 */
public interface MysqlTableMapper {
    TableInfo getTableInfo(@Param("schema") String schema,@Param("tableName") String tableName);
    List<FieldInfo> getColumns(@Param("schema") String schema,@Param("tableName") String tableName);
}
