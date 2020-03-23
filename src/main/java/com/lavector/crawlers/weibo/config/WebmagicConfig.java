package com.lavector.crawlers.weibo.config;


import com.lavector.crawlers.weibo.weibo.WeiboProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.Spider;

@Configuration
public class WebmagicConfig {

    @Autowired
    private WeiboProcessor weiboProcessor;

    @Bean
    public Spider getSpider(){
        return new Spider(weiboProcessor);
    }

}
