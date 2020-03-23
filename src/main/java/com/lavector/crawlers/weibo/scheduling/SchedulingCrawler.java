package com.lavector.crawlers.weibo.scheduling;

import com.lavector.crawlers.weibo.config.RabbitConfig;
import com.lavector.crawlers.weibo.entity.CrawlerTask;
import com.lavector.crawlers.weibo.entity.Status;
import com.lavector.crawlers.weibo.event.EventType;
import com.lavector.crawlers.weibo.listener.CustomSpiderStatus;
import com.lavector.crawlers.weibo.listener.SpiderMonitorImpl;
import com.lavector.crawlers.weibo.utils.RabbitSendUtils;
import com.lavector.crawlers.weibo.weibo.WeiboCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class SchedulingCrawler {

    private Logger logger = LoggerFactory.getLogger(SchedulingCrawler.class);

    private static Integer totalNumber = 0;

    @Autowired
    private WeiboCrawler weiboCrawler;

    @Autowired
    private RabbitSendUtils rabbitSendUtils;

//    @Scheduled(cron = "3 * * * * ?")
    @Async("threadPoolTaskExecutor")
    public void scheduledCrawler() {
//        logger.info("-------定时扫描已爬取页数-------> totalNumber : {}", totalNumber);
        SpiderMonitorImpl spiderMonitor = weiboCrawler.getSpiderMonitor();
        int totalPageCount = 0;
        if (spiderMonitor != null) {
            CustomSpiderStatus customSpiderStatus = spiderMonitor.getCustomSpiderStatus();
            if (customSpiderStatus != null) {
                totalPageCount = customSpiderStatus.getTotalPageCount();
                if (totalPageCount == totalNumber) {
                    CrawlerTask task = spiderMonitor.getTask();
                    //爬虫卡住
                    task.setStatus(Status.COMPLETED);
                    rabbitSendUtils.sendMessage(RabbitConfig.EVENTS, task, totalPageCount, EventType.COMPLETED);
                    spiderMonitor.getSpider().stop();
                    weiboCrawler.setSpiderMonitor(null);
                    return;
                }
            } else {
                //正常结束
                CrawlerTask task = spiderMonitor.getTask();
                task.setStatus(Status.COMPLETED);
                rabbitSendUtils.sendMessage(RabbitConfig.EVENTS, task, totalNumber, EventType.COMPLETED);
                spiderMonitor.getSpider().stop();
                weiboCrawler.setSpiderMonitor(null);
            }
            totalNumber = totalPageCount;
            logger.info("已爬取的页数：----> totalNumber : {}", totalNumber);
        }
    }

}
