package com.lingyuting.wechat.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lingyuting.wechat.exception.WechatException;
import com.lingyuting.wechat.models.Topic;

public abstract class WechatUtil {

    private String id;
    protected Topic model;
    private int totalpages = 0;

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return "1.0";
    }

    public String getId() {
        return this.id;
    }

    private int getTotalPage(String str) {
        if (0 != totalpages) {
            return totalpages;
        }
        Pattern pattern = Pattern.compile("totalPages\":([0-9]*)");
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            totalpages = Integer.parseInt(matcher.group(1));
        }
        return totalpages;
    }

    /**
     * 获取第一页的doc对象
     * 
     * @return
     */
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

    /**
     * 获取指定url的doc对象
     * 
     * @param url
     * @return
     */
    protected Document getDoc(String url) {
        try {
            return Jsoup
                    .connect(url)
                    .ignoreContentType(true)
                    .timeout(10000)
                    .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.131 Safari/537.36")
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String makeUrl() {
        if (null == id || "".equals(id)) {
            throw new WechatException("must set id first");
        }
        return "http://weixin.sogou.com/gzhjs?openid=" + id;
    }

    protected String makeUrl(int page) {
        if (null == id || "".equals(id)) {
            throw new WechatException("must set id first");
        }
        return "http://weixin.sogou.com/gzhjs?openid=" + id + "&page=" + page;
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
                    "make sure the openId is right, otherwise no topics in this wechat account");
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

    /**
     * 获取最新指定条数的文章doc对象
     * 
     * @param limit
     *            条数
     * @return
     */
    public List<Document> getDocuments(int limit) {
        Document doc = getDoc();
        if (null == doc) {
            throw new WechatException("unknown error");
        }
        List<Document> docs = new ArrayList<Document>();

        ListIterator<Element> topicUrls = doc.select("url").listIterator();
        if (!topicUrls.hasNext()) {
            throw new WechatException(
                    "make sure the openId is right, otherwise no topics in this wechat account");
        }

        int start = 1;
        while (topicUrls.hasNext()) {
            Element topicUrl = topicUrls.next();
            topicUrl.select("title1").remove();
            String url = topicUrl.text();
            Document topicDoc = getDoc(url);
            if (null != topicDoc) {
                docs.add(topicDoc);
            }
            topicDoc.attr("originUrl", url);
            if (limit <= start) {
                break;
            }
            start++;
        }
        return docs;
    }

    /**
     * 获取指定页的全部话题
     * 
     * @param limit
     * @return
     */
    protected List<Topic> getPageTopics(int page) {
        List<Topic> list = new ArrayList<Topic>();
        String url = makeUrl(page);
        Document doc = getDoc(url);
        if (null == doc) {
            throw new WechatException("unknown error");
        }

        if (0 != totalpages && page > totalpages) {
            return list;
        }

        if (0 == totalpages) {
            getTotalPage(doc.select("pagesize").last().html().toString());
            if (page > totalpages) {
                return list;
            }
        }

        ListIterator<Element> topicUrls = doc.select("url").listIterator();
        if (null == topicUrls || !topicUrls.hasNext()) {
            throw new WechatException(
                    "make sure the openId is right, otherwise no topics in this wechat account");
        }
        while (topicUrls.hasNext()) {
            Element topicUrl = topicUrls.next();
            topicUrl.select("title1").remove();
            Topic topic = getTopicByUrl(topicUrl.text());
            if (null != topic) {
                list.add(topic);
            }
        }

        return list;
    }

    /**
     * 获取指定页的文章doc对象
     * 
     * @param page
     *            当前页数
     * @return
     */
    public List<Document> getPageDocuments(int page) {
        String url = makeUrl(page);
        Document doc = getDoc(url);
        if (null == doc) {
            throw new WechatException("unknown error");
        }
        List<Document> docs = new ArrayList<Document>();
        if (0 != totalpages && page > totalpages) {
            return docs;
        }

        if (0 != totalpages) {
            getTotalPage(doc.select("pagesize").last().html().toString());
            if (page > totalpages) {
                return docs;
            }
        }

        ListIterator<Element> topicUrls = doc.select("url").listIterator();
        if (!topicUrls.hasNext()) {
            throw new WechatException(
                    "make sure the openId is right, otherwise no topics in this wechat account");
        }

        while (topicUrls.hasNext()) {
            Element topicUrl = topicUrls.next();
            topicUrl.select("title1").remove();
            Document topicDoc = getDoc(topicUrl.text());
            if (null != topicDoc) {
                docs.add(topicDoc);
            }
            topicDoc.attr("originUrl", url);
        }
        return docs;
    }

}
