package com.lingyuting.wechat;

import java.util.List;

import com.lingyuting.wechat.models.Topic;
import com.lingyuting.wechat.util.WechatUtil;

public class WechatSpider extends WechatUtil {

    private int limit = 5;

    /**
     * 
     * @param id
     *            微信公共号的openId
     * @deprecated for you can use new WechatSpider(String id, int limit)
     */
    public WechatSpider(String id) {
        super.setId(id);
        super.excute();
    }

    /**
     * 
     * @param id
     *            微信公共号的openId
     * @param limit
     *            最多获取的条数
     */
    public WechatSpider(String id, int limit) {
        super.setId(id);
        this.limit = limit;
    }

    public List<Topic> getTopics() {
        return super.getTopics(this.limit);
    }
}
