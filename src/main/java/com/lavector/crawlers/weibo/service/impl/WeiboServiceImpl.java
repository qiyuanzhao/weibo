package com.lavector.crawlers.weibo.service.impl;


import com.lavector.crawlers.weibo.config.RabbitConfig;
import com.lavector.crawlers.weibo.entity.CrawlerTask;
import com.lavector.crawlers.weibo.entity.Status;
import com.lavector.crawlers.weibo.event.EventType;
import com.lavector.crawlers.weibo.repository.TaskRepository;
import com.lavector.crawlers.weibo.service.WeiboService;
import com.lavector.crawlers.weibo.utils.RabbitSendUtils;
import com.lavector.crawlers.weibo.weibo.WeiboCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WeiboServiceImpl implements WeiboService {

    private Logger logger = LoggerFactory.getLogger(WeiboServiceImpl.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private WeiboCrawler weiboCrawler;

    @Autowired
    private RabbitSendUtils rabbitSendUtils;


    @Override
    public void handleCrawler(String taskId) {

        CrawlerTask task = taskRepository.findById(taskId).orElse(null);
        logger.info("获取的task：", task.toString());
        if (task != null) {
            logger.info("开始启动爬虫 ----> taksId : {} ", taskId);
            weiboCrawler.startCrawler(task);
            logger.info("爬虫启动成功 ----> taksId : {} ", taskId);
            task.setStatus(Status.STARTED);

            //发送events爬虫启动成功
            rabbitSendUtils.sendMessage(RabbitConfig.EVENTS, task, 0, EventType.STARTED);
            //更新状态为STARTED
            taskRepository.save(task);
            return;
        }

//        logger.info("爬虫任务不存在 ----> taksId : {} ", taskId);
//        Task failedTask = new Task();
//        failedTask.setId(taskId);
//        failedTask.setName("");
//        failedTask.setStatus(Status.FAILED);
//        rabbitSend.rabbitSendSuccess(failedTask, null);

    }


}
