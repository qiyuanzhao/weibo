package com.lavector.crawlers.weibo.constant;

import com.fasterxml.jackson.annotation.JsonCreator;


public enum Site {

    // auto
    AUTOHOME("汽车之家", "autohome"),
    XCAR("爱卡汽车", "xcar"),
    SINA("新浪", "sina"),

    // social media
    WEIBO("微博", "weibo"),
    TENCENT_WEIBO("腾讯微博", "qqt"),
    TENCENT_BULUO("腾讯兴趣部落","tbuluo"),
    WEIXIN("微信", "weixin"),
    TIEBA("百度贴吧","tieba"),

    IYINGDI("旅法师营地","iyingdi"),
    ZHIHU("知乎", "zhihu"),
    DIANPING("大众点评", "dianping"),

    JD("京东", "jd"),
    TMALL("天猫", "tmall"),

    XIACF("下厨房", "xiacf"),
    DOUBAN("豆瓣", "douban"),
    NEWRANK("新榜","newrank"),
    TOUTIAO("今日头条", "toutiao"),
    MEIPAI("美拍", "meipai"),
    REDBOOK("小红书", "redbook");

    private String name;
    private String code;

    @JsonCreator
    Site(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public String getCode() {
        return this.code;
    }

    public static Site findByCode(String code) {
        for (Site site : Site.values()) {
            if (site.getCode().equals(code)) {
                return site;
            }
        }
        return null;
    }
}
