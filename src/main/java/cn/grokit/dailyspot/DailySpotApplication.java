package cn.grokit.dailyspot;

import cn.grokit.dailyspot.constant.Zhihu;
import cn.grokit.dailyspot.entity.zhihu.Topic;
import cn.grokit.dailyspot.process.zhihu.*;
import cn.grokit.dailyspot.thread.ScrapeThreadPool;
import cn.grokit.dailyspot.util.ScrapeUtils;
import cn.grokit.dailyspot.util.TopicTree;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.handler.CompositePageProcessor;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;
import us.codecraft.webmagic.utils.HttpConstant;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.grokit.dailyspot.constant.Zhihu.*;


/**
 * 热点聚合程序入口
 *
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/18
 * @license MIT
 * @since 0.1.0
 */
@Slf4j
public class DailySpotApplication {

    public static final String COOKIE = System.getenv().get("COOKIE");
    public static final String ANSWER_FLAG = System.getenv().get("ANSWER_FLAG");

    public final static ScheduledExecutorService scheduledThreadPool = Executors.newSingleThreadScheduledExecutor();


    public static void main(String[] args) {
        // 爬取知乎热榜
        Spider.create(new HotTopProcess()).addUrl(Zhihu.HOTSPOT_TOPICS_URL).run();
        if (StringUtils.isEmpty(COOKIE)) {
            log.warn("请在环境变量配置cookie参数");
            return;
        }
        Spider spider = Spider
                .create(assemblyPage(COOKIE))
                .thread(Runtime.getRuntime().availableProcessors() << 5)
                .setScheduler(new FileCacheQueueScheduler(TMP_PATH)
                );

        ScrapeUtils.setSpider(spider);

        log.info("《《《《《《《《《《《《《《《《开始爬取topic》》》》》》》》》》》》》》");
        if (!TopicTree.checkTopic()) {
            parseTopic(spider);
        }
        //结合话题广场的话题
        spider.addUrl(TOPICS_PAGE_URL).run();
        scheduledThreadPool.shutdown();

        boolean parseAnswer = Optional
                .ofNullable(ANSWER_FLAG)
                .map(Boolean::valueOf)
                .orElse(false);
        if (parseAnswer) {
            // 爬取话题下的问题
            parseTopicQuestion(spider, 3);
        }
        log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<回答爬取全部结束>>>>>>>>>>>>>>>>>>>>>>>>");
    }


    /**
     * 爬取话题
     *
     * @param spider
     */
    public static void parseTopic(Spider spider) {
        scheduledThreadPool.scheduleWithFixedDelay(() -> {
            // 保存话题json文件
            try {
                FileUtil.writeUtf8String(JSON.toJSONString(TopicTree.getRootTopic(), JSONWriter.Feature.PrettyFormat), Zhihu.TOPIC_FILENAME);
                List<Topic> topics = TopicTree.TOPIC_LIST.stream().filter(
                        topic -> topic.getFollowers() > 20000
                ).peek(
                        topic -> topic.setSubTopics(null)
                ).collect(Collectors.toList());
                FileUtil.writeUtf8String(JSON.toJSONString(topics, JSONWriter.Feature.PrettyFormat), TOP_TOPICS_FILENAME);
            } catch (IORuntimeException e) {
                log.error("保存数据文件失败：{}", ExceptionUtil.stacktraceToString(e));
            }
        }, 1, 1, TimeUnit.MINUTES);
        TopicTree.setRootTopic(new CopyOnWriteArrayList<Topic>() {{
            add(new Topic().setTopicId(19776749L).setTopicName("根话题").setParentId(0L));
        }});
        Request request = new Request("https://www.zhihu.com/topic/19776749/organize");
        request.addCookie("z_c0", COOKIE);
        spider.addRequest(request).run();
        scheduledThreadPool.shutdown();
        log.info("《《《《《《《《《《《《《《topic爬取完成，开始写入topic文件》》》》》》》》》》》》》》");
    }


    /**
     * 爬取话题下高赞回答
     *
     * @param spider
     * @param threadNum 线程数 目前设置并发为2
     */
    private static void parseTopicQuestion(Spider spider, Integer threadNum) {
        spider.setExecutorService(new ScrapeThreadPool(threadNum));
        String json = FileUtil.readUtf8String(TOP_TOPICS_FILENAME);
        List<Topic> list = JSON.parseArray(json, Topic.class);
        //按热度排序，优先爬取热度高的话题
        list.sort(Comparator.comparingLong(Topic::getFollowers).reversed());
        list.forEach(topic -> spider.addUrl(
                StrUtil.format(Zhihu.ANSWER_URL, topic.getTopicId(), 0))
        );
        spider.run();
    }


    /**
     * 递归添加请求
     *
     * @param topics
     * @param heightTopics
     */
    private static void forEachAddRequest(CopyOnWriteArrayList<Topic> topics, CopyOnWriteArrayList<Topic> heightTopics) {
        if (CollectionUtil.isEmpty(topics)) {
            return;
        }
        topics.stream().filter(topic -> topic.getFollowers() > 10000).forEach(topic -> {
            heightTopics.add(topic);
            if (CollectionUtil.isNotEmpty(topic.getSubTopics())) {
                forEachAddRequest(topic.getSubTopics(), topics);
            }
        });
    }


    /**
     * 基于cookie爬取话题
     *
     * @param cookie
     * @return
     */
    private static CompositePageProcessor assemblyPage(String cookie) {
        // 创建复合页面
        return new CompositePageProcessor(
                Site.me()
                        .setDomain("zhihu.com")
                        .setRetryTimes(3)
                        .setCycleRetryTimes(3)
                        .setCharset(StandardCharsets.UTF_8.toString())
                        .setTimeOut(10000)
                        .setAcceptStatCode(
                                new HashSet<Integer>() {
                                    {
                                        add(HttpConstant.StatusCode.CODE_200);
                                        add(HttpStatus.HTTP_NOT_FOUND);
                                        add(HttpStatus.HTTP_FORBIDDEN);
                                    }
                                }
                        )
        )
                .setSubPageProcessors(
                        new TopicPageProcess(TOPIC_PAGE_PATTERN, cookie),
                        new SubTopicProcess(TOPIC_URL),
                        new TopAnswerProcess(ANSWER_PATTERN),
                        new AnswerPageProcess(ANSWER_PAGE_PATTERN)
                );
    }
}
