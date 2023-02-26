package cn.grokit.dailyspot.constant;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 通用静态资源
 *
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/26
 * @license MIT
 * @since 0.1.0
 */
@Slf4j
public class Common {
    public static final String PACKAGE_PATH;
    public static final String TMP_ROOT_PATH;
    public static final String TOPIC_ROOT_PATH;
    public static final String TOP_TOPICS_ROOT_PATH;

    static {
        String resourcePath = Common.class.getClassLoader().getResource("").getPath();
        if (resourcePath.contains("build")) {
            PACKAGE_PATH = StrUtil.subBefore(resourcePath, "build", true) + "data";
        } else {
            PACKAGE_PATH = StrUtil.subBefore(resourcePath, "out", true) + "data";
        }
        TMP_ROOT_PATH = PACKAGE_PATH + File.separator + "tmp";
        TOPIC_ROOT_PATH = PACKAGE_PATH + File.separator + "topic";
        TOP_TOPICS_ROOT_PATH = TOPIC_ROOT_PATH + File.separator + "top";
    }

}
