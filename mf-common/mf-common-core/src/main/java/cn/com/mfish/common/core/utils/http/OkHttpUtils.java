package cn.com.mfish.common.core.utils.http;

import cn.com.mfish.common.core.enums.HttpType;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.web.Result;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description: okhttp通用类
 * @author: mfish
 * @date: 2023/2/8 11:27
 */
@Slf4j
public class OkHttpUtils {
    public static final MediaType Form_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType File_TYPE = MediaType.parse("multipart/form-data; charset=utf-8");
    public static final MediaType Png_TYPE = MediaType.parse("image/png");
    public static final MediaType Jpg_TYPE = MediaType.parse("image/jpeg");

    @Data
    @Accessors(chain = true)
    public static class TimeOut {
        private long connectTimeOut = 30;
        private long readTimeOut = 30;
        private long writeTimeout = 60;
        private TimeUnit timeUnit = TimeUnit.SECONDS;
    }

    public static Result<String> get(String url) throws IOException {
        return get(url, null, null);
    }

    public static Result<String> get(String url, TimeOut timeOut) throws IOException {
        return get(url, null, timeOut);
    }

    public static Result<String> get(String url, Map<String, String> headers) throws IOException {
        return get(url, headers, null);
    }

    /**
     * get请求
     *
     * @param url
     * @param params
     * @return
     */
    public static Result<String> get(String url, Map<String, String> params, Map<String, String> headers, TimeOut timeOut) throws IOException {
        if (params == null || params.isEmpty()) {
            return get(url, headers, timeOut);
        }
        StringBuilder sb = new StringBuilder(url);
        boolean firstFlag = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (firstFlag) {
                sb.append("?").append(entry.getKey()).append("=").append(entry.getValue());
                firstFlag = false;
            } else {
                sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        return get(sb.toString(), headers, timeOut);
    }

    public static Result<String> get(String url, Map<String, String> headers, TimeOut timeOut) throws IOException {
        log.info("请求url：{},请求入参：{}", url, JSON.toJSONString(headers));
        OkHttpClient okHttpClient = buildOkHttpClient(url, timeOut);
        Request request = buildRequest(url, headers, null, RequestMethod.GET);
        return buildResult(okHttpClient, request);
    }

    public static Result<String> postForm(String url, String params) throws IOException {
        return postForm(url, params, null);
    }

    public static Result<String> postForm(String url, String params, Map<String, String> headers) throws IOException {
        return postForm(url, params, headers, null);
    }

    /**
     * Form类型post请求
     *
     * @param url
     * @param params
     * @param headers
     * @param timeOut
     * @return
     */
    public static Result<String> postForm(String url, String params, Map<String, String> headers, TimeOut timeOut) throws IOException {
        return post(url, params, headers, timeOut, Form_TYPE);
    }

    public static Result<String> postJson(String url, String params) throws IOException {
        return postJson(url, params, null);
    }

    public static Result<String> postJson(String url, String params, Map<String, String> headers) throws IOException {
        return postJson(url, params, headers, null);
    }

    /**
     * json类型post请求
     *
     * @param url
     * @param params
     * @param headers
     * @param timeOut
     * @return
     */
    public static Result<String> postJson(String url, String params, Map<String, String> headers, TimeOut timeOut) throws IOException {
        return post(url, params, headers, timeOut, JSON_TYPE);
    }

    /**
     * post请求
     *
     * @param url
     * @param params
     * @param headers
     * @param timeOut
     * @param mediaType
     * @return
     */
    public static Result<String> post(String url, String params, Map<String, String> headers, TimeOut timeOut, MediaType mediaType) throws IOException {
        log.info("请求url：{},请求入参：{},请求头部：{}", url, params, headers);
        RequestBody requestBody = RequestBody.create(params, mediaType);
        Request request = buildRequest(url, headers, requestBody, RequestMethod.POST);
        OkHttpClient okHttpClient = buildOkHttpClient(url, timeOut);
        return buildResult(okHttpClient, request);
    }

    public static Result<String> upload(String url, String filePath) throws IOException {
        return upload(url, filePath, null, File_TYPE);
    }

    public static Result<String> upload(String url, String filePath, MediaType mediaType) throws IOException {
        return upload(url, filePath, null, mediaType);
    }

    public static Result<String> upload(String url, String filePath, Map<String, String> headers, MediaType mediaType) throws IOException {
        return upload(url, new File(filePath), headers, null, mediaType);
    }

    public static Result<String> upload(String url, String filePath, Map<String, String> headers, TimeOut timeOut, MediaType mediaType) throws IOException {
        return upload(url, new File(filePath), headers, timeOut, mediaType);
    }

    public static Result<String> upload(String url, File file) throws IOException {
        return upload(url, file, null, File_TYPE);
    }

    public static Result<String> upload(String url, File file, MediaType mediaType) throws IOException {
        return upload(url, file, null, mediaType);
    }

    public static Result<String> upload(String url, File file, Map<String, String> headers, MediaType mediaType) throws IOException {
        return upload(url, file, headers, null, mediaType);
    }

    /**
     * 文件上传接口
     *
     * @param url
     * @param file
     * @param headers
     * @param timeOut
     * @return
     * @throws IOException
     */
    public static Result<String> upload(String url, File file, Map<String, String> headers, TimeOut timeOut, MediaType mediaType) throws IOException {
        OkHttpClient client = buildOkHttpClient(url, timeOut);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(file, mediaType))
                .build();
        Request request = buildRequest(url, headers, requestBody, RequestMethod.POST);
        return buildResult(client, request);
    }

    /**
     * 构建httpClient
     *
     * @param url
     * @param timeOut
     * @return
     */
    private static OkHttpClient buildOkHttpClient(String url, TimeOut timeOut) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder();
        if (timeOut != null) {
            clientBuilder.connectTimeout(timeOut.getConnectTimeOut(), timeOut.getTimeUnit())
                    .readTimeout(timeOut.getReadTimeOut(), timeOut.getTimeUnit())
                    .writeTimeout(timeOut.getWriteTimeout(), timeOut.getTimeUnit());
        }
        HttpType httpType = HttpType.getHttpType(url);
        if (httpType == HttpType.HTTPS) {
            clientBuilder.sslSocketFactory(SSLBuild.getSSLSocketFactory(), SSLBuild.getX509TrustManager());
        }
        return clientBuilder.build();
    }

    /**
     * 构建请求
     *
     * @param url
     * @param headers
     * @param requestBody
     * @param method
     * @return
     */
    private static Request buildRequest(String url, Map<String, String> headers, RequestBody requestBody, RequestMethod method) {
        Request.Builder builder = new Request.Builder().url(url);
        switch (method) {
            case GET:
                builder.get();
                break;
            case PUT:
                builder.put(requestBody);
                break;
            case PATCH:
                builder.patch(requestBody);
                break;
            case DELETE:
                builder.delete(requestBody);
                break;
            default:
                builder.post(requestBody);
                break;
        }
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    /**
     * 构建返回结果
     *
     * @param okHttpClient
     * @param request
     * @return
     */
    private static Result<String> buildResult(OkHttpClient okHttpClient, Request request) throws IOException {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new MyRuntimeException(MessageFormat.format("错误:OkHttp请求异常。请求:{0} 结果:{1}"
                        , request.body(), response.body()));
            }
            assert response.body() != null;
            return Result.buildResult(response.body().string(), response.code(), response.message());
        } catch (IOException e) {
            log.error("错误:OkHttp请求异常", e);
            throw e;
        }
    }

    public static class SSLBuild {
        //获取SSLSocketFactory
        public static SSLSocketFactory getSSLSocketFactory() {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, getTrustManager(), new SecureRandom());
                return sslContext.getSocketFactory();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        //获取TrustManager
        private static TrustManager[] getTrustManager() {
            return new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };
        }

        public static X509TrustManager getX509TrustManager() {
            X509TrustManager trustManager = null;
            try {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                }
                trustManager = (X509TrustManager) trustManagers[0];
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            return trustManager;
        }

    }
}
