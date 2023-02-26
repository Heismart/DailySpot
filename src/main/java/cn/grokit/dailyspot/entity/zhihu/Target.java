package cn.grokit.dailyspot.entity.zhihu;

import lombok.Data;

import java.util.List;

/**
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/18
 * @license MIT
 * @since 0.1.0
 */
@Data
public class Target {

    private long answerCount;
    private Author author;
    private List<Long> boundTopicIds;
    private long commentCount;
    private long created;
    private String excerpt;
    private long followerCount;
    private long id;
    private boolean isFollowing;
    private String title;
    private String type;
    private String url;

}
