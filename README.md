##WechatSpider 能做什么
是一依赖于jsoup用来抓取微信认证公共号最新文章的工具类

##如何使用

* 首先导入 wechat.jar 和 jsoup.jar 包到工程目录
* 实例化类 `WechatSpider spider = new WechatSpider("oIWsFt1cKphwPhDydMD0b_fi7n80", 5);` 参数为微信公共号的openId和想要获取的条数
* 然后`spider.getTopics();` 可以获取标题，作者，时间，内容，url，以及文章内图片等信息的列表

##如何把微信的文章保存到MySQL
* WechatSpider 可以通过 getDocs 返回 jsoup的Document对象列表

```java
    WechatSpider spider = new WechatSpider("oIWsFt507pxIH3FW5d4DvLDLJHfA", 5);
    List<Document> docs = spider.getDocs();
    for (Document doc : docs) {
        String title = doc.select("#activity-name").first().text();
        String url = doc.attr("originUrl");
        Element content = doc.select("#js_content").first();
        // 下载图片到自己的服务器并替换图片url,需自行实现即可
        Elements imagesDom = content.select("img[data-src]");
        for (Element img : imagesDom) {
            String file = FileUtil.loadImage(img.attr("data-src"));
            if (null == file || "" == file) {
                continue;
            }
            img.attr("src", PicUtil.imgServer + "/" + file);
        }
        String topic = content.html();
        //TODO inert into mysql
    }
```

* WechatSpider 获取某个公共号全部的文章

```java
		WechatSpider spider = new WechatSpider("oIWsFt3vXtE-Bsg2MvX8Nebm-p2g");// 360手机卫士
        int page = 1;
        while (true) {
            List<Topic> topics = spider.getPageTopics(page);
            if (topics.size() == 0) {
                break;
            }
            for (Topic topic : topics) {
                System.out.println(topic.getTitle());
				//your business
            }
            page++;
        }
```

##程序健壮性
* 运行一个月，每天抓取100条左右，暂时没有异常出现
* 微信推送的文章内的图片都是webp格式,在IOS上显示会有一定的问题，如果下载到自己本地服务器记得转格式
* 搜狗最近推出了反爬虫的机制，这周有点忙，大家如果遇到问题可以尝试保持cookie会话来解决一下（尚未验证）

##有问题反馈
在使用中有任何问题，欢迎反馈给我，可以用以下联系方式跟我交流

* QQ: 893157632