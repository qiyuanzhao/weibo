package com.lavector.crawlers.weibo.weibo;

import com.lavector.crawlers.weibo.constant.RequestExtraKey;
import com.lavector.crawlers.weibo.downloader.WeiBoDownloader;
import com.lavector.crawlers.weibo.entity.CrawlerTask;
import com.lavector.crawlers.weibo.listener.SpiderMonitorImpl;
import com.lavector.crawlers.weibo.utils.UrlUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

import javax.management.JMException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class WeiboCrawler {

    private Logger logger = LoggerFactory.getLogger(WeiboCrawler.class);

    @Autowired
    private WeiBoDownloader weiBoDownloader;

    private SpiderMonitorImpl spiderMonitor;


//    @Autowired
//    private Spider spider;

    //爬取的频率
    @Value("${time}")
    private String time;

    //如果 time == hour  按照几个小时爬取
    //如果 time == day   按照几天爬取
    @Value("${count}")
    private Integer count;

    //爬取的起始位置
    @Value("${pagenumber}")
    private Integer pageNumber;


    public void startCrawler(CrawlerTask task) {
        Spider spider = Spider.create(new WeiboProcessor());

        List<Request> request = new RequestBuilder()
                .setCount(count)
                .setEndDate(task.getEndTime())
                .setTimeIndex(task.getBeginTime())
                .setKeywords(new HashSet<>(Arrays.asList(task.getKeywords())))
                .setPageNumber(pageNumber)
                .setTask(task)
                .setRequests(new ArrayList<>())
                .setTime(time)
                .build();

        for (Request request1 : request) {
            spider.addRequest(request1);
        }

        //添加监听
        SpiderMonitorImpl spiderMonitor = new SpiderMonitorImpl(spider);
        try {
            if (spiderMonitor.getSpider() == null) {
                spiderMonitor.register(spider);
            }
        } catch (JMException e) {
            e.printStackTrace();
        }

        spiderMonitor.setTask(task);
        this.spiderMonitor = spiderMonitor;

        spider.setDownloader(weiBoDownloader);
        spider.thread(2);
        spider.start();

    }


    private class RequestBuilder {

        private Date timeIndex;

        private Date endDate;

        private Set<String> keywords;

        private List<Request> requests;

        private CrawlerTask task;

        private Integer count;

        private Integer pageNumber;

        private String time;

        private SimpleDateFormat simpleDateFormatByHour = new SimpleDateFormat("yyyy-MM-dd-HH");

        private SimpleDateFormat simpleDateFormatByDay = new SimpleDateFormat("yyyy-MM-dd");


        public RequestBuilder setTimeIndex(Date timeIndex) {
            this.timeIndex = timeIndex;
            return this;
        }

        public RequestBuilder setEndDate(Date endDate) {
            this.endDate = endDate;
            return this;
        }

        public RequestBuilder setKeywords(Set<String> keywords) {
            this.keywords = keywords;
            return this;
        }

        public RequestBuilder setRequests(List<Request> requests) {
            this.requests = requests;
            return this;
        }

        public RequestBuilder setTask(CrawlerTask task) {
            this.task = task;
            return this;
        }

        public RequestBuilder setCount(Integer count) {
            this.count = count;
            return this;
        }

        public RequestBuilder setPageNumber(Integer pageNumber) {
            this.pageNumber = pageNumber;
            return this;
        }

        public RequestBuilder setTime(String time) {
            this.time = time;
            return this;
        }


        public List<Request> build() {
            while (true) {
                logger.info("start time : {}, today : {}", timeIndex, new Date());
                if (timeIndex.compareTo(endDate) >= 0) {
                    break;
                }
                if ("hour".equalsIgnoreCase(time)) {
                    Date endDate = DateUtils.addHours(timeIndex, count);
                    getRequest(simpleDateFormatByHour, endDate);
                    timeIndex = DateUtils.addHours(timeIndex, count);
                } else if ("day".equalsIgnoreCase(time)) {
                    Date endDate = DateUtils.addDays(timeIndex, count);
                    getRequest(simpleDateFormatByDay, endDate);
                    timeIndex = DateUtils.addDays(timeIndex, count);
                } else {
                    logger.info("时间标志参数错误");
                }
            }

            return this.requests;
        }

        private synchronized void getRequest(SimpleDateFormat simpleDateFormatr, Date endDate) {
            for (String keyword : keywords) {
                logger.info("***********keyword = {}***********", keyword);
                String endDateStr = simpleDateFormatr.format(endDate);
                String startDateString = simpleDateFormatr.format(timeIndex);
                String url = "https://s.weibo.com/weibo?q=" + UrlUtils.encodeStr(keyword)
                        + "&typeall=1&suball=1&timescope=custom:" + startDateString + ":" + endDateStr + "&Refer=g&page=" + pageNumber;
                logger.info("url:{}", url);
                this.requests.add(new Request(url).putExtra(RequestExtraKey.KEY_KEYWORD, keyword).putExtra(RequestExtraKey.EXCLUDE_KEYWORDS, new HashSet<>(Arrays.asList(task.getExcludeKeywords()))).putExtra(RequestExtraKey.TASK_ID, task.getId()));
            }
        }

    }

    public SpiderMonitorImpl getSpiderMonitor() {
        return spiderMonitor;
    }

    public void setSpiderMonitor(SpiderMonitorImpl spiderMonitor) {
        this.spiderMonitor = spiderMonitor;
    }
}
