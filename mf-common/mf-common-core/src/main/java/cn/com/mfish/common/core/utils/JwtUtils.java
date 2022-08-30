package cn.com.mfish.common.core.utils;

import cn.com.mfish.common.core.constants.Constants;
import cn.com.mfish.common.core.constants.CredentialConstants;
import cn.com.mfish.common.core.utils.text.Convert;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Map;

/**
 * @author ：qiufeng
 * @description：JWT通用类
 * @date ：2021/11/18 18:11
 */
public class JwtUtils {

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    public static String createToken(Map<String, Object> claims)
    {
        String token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, Constants.SECRET).compact();
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    public static Claims parseToken(String token)
    {
        return Jwts.parser().setSigningKey(Constants.SECRET).parseClaimsJws(token).getBody();
    }

    /**
     * 根据令牌获取用户标识
     *
     * @param token 令牌
     * @return 用户ID
     */
    public static String getUserKey(String token)
    {
        Claims claims = parseToken(token);
        return getValue(claims, CredentialConstants.USER_KEY);
    }

    /**
     * 根据令牌获取用户标识
     *
     * @param claims 身份信息
     * @return 用户ID
     */
    public static String getUserKey(Claims claims)
    {
        return getValue(claims, CredentialConstants.USER_KEY);
    }

    /**
     * 根据令牌获取用户ID
     *
     * @param token 令牌
     * @return 用户ID
     */
    public static String getUserId(String token)
    {
        Claims claims = parseToken(token);
        return getValue(claims, CredentialConstants.DETAILS_USER_ID);
    }

    /**
     * 根据身份信息获取用户ID
     *
     * @param claims 身份信息
     * @return 用户ID
     */
    public static String getUserId(Claims claims)
    {
        return getValue(claims, CredentialConstants.DETAILS_USER_ID);
    }

    /**
     * 根据令牌获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public static String getUserName(String token)
    {
        Claims claims = parseToken(token);
        return getValue(claims, CredentialConstants.DETAILS_USERNAME);
    }

    /**
     * 根据身份信息获取用户名
     *
     * @param claims 身份信息
     * @return 用户名
     */
    public static String getUserName(Claims claims)
    {
        return getValue(claims, CredentialConstants.DETAILS_USERNAME);
    }

    /**
     * 根据身份信息获取键值
     *
     * @param claims 身份信息
     * @param key 键
     * @return 值
     */
    public static String getValue(Claims claims, String key)
    {
        return Convert.toStr(claims.get(key), "");
    }
}
