package com.lavector.crawlers.weibo.controller;

import com.alibaba.fastjson.JSONObject;
import com.lavector.crawlers.weibo.config.RabbitConfig;
import com.lavector.crawlers.weibo.service.WeiboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = RabbitConfig.WEIBOCRAWLER)
public class WeiboController {

    private Logger logger = LoggerFactory.getLogger(WeiboController.class);

    private WeiboService weiboService;

    public WeiboController(WeiboService weiboService) {
        this.weiboService = weiboService;
    }


    @RabbitHandler
    public void process(String jsonObj) {
        JSONObject jsonObject = JSONObject.parseObject(jsonObj);
        String taskId = jsonObject.get("taskId").toString();

        logger.info("接受到消息-->  taskId :{}", taskId);
        weiboService.handleCrawler(taskId);
    }


}
