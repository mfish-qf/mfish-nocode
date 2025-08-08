package cn.com.mfish.common.core.utils.http;

import cn.com.mfish.common.core.enums.HttpType;
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
import java.util.Arrays;
import java.util.List;
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
        return get(url, null, null, null);
    }

    public static Result<String> get(String url, TimeOut timeOut) throws IOException {
        return get(url, null, timeOut);
    }

    public static Result<String> get(String url, Map<String, String> headers) throws IOException {
        return get(url, null, headers, null);
    }

    public static Result<String> get(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        return get(url, params, headers, null);
    }

    /**
     * 执行HTTP GET请求
     * <p>
     * 此方法构造一个带有参数的URL并执行GET请求如果参数为空，则执行不带参数的GET请求
     *
     * @param url     请求的URL
     * @param params  请求的参数，格式为键值对
     * @param headers 请求的头信息，格式为键值对
     * @param timeOut 请求的超时时间
     * @return 返回请求的结果
     * @throws IOException 如果请求过程中发生错误
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

    public static <T> Result<String> postForm(String url, T params) throws IOException {
        return postForm(url, params, null);
    }

    public static <T> Result<String> postForm(String url, T params, Map<String, String> headers) throws IOException {
        return postForm(url, params, headers, null);
    }

    /**
     * 使用Form类型执行POST请求
     * <p>
     * 本方法专注于处理Form类型的POST请求，通过提供的URL、请求参数、自定义请求头和超时设置，
     * 来执行网络请求，并返回请求的结果。使用较低级别的HTTP API来实现自定义请求的需求。
     *
     * @param url     请求的URL，指定了资源的位置
     * @param params  请求参数，以字符串形式提交，通常为键值对的形式
     * @param headers 请求头信息，包含HTTP请求头部字段和对应的值，可为空
     * @param timeOut 请求的超时设置，决定了请求各阶段的最大等待时间
     * @return 返回请求的结果，包括状态码、响应头和响应体等信息
     * @throws IOException 如果请求过程中发生I/O错误，如网络中断等
     */
    public static <T> Result<String> postForm(String url, T params, Map<String, String> headers, TimeOut timeOut) throws IOException {
        return post(url, params, headers, timeOut, Form_TYPE);
    }

    public static <T> Result<String> postJson(String url, T params) throws IOException {
        return postJson(url, params, null);
    }

    public static <T> Result<String> postJson(String url, T params, Map<String, String> headers) throws IOException {
        return postJson(url, params, headers, null);
    }

    /**
     * 执行JSON类型的POST请求
     * 此方法用于发送带有JSON数据的HTTP POST请求到指定的URL
     * 它允许自定义请求头和超时设置，以满足不同的网络请求需求
     *
     * @param url     请求的URL地址，指定资源位置
     * @param params  请求的JSON参数，以字符串形式表示
     * @param headers 请求头信息，包含请求的附加信息，如认证信息、接受类型等
     * @param timeOut 连接和读取超时设置，用于控制网络请求的响应时间
     * @return 返回包含响应结果的Result对象，响应体以字符串形式存储
     */
    public static <T> Result<String> postJson(String url, T params, Map<String, String> headers, TimeOut timeOut) throws IOException {
        return post(url, params, headers, timeOut, JSON_TYPE);
    }

    /**
     * 执行POST请求
     * 该方法负责发起HTTP POST请求，根据提供的参数构建请求并发送
     * 主要用于需要向服务器提交数据的场景
     *
     * @param url       请求的URL，表示服务器的地址
     * @param params    请求的参数，通常为JSON字符串或其他格式的数据
     * @param headers   请求头部，包含一些附加信息如认证信息、内容类型等
     * @param timeOut   请求的超时时间设置
     * @param mediaType 请求体的媒体类型，通常指明发送的数据格式（如"application/json"）
     * @return 返回请求的结果，包含响应的所有信息
     * @throws IOException 如果请求过程中发生错误，可能会抛出此异常
     */
    public static <T> Result<String> post(String url, T params, Map<String, String> headers, TimeOut timeOut, MediaType mediaType) throws IOException {
        return request(url, params, headers, timeOut, mediaType, RequestMethod.POST);
    }

    /**
     * 执行PUT请求
     * 该方法用于向指定URL发送PUT请求，用于更新资源
     *
     * @param url       请求的URL，指定资源位置
     * @param params    请求的参数，通常为JSON字符串或其他格式的数据
     * @param headers   请求头信息，包含一些附加信息如认证信息、内容类型等
     * @param timeOut   请求的超时时间设置
     * @param mediaType 请求体的媒体类型，通常指明发送的数据格式（如"application/json"）
     * @return 返回请求的结果，包含响应的所有信息
     * @throws IOException 如果请求过程中发生错误，可能会抛出此异常
     */
    public static <T> Result<String> put(String url, T params, Map<String, String> headers, TimeOut timeOut, MediaType mediaType) throws IOException {
        return request(url, params, headers, timeOut, mediaType, RequestMethod.PUT);
    }

    /**
     * 执行DELETE请求
     * 该方法用于向指定URL发送DELETE请求，用于删除资源
     *
     * @param url       请求的URL，指定资源位置
     * @param params    请求的参数，通常为JSON字符串或其他格式的数据
     * @param headers   请求头信息，包含一些附加信息如认证信息、内容类型等
     * @param timeOut   请求的超时时间设置
     * @param mediaType 请求体的媒体类型，通常指明发送的数据格式（如"application/json"）
     * @return 返回请求的结果，包含响应的所有信息
     * @throws IOException 如果请求过程中发生错误，可能会抛出此异常
     */
    public static <T> Result<String> delete(String url, T params, Map<String, String> headers, TimeOut timeOut, MediaType mediaType) throws IOException {
        return request(url, params, headers, timeOut, mediaType, RequestMethod.DELETE);
    }

    /**
     * 执行通用请求
     * 该方法用于向指定URL发送HTTP请求，根据提供的参数构建请求并发送
     * 主要用于需要向服务器提交数据的场景
     *
     * @param url           请求的URL，表示服务器的地址
     * @param params        请求的参数，通常为JSON字符串或其他格式的数据
     * @param headers       请求头部，包含一些附加信息如认证信息、内容类型等
     * @param timeOut       请求的超时时间设置
     * @param mediaType     请求体的媒体类型，通常指明发送的数据格式（如"application/json"）
     * @param requestMethod 请求方法，指定HTTP请求的类型（如GET、POST、PUT、DELETE等）
     * @return 返回请求的结果，包含响应的所有信息
     * @throws IOException 如果请求过程中发生错误，可能会抛出此异常
     */
    public static <T> Result<String> request(String url, T params, Map<String, String> headers, TimeOut timeOut, MediaType mediaType, RequestMethod requestMethod) throws IOException {
        log.info("请求url：{},请求入参：{},请求头部：{}", url, params, headers);
        RequestBody requestBody;
        // 判断请求参数类型
        if (params instanceof String) {
            requestBody = RequestBody.create((String) params, mediaType);
        } else {
            requestBody = RequestBody.create(JSON.toJSONString(params), mediaType);
        }
        Request request = buildRequest(url, headers, requestBody, requestMethod);
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
        return upload(url, file, headers, new TimeOut(), mediaType);
    }

    /**
     * 文件上传接口
     *
     * @param url     文件上传的URL，指定文件将被上传到的服务器地址
     * @param file    待上传的文件，通过File对象指定
     * @param headers 请求头信息，用于设置HTTP请求的头字段
     * @param timeOut 超时设置，控制请求的读写超时时间
     * @return 返回一个Result对象，包含上传结果和一个字符串消息
     * @throws IOException 如果文件读写或网络通信出现问题，可能会抛出此异常
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
     * 根据给定的URL和超时设置构建一个OkHttpClient实例，支持HTTPS连接和自定义超时设置
     *
     * @param url     要请求的URL，用于确定使用HTTP还是HTTPS
     * @param timeOut 超时设置，用于设置连接、读取和写入的超时时间
     * @return 构建好的OkHttpClient实例
     */
    private static OkHttpClient buildOkHttpClient(String url, TimeOut timeOut) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder().protocols(List.of(Protocol.HTTP_1_1));
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
     * @param url         请求的URL地址
     * @param headers     请求头信息，为键值对形式的Map
     * @param requestBody 请求体内容，根据不同请求方法包含不同的数据
     * @param method      请求方法，包括GET、POST、PUT、PATCH、DELETE等
     * @return 返回构建完成的Request对象
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
     * @param okHttpClient OkHttpClient实例，用于发送HTTP请求
     * @param request      HTTP请求对象，包含请求的详细信息
     * @return 返回一个Result对象，包含请求的响应内容、状态码和消息
     * @throws IOException 如果请求过程中发生IO异常，则会抛出此异常
     */
    private static Result<String> buildResult(OkHttpClient okHttpClient, Request request) throws IOException {
        try (Response response = okHttpClient.newCall(request).execute()) {
            int code = response.code();
            String msg = response.message();
            log.info("请求执行完成，响应状态码：{}，响应信息：{}", code, msg);
            if (response.body() != null) {
                String data = response.body().string();
                log.info("响应结果：{}", data);
                return Result.buildResult(data, code, msg);
            }
            return Result.buildResult(null, code, msg);
        } catch (IOException e) {
            log.error("错误:OkHttp请求异常", e);
            throw e;
        }
    }

    public static class SSLBuild {
        //获取SSLSocketFactory
        public static SSLSocketFactory getSSLSocketFactory() {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
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
