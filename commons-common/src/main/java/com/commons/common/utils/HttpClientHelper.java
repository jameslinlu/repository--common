/*
 * 文  件  名：HttpClientHelper.java
 * 版         权：Copyright 2014 GSOFT Tech.Co.Ltd.All Rights Reserved.
 * 描         述：
 * 修  改  人：hadoop
 * 修改时间：2015年5月7日
 * 修改内容：新增
 */
package com.commons.common.utils;

import com.alibaba.fastjson.JSON;
import com.commons.common.utils.http.HttpDeleteRequest;
import com.commons.metadata.model.http.HttpResponse;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.CodingErrorAction;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * HttpClient工具
 */
public class HttpClientHelper {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientHelper.class);

    private static HttpClientHelper helper;

    private final static int TIMEOUT_CONNECTION = 3 * 60 * 1000;
    private final static int TIMEOUT_SOCKET = 3 * 60 * 1000;
    private final static int MAX_TOTAL = Integer.MAX_VALUE;
    private final static int MAX_RETRY = 5;
    private final static int MAX_ROUTE_TOTAL = Integer.MAX_VALUE;

    private CloseableHttpClient httpClient;
    private TrustManager[] trustManagers = new TrustManager[1];

    private HttpClientHelper() {
    }

    public static synchronized HttpClientHelper getInstance() {
        if (helper == null) {
            helper = new HttpClientHelper();
            helper.initialize();
        }
        return helper;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public static void destroy() {
        try {
            if (helper != null && helper.httpClient != null)
                helper.httpClient.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpClientContext createContext() {
        // httpClient上下文
        HttpClientContext httpClientContext = HttpClientContext.create();
        httpClientContext.setCookieStore(new BasicCookieStore());
        return httpClientContext;
    }

    private void initialize() {
        // Connection配置
        ConnectionConfig connectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).build();

        // 覆盖证书检测过程 [用以非CA的https链接 (CA, Certificate Authority 数字证书)]
        trustManagers[0] = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };


        SSLConnectionSocketFactory sslSocketFactory;
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(new KeyManager[0], trustManagers, new SecureRandom());
            sslSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        } catch (Exception e) {
            logger.error("error: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        // 创建 httpClient连接池
        PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create().
                        register("http", PlainConnectionSocketFactory.getSocketFactory()).
                        register("https", sslSocketFactory).build());
        httpClientConnectionManager.setMaxTotal(MAX_TOTAL); // 设置连接池线程最大数量
        httpClientConnectionManager.setDefaultMaxPerRoute(MAX_ROUTE_TOTAL); // 设置单个路由最大的连接线程数量
        httpClientConnectionManager.setDefaultConnectionConfig(connectionConfig);

        // 默认请求配置
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                .setSocketTimeout(TIMEOUT_SOCKET)
                .setConnectTimeout(TIMEOUT_CONNECTION)
                .setConnectionRequestTimeout(TIMEOUT_CONNECTION).build();

        // 设置重定向策略
        LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();

        // 重试策略
        HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= MAX_RETRY) {
                    // Do not retry if over max retry count
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {
                    // Retry if the server dropped connection on us
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {
                    // Do not retry on SSL handshake exception
                    return false;
                }
                HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    // Retry if the request is considered idempotent
                    return true;
                }
                return false;
            }
        };

        // 初始化 httpClient客户端
        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setConnectionManager(httpClientConnectionManager)
                .setDefaultRequestConfig(defaultRequestConfig)
                .setRedirectStrategy(redirectStrategy)
                .setRetryHandler(retryHandler);

        httpClient = httpClientBuilder.build();
    }

    private MultipartEntityBuilder processBuilderParams(Map<String, Object> params) {

        ContentType contentType = ContentType.TEXT_PLAIN.withCharset(Consts.UTF_8);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        if (params != null) {
            logger.debug(params.toString());
            Object value;
            for (Entry<String, Object> entry : params.entrySet()) {
                value = entry.getValue();
                if (value instanceof File) {
                    builder.addBinaryBody(entry.getKey(), (File) value);
                } else if (value instanceof CharSequence) {
                    builder.addTextBody(entry.getKey(), value.toString(), contentType);
                } else {
                    builder.addTextBody(entry.getKey(), JSON.toJSONString(value), contentType);
                }
            }
        }
        return builder;
    }

    /**
     * POST with params
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws Exception
     */
    public InputStream postStream(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        return checkResponseStatus(post(url, params, headers)).getStream();
    }

    public String postPlain(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        return checkResponseStatus(post(url, params, headers)).getText();
    }

    public HttpResponse post(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        HttpPost post = new HttpPost(url);
        logger.debug(url);
        MultipartEntityBuilder builder = processBuilderParams(params);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setCharset(Consts.UTF_8);
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        processHeader(post, headers);
        return internalProcess(post);
    }

    /**
     * POST with HttpEntity
     *
     * @param url
     * @param entityParam
     * @param headers
     * @return
     * @throws Exception
     */
    public InputStream postStream(String url, HttpEntity entityParam, Map<String, String> headers) throws Exception {
        return checkResponseStatus(post(url, entityParam, headers)).getStream();
    }

    public String postPlain(String url, HttpEntity entityParam, Map<String, String> headers) throws Exception {
        return checkResponseStatus(post(url, entityParam, headers)).getText();
    }

    public HttpResponse post(String url, HttpEntity entity, Map<String, String> headers) throws Exception {
        HttpPost post = new HttpPost(url);
        logger.debug(url);
        post.setEntity(entity);
        processHeader(post, headers);
        return internalProcess(post);
    }

    /**
     * GET with params, except HttpEntity
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws Exception
     */
    public InputStream getStream(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        return checkResponseStatus(get(url, params, headers)).getStream();
    }

    public String getPlain(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        return checkResponseStatus(get(url, params, headers)).getText();
    }

    public HttpResponse get(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        HttpGet get = new HttpGet(processURL(url, params));
        logger.debug(url);
        processHeader(get, headers);
        return internalProcess(get);
    }

    /**
     * PUT with params
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws Exception
     */
    public InputStream putStream(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        return checkResponseStatus(put(url, params, headers)).getStream();
    }

    public String putPlain(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        return checkResponseStatus(put(url, params, headers)).getText();
    }

    public HttpResponse put(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        HttpPut put = new HttpPut(processURL(url, params));
        logger.debug(url);
        processHeader(put, headers);
        return internalProcess(put);
    }

    /**
     * PUT with HttpEntity
     *
     * @param url
     * @param entityParam
     * @param headers
     * @return
     * @throws Exception
     */
    public InputStream putStream(String url, HttpEntity entityParam, Map<String, String> headers) throws Exception {
        return checkResponseStatus(put(url, entityParam, headers)).getStream();
    }

    public String putPlain(String url, HttpEntity entityParam, Map<String, String> headers) throws Exception {
        return checkResponseStatus(put(url, entityParam, headers)).getText();
    }

    public HttpResponse put(String url, HttpEntity entity, Map<String, String> headers) throws Exception {
        HttpPut put = new HttpPut(url);
        logger.debug(url);
        put.setEntity(entity);
        processHeader(put, headers);
        return internalProcess(put);
    }

    /**
     * DELETE with params, except HttpEntity
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws Exception
     */
    public InputStream deleteStream(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        return checkResponseStatus(delete(url, params, headers)).getStream();
    }

    public String deletePlain(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        return checkResponseStatus(delete(url, params, headers)).getText();
    }

    public HttpResponse delete(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        HttpDelete delete = new HttpDelete(processURL(url, params));
        logger.debug(url);
        processHeader(delete, headers);
        return internalProcess(delete);
    }


    /**
     * DELETE with HttpEntity
     *
     * @param url
     * @param entityParam
     * @param headers
     * @return
     * @throws Exception
     */
    public InputStream deleteStream(String url, HttpEntity entityParam, Map<String, String> headers) throws Exception {
        return checkResponseStatus(put(url, entityParam, headers)).getStream();
    }

    public String deletePlain(String url, HttpEntity entityParam, Map<String, String> headers) throws Exception {
        return checkResponseStatus(put(url, entityParam, headers)).getText();
    }

    public HttpResponse delete(String url, HttpEntity entity, Map<String, String> headers) throws Exception {
        HttpDeleteRequest delete = new HttpDeleteRequest(url);
        logger.debug(url);
        delete.setEntity(entity);
        processHeader(delete, headers);
        return internalProcess(delete);
    }


    /**
     * RESTful 内部操作
     *
     * @param rest
     * @return
     * @throws IOException
     */
    private HttpResponse internalProcess(HttpRequestBase rest) throws IOException {
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(rest, this.createContext());
            int statusCode = response.getStatusLine().getStatusCode();
            Header[] headers = response.getAllHeaders();
            HttpResponse httpResponse = new HttpResponse();
            httpResponse.setHeaders(new HashMap<>());
            if (headers != null && headers.length > 0) {
                for (Header header : headers) {
                    httpResponse.getHeaders().put(header.getName(), header.getValue());
                }
            }
            httpResponse.setCode(statusCode);
            if (httpResponse.isError()) {
                logger.error("error response: status {}, method {} ", statusCode, rest.getMethod());
            }
            httpResponse.setBytes(EntityUtils.toByteArray(response.getEntity()));
            Header header;
            if ((header = response.getEntity().getContentType()) != null)
                httpResponse.setContentType(header.getValue());
            return httpResponse;
        } catch (Exception e) {
            logger.error("error: {}", e.getMessage());
            throw e;
        } finally {
            release(rest, response, response != null ? response.getEntity() : null);
        }
    }

    /**
     * 检查 HttpResponse's Code [装饰器模式]
     *
     * @param response
     */
    private HttpResponse checkResponseStatus(HttpResponse response) {
        if (response.isError())
            throw new RuntimeException(String.format("Response is error, code: %s", response.getCode()));
        return response;
    }

    private void processHeader(HttpRequestBase entity, Map<String, String> headers) {
        if (headers == null) {
            return;
        }
        for (Entry<String, String> entry : headers.entrySet()) {
            entity.addHeader(entry.getKey(), entry.getValue());
        }
    }

    private String processURL(String processUrl, Map<String, Object> params) {
        if (params == null) {
            return processUrl;
        }
        logger.debug(params.toString());
        StringBuilder url = new StringBuilder(processUrl);
        if (url.indexOf("?") < 0)
            url.append('?');

        for (String name : params.keySet()) {
            url.append('&');
            url.append(name);
            url.append('=');
            url.append(String.valueOf(params.get(name)));
        }
        return url.toString().replace("?&", "?");
    }

    private void release(HttpRequestBase request, CloseableHttpResponse response, HttpEntity entity) {
        try {
            if (request != null)
                request.releaseConnection();
        } finally {
            try {
                if (entity != null)
                    EntityUtils.consume(entity);
            } catch (IOException e) {
                logger.error("error: {}", e.getMessage());
            } finally {
                try {
                    if (response != null)
                        response.close();
                } catch (IOException e) {
                    logger.error("error: {}", e.getMessage());
                }
            }
        }
    }

}
