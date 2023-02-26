package cn.grokit.dailyspot.entity.zhihu;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 话题树形模型
 *
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/18
 * @license MIT
 * @since 0.1.0
 */
@Data
@Accessors(chain = true)
public class Topic {

    private long topicId;
    private long parentId;
    private String topicName;
    private long followers;
    private CopyOnWriteArrayList<Topic> subTopics;
}
