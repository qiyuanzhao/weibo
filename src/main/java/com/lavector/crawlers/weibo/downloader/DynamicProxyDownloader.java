package com.lavector.crawlers.weibo.downloader;

import com.google.common.base.Joiner;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class DynamicProxyDownloader extends HttpClientDownloader {

    public static final String[] USER_AGENTS;

    static {
        USER_AGENTS = new String[]{
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/604.3.5 (KHTML, like Gecko) Version/11.0.1 Safari/604.3.5",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:45.0) Gecko/20100101 Firefox/45.0"
        };
    }


    //定义申请获得的app_key和appSecret
    private static final String app_key = "175626467";
    private static final String secret = "c45206bf2209902ec1415b8bd12cbfa0";


    public static final HttpHost httpHost = new HttpHost("s2.proxy.mayidaili.com", 8123);//蚂蚁动态代理ip)
    protected static final RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).build();

    public static String getAuthHeader() {
        // 创建参数表
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("app_key", app_key);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));//使用中国时间，以免时区不同导致认证错误
        paramMap.put("timestamp", format.format(new Date()));

        // 对参数名进行排序
        String[] keyArray = paramMap.keySet().toArray(new String[0]);
        Arrays.sort(keyArray);

        // 拼接有序的参数名-值串
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(secret);
        for (String key : keyArray) {
            stringBuilder.append(key).append(paramMap.get(key));
        }

        stringBuilder.append(secret);
        String codes = stringBuilder.toString();

        // MD5编码并转为大写， 这里使用的是Apache codec
        String sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(codes).toUpperCase();

        paramMap.put("sign", sign);

        // 拼装请求头Proxy-Authorization的值，这里使用 guava 进行map的拼接

        return "MYH-AUTH-MD5 " + Joiner.on('&').withKeyValueSeparator("=").join(paramMap);
    }


    /*
     * 忽略认证
     */
    public CloseableHttpClient ignoreValidationHttpClient() {
//		SSLContext sslcontext = null;
//		try {
//			sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
//
//				@Override
//				public boolean isTrusted(X509Certificate[] chain, String authType)
//						throws CertificateException {
//					return true;
//				}
//			}).build();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

        HttpClientBuilder builder = ignoreValidating();

        builder.setProxy(httpHost);
        CloseableHttpClient httpclient = builder.build();
        return httpclient;

    }

    public void sleep(int num) {
        try {
            Thread.sleep(num);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public HttpClientBuilder ignoreValidating() {
        HttpClientBuilder builder = HttpClients.custom();

        try {
            SSLContext ignoreVerifySSL = createIgnoreVerifySSL();
//            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ignoreVerifySSL, NoopHostnameVerifier.INSTANCE);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ignoreVerifySSL, new String[] { "TLSv1.2" }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            builder.setSSLSocketFactory(sslsf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder;
    }

    protected SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {

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

        SSLContext sc = SSLContext.getInstance("SSLv3");
        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }


//    public static void main(String[] args) throws Exception {
//        DynamicProxyDownloader dynamicProxyDownloader = new DynamicProxyDownloader();
//        String url = "http://www.xcar.com.cn/bbs/";
//        HttpHost httpHost = new HttpHost("s2.proxy.mayidaili.com", 8123);//蚂蚁动态代理ip)
//        RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).build();
//        HttpGet httpGet = new HttpGet(url);
////		httpGet.addHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
//        httpGet.addHeader("cookie", "TY_SESSION_ID=858a01bb-c98a-440a-8dbe-eb8d6f4c21d1");
//        httpGet.setConfig(requestConfig);
//        httpGet.addHeader("Proxy-Authorization", dynamicProxyDownloader.getAuthHeader());
//    }
}
