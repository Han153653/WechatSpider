package com.lingyuting.wechat.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lingyuting.wechat.exception.WechatException;
import com.lingyuting.wechat.models.Topic;

public abstract class WechatUtil {

    private String id;
    protected Topic model;

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return "1.0";
    }

    public String getId() {
        return this.id;
    }

    protected Document getDoc() {
        String url = makeUrl();
        try {
            return Jsoup
                    .connect(url)
                    .timeout(10000)
                    .ignoreContentType(true)
                    .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.131 Safari/537.36")
                    .get();
        } catch (IOException e) {
            return null;
        }
    }

    protected Document getDoc(String url) {
        try {
            return Jsoup
                    .connect(url)
                    .timeout(10000)
                    .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.131 Safari/537.36")
                    .get();
        } catch (IOException e) {
            return null;
        }
    }

    protected String makeUrl() {
        if (null == id || "".equals(id)) {
            throw new WechatException("must set id first");
        }
        return "http://weixin.sogou.com/gzhjs?openid=" + id;
    }

    protected void excute() {
        Document doc = getDoc();
        if (null == doc) {
            throw new WechatException("unknown error");
        }

        Element topicUrl = doc.select("url").first();
        if (null == topicUrl) {
            throw new WechatException(
                    "make sure the openId is right, otherwise no topcs in this wechat account");
        }
        topicUrl.select("title1").remove();
        String url = topicUrl.text();
        fetchContent(url);
    }

    protected void fetchContent(String url) {
        Document doc = getDoc(url);
        if (null == doc) {
            return;
        }
        model = new Topic();
        String title = doc.select("#activity-name").first().text();
        Elements imagesDom = doc.select("#js_content img[data-src]");
        String content = doc.select("#js_content").first().html();
        String date = doc.select("#post-date").first().text();
        String user = doc.select("#post-user").first().text();
        List<String> images = new ArrayList<>();
        for (Element img : imagesDom) {
            images.add(img.attr("data-src"));
        }

        model.setContent(content);
        model.setImages(images);
        model.setUrl(url);
        model.setTitle(title);
        model.setDate(date);
        model.setUser(user);
    }

    protected Topic getTopicByUrl(String url) {
        Document doc = getDoc(url);
        if (null == doc) {
            return null;
        }
        Topic topic = new Topic();
        String title = doc.select("#activity-name").first().text();
        Elements imagesDom = doc.select("#js_content img[data-src]");
        String content = doc.select("#js_content").first().html();
        String date = doc.select("#post-date").first().text();
        String user = doc.select("#post-user").first().text();
        List<String> images = new ArrayList<>();
        for (Element img : imagesDom) {
            images.add(img.attr("data-src"));
        }

        topic.setContent(content);
        topic.setImages(images);
        topic.setUrl(url);
        topic.setTitle(title);
        topic.setDate(date);
        topic.setUser(user);
        return topic;
    }

    /**
     * 获取指定条数的最新话题
     * 
     * @param limit
     * @return
     */
    protected List<Topic> getTopics(int limit) {
        List<Topic> list = new ArrayList<Topic>();
        Document doc = getDoc();
        if (null == doc) {
            throw new WechatException("unknown error");
        }

        ListIterator<Element> topicUrls = doc.select("url").listIterator();
        if (!topicUrls.hasNext()) {
            throw new WechatException(
                    "make sure the openId is right, otherwise no topcs in this wechat account");
        }
        int start = 1;
        while (topicUrls.hasNext()) {
            Element topicUrl = topicUrls.next();
            topicUrl.select("title1").remove();
            String url = topicUrl.text();
            Topic topic = getTopicByUrl(url);
            if (null != topic) {
                list.add(topic);
            }
            if (limit <= start) {
                break;
            }
            start++;
        }

        return list;
    }

}
