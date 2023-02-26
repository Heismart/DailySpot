package cn.grokit.dailyspot.entity.zhihu;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/18
 * @license MIT
 * @since 0.1.0
 */
@Data
@Accessors(chain = true)
public class Answer {

    /**
     * 点赞数
     */
    int voteUpCount;
    /**
     * 评论数
     */
    int commentCount;
    /**
     * 回答id
     */
    long answerId;
    /**
     * 问题id
     */
    long questionId;
    /**
     * 问题标题
     */
    String title;
    /**
     * 作者
     */
    String authorName;
    /**
     * 回答url
     */
    String answerUrl;
}
