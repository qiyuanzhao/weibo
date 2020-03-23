package com.lavector.crawlers.weibo.weibo.page;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lavector.crawlers.weibo.constant.RequestExtraKey;
import com.lavector.crawlers.weibo.entity.Message;
import com.lavector.crawlers.weibo.repository.WeiboMessageRepository;
import com.lavector.crawlers.weibo.utils.RegexUtil;
import com.lavector.crawlers.weibo.base.BasePage;
import com.lavector.crawlers.weibo.entity.WeiboMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SearchPage implements BasePage {

    private Logger logger = LoggerFactory.getLogger(SearchPage.class);

    Pattern pattern = Pattern.compile("[1-9]\\d*");

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH:ss");

    @Autowired
    private WeiboMessageRepository weiboMessageRepository;


    public static SearchPage searchPage;

    @PostConstruct
    public void init() {
        searchPage = this;
        searchPage.weiboMessageRepository = this.weiboMessageRepository;
    }

    @Override
    public boolean handleUrl(String url) {
        return RegexUtil.isMatch("https://s.weibo.com/weibo\\?q.*", url);
    }

    @Override
    public void process(Page page) {

//        logger.info("获取的page：{}", page.getRawText());

        String keyword = page.getRequest().getExtra(RequestExtraKey.KEY_KEYWORD).toString();
        Set<String> excludeKeywords = (Set<String>) page.getRequest().getExtra(RequestExtraKey.EXCLUDE_KEYWORDS);
        String taskId = (String) page.getRequest().getExtra(RequestExtraKey.TASK_ID);

        Html html = page.getHtml();
        Selectable mConL = html.xpath("//div[@class='m-main']//div[@class='m-wrap']/div[@class='m-con-l']");
        String mConlStr = mConL.get();
        if (!StringUtils.isEmpty(mConlStr) && mConlStr.contains("抱歉，未找到") && mConlStr.contains("相关结果")) {
            logger.info("------没有搜索到结果------  url：{} --- keyword：{}", page.getUrl(), keyword);
            return;
        }

        //搜索列表
        List<Selectable> nodes = mConL.xpath("//div[@class='card-wrap' and @action-type='feed_list_item']").nodes();
        logger.info("获取{}条数据", nodes.size());
        for (Selectable node : nodes) {
            Message message = new Message();
            String id = node.xpath("//div[@class='card-wrap']/@mid").get();
            Selectable contenSelectable = node.xpath("//div[@class='content']");

            //微博内容
            String content = contenSelectable.xpath("//div[@class='content']/p[@node-type='feed_list_content_full']/allText()").get();
            if (content == null) {
                content = contenSelectable.xpath("//div[@class='content']/p[@node-type='feed_list_content']/allText()").get();
            }

            //用户id
            String userUrl = node.xpath("//div[@class='card-wrap']//div[@class='card-feed']//div[@class='avator']//a/@href").get();
            String userId = userUrl.substring(userUrl.indexOf("weibo.com/") + 10, userUrl.indexOf("?", 1));

            //转发
            Selectable cardCommentSelect = contenSelectable.xpath("//div[@class='card-comment']");
            String cardComment = cardCommentSelect.get();

            //如果有转发
            if (!StringUtils.isEmpty(cardComment) && !"null".equalsIgnoreCase(cardComment)) {
                Map quotedMessageEntity = handleReport(cardCommentSelect);
                message.quotedMessageEntity = quotedMessageEntity;
                message.type = "RePost";
            } else {
                message.type = "Post";
            }

            //图片
            List<String> pics = new ArrayList<>();
            Selectable piclistSelect = contenSelectable.xpath("//div[@class='content']/div[@node-type='feed_list_media_prev']/div[@class='media media-piclist']");
            String piclistSelectStr = piclistSelect.get();
            if (!StringUtils.isEmpty(piclistSelectStr) && !"null".equalsIgnoreCase(piclistSelectStr)) {
                List<Selectable> picsSelects = piclistSelect.xpath("//ul/li").nodes();
                for (Selectable picsSelect : picsSelects) {
                    String picUrl = "http:" + picsSelect.xpath("//img/@src").get();
                    pics.add(picUrl);
                }
            }

            //时间
            String tc = contenSelectable.xpath("//div[@class='content']/p[@class='from']//a[@target='_blank']/text()").get();
            tc = handleTime(tc);

            //发布地址
            String local = contenSelectable.xpath("//div[@class='content']/p[@class='from']//a[@rel='nofollow']").get();
            //微博url
            String url = "http:" + contenSelectable.xpath("//div[@class='content']/p[@class='from']//a[@target='_blank']/@href").get();

            Selectable reportCommentLike = node.xpath("//div[@class='card-act']");
            String report = reportCommentLike.xpath("//ul/li[2]//a/text()").get();
            String comment = reportCommentLike.xpath("//ul/li[3]//a/text()").get();
            String like = reportCommentLike.xpath("//ul/li[4]//a//em/text()").get();

            message.setId(id);
            message.setContent(content);
            message.reposts = Integer.parseInt(handleInteger(report));
            message.comments = Integer.parseInt(handleInteger(comment));
            message.likes = Integer.parseInt(handleInteger(like));
            message.setUrl(url);
            message.settC(tc);
            if (message.quotedMessageEntity != null) {
                message.qmId = message.quotedMessageEntity.get("id").toString();
            }

            message.client = local;
            message.keyword = keyword;
            message.pics = pics;
            message.tF = new Date();
            message.userId = userId;
            message.site = "weibo";

            if (checkCountAndTitle(message, keyword) && checkExcludeKeywordsCountAndTitle(message, excludeKeywords)) {
                long start = System.currentTimeMillis();
                //保存到数据库
                saveMessage(message, taskId);
                long end = System.currentTimeMillis();
                logger.info("保存到数据库的时间：{}", (end - start) / 1000);
            }
        }


        String url = page.getRequest().getUrl();
        String pageNumber = url.substring(url.lastIndexOf("=") + 1);
        int newPageNumber = Integer.parseInt(pageNumber) + 1;

        if (newPageNumber < 50 && nodes.size() > 0) {
            String newUrl = url.substring(0, url.lastIndexOf("=") + 1) + newPageNumber;

            logger.info("下一页链接：{}", newUrl);
            page.addTargetRequest(new Request(newUrl).putExtra(RequestExtraKey.KEY_KEYWORD, keyword).putExtra(RequestExtraKey.EXCLUDE_KEYWORDS, excludeKeywords).putExtra(RequestExtraKey.TASK_ID, taskId));
            page.setSkip(true);
        }

    }


    private Map handleReport(Selectable cardCommentSelect) {
        Message reportMessage = new Message();
        Selectable cardCommentDtile = cardCommentSelect.xpath("//div[@class='card-comment']//div[@node-type='feed_list_forwardContent']");


        //文本
        String contentDtile = cardCommentDtile.xpath("//p[@node-type='feed_list_content_full']/allText()").get();
        if (contentDtile == null) {
            contentDtile = cardCommentDtile.xpath("//p[@node-type='feed_list_content']/allText()").get();
        }

        //用户id
        String uidStr = cardCommentSelect.xpath("//div[@class='func']//ul[@class='act s-fr']/li[1]//a/@href").get();
        String uidStart = uidStr.substring(0, uidStr.lastIndexOf("/"));
        String uid = handleInteger(uidStart);

        //mid
        String midStr = cardCommentSelect.xpath("//div[@class='func']//ul[@class='act s-fr']/li[3]//a/@action-data").get();
        String mid = handleInteger(midStr);

        //图片
        List<String> pics = new ArrayList<>();
        Selectable piclistSelectDetile = cardCommentSelect.xpath("//div[@node-type='feed_list_media_prev']/div[@class='media media-piclist']");
        String piclistSelectDetileStr = piclistSelectDetile.get();
        if (!StringUtils.isEmpty(piclistSelectDetileStr) && !"null".equalsIgnoreCase(piclistSelectDetileStr)) {
            List<Selectable> picsList = piclistSelectDetile.xpath("//ul/li").nodes();
            for (Selectable selectable : picsList) {
                String picUrl = "http:" + selectable.xpath("//img/@src").get();
                pics.add(picUrl);
            }
        }

        Selectable func = cardCommentSelect.xpath("//div[@class='func']");

        List<Selectable> funcList = func.xpath("//ul[@class='act s-fr']/li").nodes();
        //转发
        String repostDetil = funcList.get(0).xpath("//a/text()").get();
        //评论
        String commentDetil = funcList.get(1).xpath("//a/text()").get();
        //点赞
        String likeDetil = func.xpath("//ul[@class='act s-fr']/li[3]//em/text()").get();

        //时间
        String tc = cardCommentSelect.xpath("//p[@class='from']//a[@target='_blank']/text()").get();
        tc = handleTime(tc);

        reportMessage.userId = uid;

        reportMessage.setId(mid);
        reportMessage.qmId = mid;
        reportMessage.type = "Post";
        reportMessage.setContent(contentDtile);
        reportMessage.pics = pics;

        reportMessage.reposts = Integer.parseInt(handleInteger(repostDetil));
        reportMessage.comments = Integer.parseInt(handleInteger(commentDetil));
        reportMessage.likes = Integer.parseInt(handleInteger(likeDetil));
        reportMessage.settC(tc);
        reportMessage.tF = new Date();

        String string = JSONObject.toJSONString(reportMessage, SerializerFeature.DisableCircularReferenceDetect);
        Map map = JSON.parseObject(string, Map.class);
        return map;
    }


    /**
     * 保存到数据库
     *
     * @param message
     * @param taskId
     */
    private synchronized Integer saveMessage(Message message, String taskId) {

        long findStart = System.currentTimeMillis();
        List<WeiboMessage> weiboMessageList = searchPage.weiboMessageRepository.findByWeiboId(message.getId());
        long findEnd = System.currentTimeMillis();
        logger.info("查询的时间：{}", findEnd - findStart);

        if (CollectionUtils.isEmpty(weiboMessageList)) {
            WeiboMessage weiboMessage = new WeiboMessage();
            weiboMessage.setId(Long.parseLong(message.getId()));
            weiboMessage.setWeiboId(message.getId());
            String body = JSONObject.toJSONString(message, SerializerFeature.DisableCircularReferenceDetect);
            weiboMessage.setBody(body);
            weiboMessage.setCreateTime(new Date());
            weiboMessage.setFlag(0);
            weiboMessage.setKeyword(message.keyword);
            weiboMessage.type = message.type;
            try {
                weiboMessage.setTitleTime(simpleDateFormat.parse(message.gettC()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            weiboMessage.setContainsExclude(0);
            weiboMessage.setTaskId(taskId);
            WeiboMessage save = searchPage.weiboMessageRepository.save(weiboMessage);
            logger.info("————————保存到mysql:{}————————", save.getId());
            return 0;
        } else {
            if (weiboMessageList.size() > 1) {
                Long id = weiboMessageList.get(1).getId();
                searchPage.weiboMessageRepository.deleteById(id);
            }
            logger.info("————————数据库中已存在 微博id:{}————————", weiboMessageList.get(0).getId());
            return 1;
        }
    }

    private String handleInteger(String str) {
        if (StringUtils.isEmpty(str)) {
            return "0";
        }
        String group = "";
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            group = matcher.group();
        }
        if (StringUtils.isEmpty(group)) {
            return "0";
        }
        return group;
    }

    private String handleTime(String tc) {
        if (StringUtils.isEmpty(tc)) {
            return null;
        }
        tc = tc.trim().replace(" ", "");
        if (tc.contains("月") && tc.contains("日") && !tc.contains("年")) {
            tc = "2019年" + tc;
        }
        if (tc.contains("分钟前") || tc.contains("秒前") || tc.contains("今天")) {
            tc = simpleDateFormat.format(new Date());
        }
        return tc;
    }


    //查询关键词
    private boolean checkCountAndTitle(Message message, String keyword) {
        boolean flag = false;
        if (!StringUtils.isEmpty(message.getContent())) {
            String content = message.getContent().toUpperCase();
            if (keyword.contains(" ")) {
                String[] split = keyword.toUpperCase().split(" ");
                if ((content.contains(split[0]) && content.contains(split[1]))) {
                    flag = true;
                }
            } else if (content.contains(keyword.toUpperCase())) {
                flag = true;
            }
        }

        if (!StringUtils.isEmpty(message.quotedMessageEntity) && !StringUtils.isEmpty(message.quotedMessageEntity.get("content"))) {
            String content = message.quotedMessageEntity.get("content").toString().toUpperCase();
            if (keyword.contains(" ")) {
                String[] split = keyword.toUpperCase().split(" ");
                if ((content.contains(split[0]) && content.contains(split[1]))) {
                    flag = true;
                }
            } else if (content.contains(keyword.toUpperCase())) {
                flag = true;
            }
        }
        return flag;
    }


    //查询排除词
    private boolean checkExcludeKeywordsCountAndTitle(Message message, Set<String> exkeywords) {
        boolean flag = true;
        for (String keyword : exkeywords) {
            if (!StringUtils.isEmpty(message.getContent())) {
                String content = message.getContent().toUpperCase();
                if (keyword.contains(" ")) {
                    String[] split = keyword.toUpperCase().split(" ");
                    if ((content.contains(split[0]) && content.contains(split[1]))) {
                        flag = false;
                    }
                } else if (content.contains(keyword.toUpperCase())) {
                    flag = false;
                }
            }

            if (!StringUtils.isEmpty(message.quotedMessageEntity) && !StringUtils.isEmpty(message.quotedMessageEntity.get("content"))) {
                String content = message.quotedMessageEntity.get("content").toString().toUpperCase();
                if (keyword.contains(" ")) {
                    String[] split = keyword.toUpperCase().split(" ");
                    if ((content.contains(split[0]) && content.contains(split[1]))) {
                        flag = false;
                    }
                } else if (content.contains(keyword.toUpperCase())) {
                    flag = false;
                }
            }
        }
        return flag;
    }


}
