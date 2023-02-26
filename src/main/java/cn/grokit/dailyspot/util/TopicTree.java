package cn.grokit.dailyspot.util;

import cn.grokit.dailyspot.constant.Zhihu;
import cn.grokit.dailyspot.entity.zhihu.Topic;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 话题树形工具
 *
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/18
 * @license MIT
 * @since 0.1.0
 */
@Slf4j
public class TopicTree {

    public static final CopyOnWriteArrayList<Topic> ROOT_TOPICS = new CopyOnWriteArrayList<>();
    public static final CopyOnWriteArrayList<Topic> TOPIC_LIST = new CopyOnWriteArrayList<>();
    private static final ConcurrentHashMap<Long, Topic> TOPIC_MAP = new ConcurrentHashMap<>();

    private static void putTopicMap(Topic topic) {
        TOPIC_MAP.putIfAbsent(topic.getTopicId(), topic);
        if (CollectionUtil.isEmpty(topic.getSubTopics())) {
            return;
        }
        topic.getSubTopics().forEach(TopicTree::putTopicMap);
    }

    public static ConcurrentHashMap<Long, Topic> getTopicMap() {
        return TOPIC_MAP;
    }

    public static CopyOnWriteArrayList<Topic> getRootTopic() {
        return ROOT_TOPICS;
    }

    public static void setRootTopic(CopyOnWriteArrayList<Topic> topics) {
        ROOT_TOPICS.addAll(topics);
        ROOT_TOPICS.parallelStream().forEach(TopicTree::putTopicMap);
    }

    /**
     * 插入话题
     *
     * @param topic 当前话题
     * @return 是否成功
     */
    public static Boolean putNode(Topic topic) {
        if (!TOPIC_MAP.containsKey(topic.getTopicId())) {
            return false;
        }
        Topic parentTopic = TOPIC_MAP.get(topic.getParentId());
        //父话题不存在，则添加到根话题列表
        if (parentTopic == null) {
            topic.setParentId(0L);
            ROOT_TOPICS.add(topic);
            putTopicMap(topic);
            return true;
        }
        CopyOnWriteArrayList<Topic> parentTopicSubTopics = parentTopic.getSubTopics();
        if (parentTopicSubTopics == null) {
            parentTopicSubTopics = new CopyOnWriteArrayList<>();
            parentTopic.setSubTopics(parentTopicSubTopics);
        }
        parentTopicSubTopics.add(topic);
        TOPIC_MAP.putIfAbsent(topic.getTopicId(), topic);
        return true;
    }

    public static void putNodeByParentId(CopyOnWriteArrayList<Topic> list, Long parentId) {
        Topic parentTopic = TOPIC_MAP.get(parentId);
        if (parentId == null) {
            return;
        }
        CopyOnWriteArrayList<Topic> subTopics = parentTopic.getSubTopics();

        // 过滤重复topic
        CopyOnWriteArrayList<Topic> topics =
                StreamEx.of(list)
                        .distinct(Topic::getTopicId)
                        .filter(sp -> !TOPIC_MAP.containsKey(sp.getTopicId()))
                        .collect(Collectors.toCollection(CopyOnWriteArrayList::new));

        if (subTopics == null) {
            subTopics = topics;
            parentTopic.setSubTopics(subTopics);
        } else {
            subTopics.addAll(topics);
        }
        // 更新topicMap
        topics.parallelStream().forEach(TopicTree::putTopicMap);
    }

    /**
     * 递归查找父节点
     *
     * @param subTopics 子话题集合
     * @param topic     当前话题
     * @return 父话题
     */
    private static Topic findParentTopic(CopyOnWriteArrayList<Topic> subTopics, Topic topic) {
        if (CollectionUtil.isEmpty(subTopics)) {
            return null;
        }
        for (Topic subTopic : subTopics) {
            if (subTopic.getTopicId() == (topic.getParentId())) {
                return subTopic;
            } else {
                return findParentTopic(subTopic.getSubTopics(), topic);
            }
        }
        return null;
    }

    public static Boolean checkTopic() {
        if (!Zhihu.topicState) {
            log.info("《《《《《《《《《《《《《topic.json不存在》》》》》》》》》》》》》》》");
            return false;
        }
        log.info("《《《《《《《《《《《《《topic.json已存在》》》》》》》》》》》》》》》");
        try {
            CopyOnWriteArrayList<Topic> topics =
                    JSON.parseObject(
                            FileUtil.readUtf8String(Zhihu.TOPIC_FILENAME),
                            new TypeReference<CopyOnWriteArrayList<Topic>>() {
                            }.getType());
            TopicTree.setRootTopic(topics);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
