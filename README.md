##WechatSpider 能做什么
* 是一依赖于jsoup用来抓取微信认证公共号最新文章的工具类
* 很多朋友问我为什么我的代码中会出现 `"" == str` 这样的代码，由于我最近一直写php，太久不写java了，大家如果再看到类似的代码自己改过来就行了

##如何使用

* 首先导入 wechat.jar 和 jsoup.jar 包到工程目录
* 实例化类 `WechatSpider spider = new WechatSpider("oIWsFt1cKphwPhDydMD0b_fi7n80", 5);` 参数为微信公共号的openId和想要获取的条数
* 然后`spider.getTopics();` 可以获取标题，作者，时间，内容，url，以及文章内图片等信息的列表

##如何把微信的文章保存到MySQL

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

##有问题反馈
在使用中有任何问题，欢迎反馈给我，可以用以下联系方式跟我交流。代码非常简单，希望大家有问题能够自己先解决一下，谢谢。
* QQ: 893157632