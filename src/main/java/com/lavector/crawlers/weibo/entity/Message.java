package com.lavector.crawlers.weibo.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Message {

    private String id;

    private String content;

    public String quotedMessage;

    private String title;

    private String tC;

    public Date tF;

    public String keyword;

    public Integer reposts; //转

    public Integer likes; //赞

    public Integer comments; //评

    public String url;

    public List<String> pics;

    public String type;

    public String client;

    public String qmId;

    public Map quotedMessageEntity;

    private boolean flag;

    public String userId;

    public String site;

    public Message() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String gettC() {
        return tC;
    }

    public void settC(String tC) {
        this.tC = tC;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", tC=" + tC +
                ", reposts=" + reposts +
                ", likes=" + likes +
                ", comments=" + comments +
                '}';
    }


    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
