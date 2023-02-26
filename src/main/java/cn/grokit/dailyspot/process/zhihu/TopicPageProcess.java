package cn.grokit.dailyspot.process.zhihu;

import cn.grokit.dailyspot.constant.Zhihu;
import cn.grokit.dailyspot.entity.zhihu.Topic;
import cn.grokit.dailyspot.util.TopicTree;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.handler.PatternProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/18
 * @license MIT
 * @since 0.1.0
 */
@Slf4j
public class TopicPageProcess extends PatternProcessor {

    private final String cookie;

    /**
     * @param pattern url pattern to handle
     */
    public TopicPageProcess(String pattern, String cookie) {
        super(pattern);
        this.cookie = cookie;
    }

    @Override
    public MatchOther processPage(Page page) {
        Long parentTopicId = Long.parseLong(StrUtil.subBetween(page.getUrl().toString(), "topic/", "/"));
        String followers = page.getHtml().xpath("//div[@class='zm-topic-side-followers-info']//strong/text()").toString();
        Long val = Optional.ofNullable(followers).map(Long::parseLong).orElse(0L);

        Topic parentTopic = TopicTree.getTopicMap().get(parentTopicId);
        parentTopic.setFollowers(val);
        if (val > 20000) {
            Topic topic = new Topic();
            BeanUtil.copyProperties(parentTopic, topic, "subTopics");
            TopicTree.TOPIC_LIST.add(topic);
        }

        List<Selectable> links = page.getHtml().xpath("//div[@id='zh-topic-organize-child-editor']//a[@class='zm-item-tag']").nodes();
        if (links.isEmpty() || links.size() == 1) {
            return MatchOther.NO;
        }

        CopyOnWriteArrayList<Topic> topics = links.stream().map(selectable -> {
            String tid = selectable.xpath("//a/@data-token").toString();
            String topicName = selectable.xpath("//a/text()").toString();
            return new Topic().setTopicId(Long.parseLong(tid)).setTopicName(topicName).setParentId(parentTopicId);
        }).filter(topic -> !TopicTree.getTopicMap().containsKey(topic.getTopicId())).collect(Collectors.toCollection(CopyOnWriteArrayList::new));
        topics.stream().map(topic -> {
            Request request = new Request(
                    StrUtil.format(Zhihu.TOPIC_ORGANIZE_URL, topic.getTopicId())
            );
            request.addCookie("z_c0", cookie);
            return request;
        }).forEach(page::addTargetRequest);

        TopicTree.putNodeByParentId(topics, parentTopicId);
        return MatchOther.YES;
    }


    @Override
    public MatchOther processResult(ResultItems resultItems, Task task) {
        return null;
    }
}
