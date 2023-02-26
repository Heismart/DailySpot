package cn.grokit.dailyspot.constant;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 知乎常量
 *
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/18
 * @license MIT
 * @since 0.1.0
 */
@Slf4j
public class Zhihu extends Common {
    public static final String ZHIHU_URL = "https://www.zhihu.com";
    public static final String HOTSPOT_TOPICS_URL = ZHIHU_URL + "/api/v3/feed/topstory/hot-lists/total";
    public static final String QUESTION_URL = ZHIHU_URL + "/question/{}";
    public static final String TOPIC_URL = ZHIHU_URL + "/node/TopicsPlazzaListV2";
    public static final String TOP_FORMAT = "- {}、[{}]({})";
    public static final String ARCHIVERS_FORMAT = "{}/archives/{}.md";
    public static final String JSON_FORMAT = "{}/json/{}.json";
    public static final String TOPICS_PAGE_URL = ZHIHU_URL + "/topics";
    public static final String TOPIC_PARAM = "{\"topic_id\":{},\"offset\":{},\"hash_id\":\"\"}";
    public static final String TOPIC_ORGANIZE_URL = ZHIHU_URL + "/topic/%s/organize";
    public static final String TOPIC_PAGE_PATTERN = ZHIHU_URL + "/topic/\\d+/organize";
    public static final String TOPIC_ANSWER_FILE_NAME = Zhihu.PACKAGE_PATH + File.separator + "%s" + File.separator + "%s-%d.%s";
    public static final String ANSWER_URL = ZHIHU_URL + "/api/v4/topics/%d/feeds/essence?limit=50&offset=%d";
    public static final String ANSWER_PAGE_URL = ZHIHU_URL + "/question/{}/answer/{}";
    public static final String ANSWER_PATTERN = ZHIHU_URL + "/api/v4/topics/\\d+/feeds/essence\\?limit=\\d+&offset=\\d+";
    public static final String ANSWER_PAGE_PATTERN = ZHIHU_URL + "/question/\\d+/answer/\\d+";
    public static boolean topicState;

    public static final String TOP_TOPICS_FILENAME;
    public static final String TOPIC_FILENAME;
    public static final String TMP_PATH;

    static {
        TMP_PATH = TMP_ROOT_PATH + File.separator + "zhihu";
        TOPIC_FILENAME = TOPIC_ROOT_PATH + File.separator + "topic.json";
        TOP_TOPICS_FILENAME = TOP_TOPICS_ROOT_PATH + File.separator + "topics.json";
        topicState = FileUtil.exist(TOPIC_FILENAME);
    }
}
