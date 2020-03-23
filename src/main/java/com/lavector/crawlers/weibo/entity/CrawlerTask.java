package com.lavector.crawlers.weibo.entity;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;


@Entity(name = "crawler_task")
public class CrawlerTask extends Task {

    @Column(nullable = false, length = 30)
    private String name;

    //@ElementCollection
    @Transient
    private String[] sites;

    @Transient
    private String[] keywords;

    @Transient
    private String[] excludeKeywords;

    @Temporal(value = TemporalType.DATE)
    private Date beginTime;

    @Temporal(value = TemporalType.DATE)
    private Date endTime;

    @Enumerated(EnumType.STRING)
    private Status status = Status.CREATED;

    @Column(nullable = false, name = "site")
    private String siteConcat;

    @Column(nullable = false, name = "keywords")
    private String keywordsConcat;

    @Column(name = "exclude_keywords")
    private String excludeKeywordsConcat;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getSite() {
        if (this.sites == null || this.sites.length == 0) {
            if (StringUtils.isNoneBlank(this.siteConcat)) {
                this.sites = StringUtils.split(this.siteConcat, ",");
            } else {
                this.sites = new String[0];
            }
        }
        return sites;
    }

    public void setSite(String... sites) {
        sites = StringUtils.stripAll(sites);
        this.sites = sites;
        this.siteConcat = StringUtils.join(sites, ",");
    }

    public String[] getKeywords() {
        if (this.keywords == null || this.keywords.length == 0) {
            if (StringUtils.isNoneBlank(this.keywordsConcat)) {
                this.keywords = StringUtils.split(this.keywordsConcat, ",");
            } else {
                this.keywords = new String[0];
            }
        }
        return keywords;
    }

    public void setKeywords(String... keywords) {
        keywords = StringUtils.stripAll(keywords);
        this.keywords = keywords;
        this.keywordsConcat = StringUtils.join(keywords, ",");
    }

    public String[] getExcludeKeywords() {
        if (this.excludeKeywords == null || this.excludeKeywords.length == 0) {
            if (StringUtils.isNoneBlank(this.excludeKeywordsConcat)) {
                this.excludeKeywords = StringUtils.split(this.excludeKeywordsConcat, ",");
            } else {
                this.excludeKeywords = new String[0];
            }
        }
        return excludeKeywords;
    }

    public void setExcludeKeywords(String[] excludeKeywords) {
        excludeKeywords = StringUtils.stripAll(excludeKeywords);
        this.excludeKeywords = excludeKeywords;
        this.excludeKeywordsConcat = StringUtils.join(excludeKeywords, ",");
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getSiteConcat() {
        return siteConcat;
    }

    public void setSiteConcat(String siteConcat) {
        this.siteConcat = siteConcat;
    }

    public String getKeywordsConcat() {
        return keywordsConcat;
    }

    public void setKeywordsConcat(String keywordsConcat) {
        this.keywordsConcat = keywordsConcat;
    }

    public String getExcludeKeywordsConcat() {
        return excludeKeywordsConcat;
    }

    public void setExcludeKeywordsConcat(String excludeKeywordsConcat) {
        this.excludeKeywordsConcat = excludeKeywordsConcat;
    }



}
