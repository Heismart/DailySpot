package cn.grokit.dailyspot.process.segmentfault;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.nio.charset.StandardCharsets;

import static cn.grokit.dailyspot.constant.Segmentfault.*;

/**
 * 思否日热榜
 *
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/26
 * @license MIT
 * @since 0.1.0
 */
public class HotTopicsDailyProcess implements PageProcessor {
    @Override
    public void process(Page page) {
        //TODO

    }

    @Override
    public Site getSite() {
        return Site.me()
                .setDomain(SEGMENTFAULT_URL)
                .setCharset(StandardCharsets.UTF_8.toString())
                .setRetryTimes(3)
                .setTimeOut(10000);
    }
}
