package cn.grokit.dailyspot.constant;

import java.io.File;

/**
 * 思否静态资源
 *
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/26
 * @license MIT
 * @since 0.1.0
 */
public class Segmentfault extends Common{
    public static final String SEGMENTFAULT_URL = "https://segmentfault.com/hottest";
    public static final String SEGMENTFAULT_MONTHLY_URL = SEGMENTFAULT_URL + "/monthly";
    public static final String SEGMENTFAULT_WEEKLY_URL = SEGMENTFAULT_URL + "/weekly";
    public static final String SEGMENTFAULT_DAILY_URL = SEGMENTFAULT_URL;

    public static final String TMP_PATH;
    public static final String TOP_TOPICS_FILENAME;
    static {
        TMP_PATH = TMP_ROOT_PATH + File.separator + "segmentfault";
        TOP_TOPICS_FILENAME = TOP_TOPICS_ROOT_PATH + File.separator + "topics.json";
    }
}
