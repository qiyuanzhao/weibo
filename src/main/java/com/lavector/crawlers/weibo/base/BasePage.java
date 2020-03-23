package com.lavector.crawlers.weibo.base;

import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;

@Component
public interface BasePage {

    boolean handleUrl(String url);

    void process(Page page);

}
