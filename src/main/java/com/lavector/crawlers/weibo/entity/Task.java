package com.lavector.crawlers.weibo.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@MappedSuperclass
abstract class Task {

    @Id
    @GeneratedValue(generator = "customGenerationId")
    @GenericGenerator(name = "customGenerationId", strategy = "com.lavector.crawlers.weibo.utils.CustomGenerationId")
    private String id;

    @Column(nullable = false)
    private String createdUserId;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createdTime;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date updatedTime;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(String createdUserId) {
        this.createdUserId = createdUserId;
    }


    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }






}
