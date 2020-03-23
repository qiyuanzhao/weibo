package com.lavector.crawlers.weibo.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;
import java.util.Date;

@Entity
public class WeiboMessage implements Serializable{

    @Id
    private Long id;

    private String weiboId;

    private String keyword;

    private Date titleTime;

    @Lob
//    @Column(columnDefinition = "clob")
    private String body;

    private Date createTime;

    @Column(columnDefinition = "tinyint")
    private int flag;

    private int containsExclude;  //不包含排除词为0  包含为1

    private String taskId;

    public String type;

    public Long getId() {
        return id;
    }

    public String getWeiboId() {
        return weiboId;
    }

    public void setWeiboId(String weiboId) {
        this.weiboId = weiboId;
    }

    public String getKeyword() {
        return keyword;
    }

    public Date getTitleTime() {
        return titleTime;
    }

    public String getBody() {
        return body;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setTitleTime(Date titleTime) {
        this.titleTime = titleTime;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getContainsExclude() {
        return containsExclude;
    }

    public void setContainsExclude(int containsExclude) {
        this.containsExclude = containsExclude;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

}
