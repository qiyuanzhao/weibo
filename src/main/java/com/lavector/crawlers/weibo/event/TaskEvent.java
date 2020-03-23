package com.lavector.crawlers.weibo.event;


public class TaskEvent {

    private String taskId;

    private String taskName;

    private String userId;

    private String status;

    private Long eventTime;

    private Integer totalPageCount;


    public TaskEvent(String taskId, String taskName, String userId, String status, Long eventTime, Integer totalPageCount) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.userId = userId;
        this.status = status;
        this.eventTime = eventTime;
        this.totalPageCount = totalPageCount;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public Integer getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(Integer totalPageCount) {
        this.totalPageCount = totalPageCount;
    }
}
