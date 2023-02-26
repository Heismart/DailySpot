package cn.grokit.dailyspot.process.zhihu;

import cn.grokit.dailyspot.entity.zhihu.Topic;
import cn.grokit.dailyspot.util.ScrapeUtils;
import cn.grokit.dailyspot.util.TopicTree;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.handler.PatternProcessor;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 话题广场
 *
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/18
 * @license MIT
 * @since 0.1.0
 */
public class TopicProcess extends PatternProcessor {

    /**
     * @param pattern url pattern to handle
     */
    public TopicProcess(String pattern) {
        super(pattern);
    }

    @Override
    public MatchOther processPage(Page page) {
        CopyOnWriteArrayList<Topic> topics = page.getHtml().xpath("//ul[@class='zm-topic-cat-main clearfix']/li").nodes().stream().map(node -> {
            String topicName = node.xpath("//a/text()").toString();
            Long topicId = Long.valueOf(node.xpath("//li/@data-id").toString());
            // 添加子话题爬取
            page.addTargetRequest(ScrapeUtils.assemblyBody(topicId, 0));
            return new Topic().setTopicId(topicId).setTopicName(topicName);
        }).collect(Collectors.toCollection(CopyOnWriteArrayList::new));

        // 设置根话题列表
        topics.forEach(TopicTree::putNode);
        return MatchOther.YES;
    }

    @Override
    public MatchOther processResult(ResultItems resultItems, Task task) {
        return null;
    }
}
