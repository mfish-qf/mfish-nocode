package cn.com.mfish.common.core.utils.http;

import cn.com.mfish.common.core.enums.HttpType;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description: okhttp通用类
 * @author: mfish
 * @date: 2023/2/8 11:27
 */
@Slf4j
public class OkHttpUtils {

    /**
     * sendGet
     *
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @param headers 请求的头部JSON
     * @return
     */
    public static <T> String post(String url, T queries, Map<String, String> headers) {
        log.info("请求url：{},请求入参：{},请求头部：{}", url, queries, headers);
        OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES);
        HttpType httpType = HttpType.getHttpType(url);
        if (httpType == HttpType.HTTPS) {
            clientBuilder.sslSocketFactory(SSLUtils.getSSLSocketFactory(), SSLUtils.getX509TrustManager())
                    .hostnameVerifier(SSLUtils.getHostnameVerifier());
        }
        OkHttpClient okHttpClient = clientBuilder.build();
        RequestBody requestBody = RequestBody.create(JSON.toJSONString(queries), MediaType.parse("application/json; charset=utf-8"));
        Request.Builder builder = new Request.Builder().url(url).post(requestBody);
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            int status = response.code();
            String result = response.body().string();
            if (status == 200) {
                log.info("[post] okhttp结果：" + result);
            } else {
                log.error("错误码:" + status);
            }
            return result;
        } catch (Exception e) {
            log.error("[post] okhttp3 get error >> ex = {}", ExceptionUtils.getStackTrace(e));
            throw new MyRuntimeException("[post] okhttp3 get error >> ex = {}" + e.getMessage());
        }
    }

}
