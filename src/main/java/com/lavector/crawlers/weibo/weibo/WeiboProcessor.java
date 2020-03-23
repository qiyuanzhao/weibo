package com.lavector.crawlers.weibo.weibo;


import com.lavector.crawlers.weibo.downloader.DynamicProxyDownloader;
import com.lavector.crawlers.weibo.base.BasePageProcessor;
import com.lavector.crawlers.weibo.weibo.page.SearchPage;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Site;

@Component
public class WeiboProcessor extends BasePageProcessor {


    private Site site = Site.me()
            .setCharset("utf-8")
            .setRetryTimes(6)//重试6次
            .setSleepTime(10000)
            .setTimeOut(10 * 1000)
            .addHeader("Proxy-Authorization", DynamicProxyDownloader.getAuthHeader())
            .addHeader("Referer", "https://s.weibo.com")
            .addHeader("Cookie", randomCookie())
            .addHeader("User-Agent", randomUserAgent());

    public WeiboProcessor(){
        super(new SearchPage());
    }

    @Override
    public Site getSite() {
        return site;
    }

}
