package com.lavector.crawlers.weibo.listener;

import com.lavector.crawlers.weibo.entity.CrawlerTask;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.monitor.SpiderStatusMXBean;


public class SpiderMonitorImpl extends SpiderMonitor {
    
    private CustomSpiderStatus customSpiderStatus;

    private Spider spider;

    private CrawlerTask task;


    @Override
    protected SpiderStatusMXBean getSpiderStatusMBean(Spider spider, MonitorSpiderListener monitorSpiderListener) {
        CustomSpiderStatus customSpiderStatus = new CustomSpiderStatus(this.spider, monitorSpiderListener);
        this.customSpiderStatus = customSpiderStatus;
        return customSpiderStatus;
    }

    public SpiderMonitorImpl(Spider spider){
        this.spider = spider;
    }


    public CustomSpiderStatus getCustomSpiderStatus() {
        return customSpiderStatus;
    }

    public Spider getSpider() {
        return spider;
    }

    public CrawlerTask getTask() {
        return task;
    }

    public void setTask(CrawlerTask task) {
        this.task = task;
    }
}
