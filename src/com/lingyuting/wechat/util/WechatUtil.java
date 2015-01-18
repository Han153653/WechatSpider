package com.lingyuting.wechat.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	
	public String getVersion()
	{
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
		String id = getId();
		if (null == id || "" == id) {
			throw new WechatException("must set id first");
		}
		return "http://weixin.sogou.com/gzhjs?openid=" + id;
	}

	public void excute() {
		Document doc = getDoc();
		if (null == doc) {
			return;
		}
		Element topicUrl = doc.select("url").first();
		topicUrl.select("title1").remove();
		String url = topicUrl.text();
		this.model = fetchContent(url);
	}

	public Topic fetchContent(String url) {
		Document doc = getDoc(url);
		if (null == doc) {
			return null;
		}
		Topic model = new Topic();
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
		return model;
	}

}
