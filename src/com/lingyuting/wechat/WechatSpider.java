package com.lingyuting.wechat;

import java.util.List;

import com.lingyuting.wechat.util.WechatUtil;

public class WechatSpider extends WechatUtil {
	
	public WechatSpider(String id) {
		super.setId(id);
		super.excute();
	}

	public String getUrl() {
		return model.getUrl();
	}

	public String getTitle() {
		return model.getTitle();
	}

	public String getContent() {
		return model.getContent();
	}

	public List<String> getImgs() {
		return model.getImages();
	}

	public String getDate() {
		return model.getDate();
	}

	public String getUser() {
		return model.getUser();
	}
}
