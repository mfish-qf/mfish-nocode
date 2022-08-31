package cn.com.mfish.code.mapper;

import cn.com.mfish.code.entity.FieldInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ：qiufeng
 * @description：mysql数据库表
 * @date ：2022/8/31 22:27
 */
public interface MysqlTableMapper {
    List<FieldInfo> getColumns(@Param("schema") String schema,@Param("tableName") String tableName);
}
