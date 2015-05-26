package com.lingyuting.wechat;

import java.util.List;

import org.jsoup.nodes.Document;

import com.lingyuting.wechat.models.Topic;
import com.lingyuting.wechat.util.WechatUtil;

public class WechatSpider extends WechatUtil {

    private int limit = 5;

    /**
     * <pre>
     *  new WechatSpider("123").getPageDocs(1); 获取第一页的的全部文章
     * </pre>
     * 
     * @param id
     *            微信公共号的openId
     */
    public WechatSpider(String id) {
        super.setId(id);
        //super.excute();
    }

    /**
     * 不能跨页获取
     * @param id
     *            微信公共号的openId
     * @param limit
     *            最多获取的条数
     * @deprecated 参考最佳实践 main()
     */
    public WechatSpider(String id, int limit) {
        super.setId(id);
        this.limit = limit;
    }

    public List<Topic> getTopics() {
        return super.getTopics(this.limit);
    }

    public List<Document> getDocs() {
        return super.getDocuments(this.limit);
    }

    public List<Topic> getPageTopics(int page) {
        return super.getPageTopics(page);
    }

    public List<Document> getPageDocs(int page) {
        return super.getPageDocuments(page);
    }

    public static void main(String[] args) {
        WechatSpider spider = new WechatSpider("oIWsFt3vXtE-Bsg2MvX8Nebm-p2g");// 360手机卫士
        int page = 1;
        while (true) {
            System.out.println(page + "----------");
            List<Topic> topics = spider.getPageTopics(page);
            if (topics.size() == 0) {
                break;
            }
            for (Topic topic : topics) {
                System.out.println(topic.getTitle());
            }
            page++;
        }
    }

}
