package com.lavector.crawlers.weibo.listener;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.monitor.SpiderStatus;

public class CustomSpiderStatus extends SpiderStatus {

    public CustomSpiderStatus(Spider spider, SpiderMonitor.MonitorSpiderListener monitorSpiderListener) {
        super(spider, monitorSpiderListener);
    }

}
