package com.lavector.crawlers.weibo.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lavector.crawlers.weibo.event.EventType;
import com.lavector.crawlers.weibo.event.TaskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitSendUtils {

    private Logger logger = LoggerFactory.getLogger(RabbitSendUtils.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void sendMessage(String queues, Object obj, Integer totalPageCount, EventType eventType) {
        String objStr = JSONObject.toJSONString(obj);
        JSONObject jsonObject = JSON.parseObject(objStr);
        String taskId;
        String taskName;
        String userId;
        Object taskIdObj = jsonObject.get("taskId");
        if (taskIdObj == null) {
            taskId = jsonObject.get("id").toString();
        } else {
            taskId = taskIdObj.toString();
        }
        Object nameObj = jsonObject.get("taskName");
        if (nameObj == null) {
            taskName = jsonObject.get("name").toString();
        } else {
            taskName = nameObj.toString();
        }

        Object userIdObj = jsonObject.get("userId");
        if (userIdObj == null) {
            userId = jsonObject.get("createdUserId").toString();
        } else {
            userId = userIdObj.toString();
        }

        Long eventTime = System.currentTimeMillis();
        String status = eventType.toString();

        TaskEvent taskEvent = new TaskEvent(taskId, taskName, userId, status, eventTime, totalPageCount);
        String taskEventJson = JSONObject.toJSONString(taskEvent);
        logger.info("发送的队列：{} ， 消息内容：{}", queues, taskEventJson);
//        System.out.println(taskEventJson);
        rabbitTemplate.convertAndSend(queues, taskEventJson);
    }


}
