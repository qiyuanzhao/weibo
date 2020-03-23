package com.lavector.crawlers.weibo.downloader;


import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.selector.Json;

import java.io.IOException;
import java.nio.charset.Charset;

@Component
public class WeiBoDownloader implements Downloader {

    @Override
    public Page download(Request request, Task task) {
        Page page = this.myDownloader(request, task);
        if (!task.getSite().getAcceptStatCode().contains(page.getStatusCode())) {
            page.setDownloadSuccess(false);
        }
        return page;
    }

    @Override
    public void setThread(int threadNum) {

    }

    private static CloseableHttpClient getHttpclient() {
        return new DynamicProxyDownloader().ignoreValidationHttpClient();
    }


    private Page myDownloader(Request request, Task task) {
        Page page = Page.fail();
        CloseableHttpClient httpClient = getHttpclient();
        try {
            page.setUrl(new Json(request.getUrl()));
            page.setRequest(request);
            HttpGet httpGet = new HttpGet(request.getUrl());
            task.getSite().getHeaders().forEach(httpGet::addHeader);
            RequestConfig config = RequestConfig.custom()
                    .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                    .setConnectTimeout(10000)
                    .setSocketTimeout(10000)
                    .setConnectionRequestTimeout(10000)
                    .build();
            httpGet.setConfig(config);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String content = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
                int statusCode = response.getStatusLine().getStatusCode();
                page.setRawText(content);
                page.setStatusCode(statusCode);
                page.setDownloadSuccess(true);
                return page;
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return page;
    }
}
