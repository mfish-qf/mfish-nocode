package cn.com.mfish.oauth.handler;

import cn.com.mfish.common.core.utils.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: mfish
 * @description: 角色处理handler
 * @date: 2022/11/22 14:42
 */
public class StrToListTypeHandler extends BaseTypeHandler<List<String>> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<String> strings, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, StringUtils.join(strings, ','));
    }

    @Override
    public List<String> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return getResult(resultSet.getString(s));
    }

    @Override
    public List<String> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return getResult(resultSet.getString(i));
    }

    @Override
    public List<String> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return getResult(callableStatement.getString(i));
    }

    private List<String> getResult(String result) {
        if (StringUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        String[] ss = result.split(",");
        return Arrays.asList(ss);
    }

}
