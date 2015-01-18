##WechatSpider 能做什么
是一依赖于jsoup用来抓取微信认证公共号最新文章的工具类

##如何使用

* 首先导入 wechat.jar 和 jsoup.jar 包到工程目录
* 实例化类 `WechatSpider spider = new WechatSpider("oIWsFt1cKphwPhDydMD0b_fi7n80");` 参数为微信公共号的openId
* 然后`System.out.println(spider.getTitle());` 可以获取标题，作者，时间，内容，url，以及文章内图片等信息

##程序健壮性
* 暂未测试，可能会有验证码一类的情况出现，可以通过cookie的方式解决。等发现问题了会做进一步的修改

##有问题反馈
在使用中有任何问题，欢迎反馈给我，可以用以下联系方式跟我交流

* QQ: 893157632