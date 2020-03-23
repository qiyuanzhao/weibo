package com;

import com.lavector.crawlers.weibo.WeiboCrawlerApplication;
import com.lavector.crawlers.weibo.config.RabbitConfig;
import com.lavector.crawlers.weibo.entity.CrawlerTask;
import com.lavector.crawlers.weibo.event.EventType;
import com.lavector.crawlers.weibo.repository.TaskRepository;
import com.lavector.crawlers.weibo.utils.RabbitSendUtils;
import com.lavector.crawlers.weibo.weibo.WeiboCrawler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WeiboCrawlerApplication.class)
public class CrawlerTest {

    @Autowired
    private WeiboCrawler weiboCrawler;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private RabbitSendUtils rabbitSendUtils;

    @Test
    public void testCrawler() {
        CrawlerTask task = taskRepository.findById("510cb93e9ee94f998357068fa0799af8").orElse(null);
        weiboCrawler.startCrawler(task);
    }

    @Test
    public void sendRabbit() {

        CrawlerTask task = taskRepository.findById("510cb93e9ee94f998357068fa0799af8").orElse(null);
        rabbitSendUtils.sendMessage(RabbitConfig.EVENTS, task, 1000, EventType.CANCELLED);

    }

}
