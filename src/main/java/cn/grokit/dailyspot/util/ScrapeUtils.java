package cn.grokit.dailyspot.util;

import cn.grokit.dailyspot.constant.Zhihu;
import cn.hutool.core.util.StrUtil;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;

import java.util.HashMap;

/**
 * 爬虫辅助工具类
 *
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/18
 * @license MIT
 * @since 0.1.0
 */
@UtilityClass
public class ScrapeUtils {

    @Setter
    private Spider spider;

    public static void addReqyest(String url) {
        spider.addUrl(url);
    }

    public static void close() {
        spider.close();
    }

    public static Request assemblyBody(Long topicId, Integer offset) {
        String param = StrUtil.format(Zhihu.TOPIC_PARAM, topicId, offset);
        Request request = new Request(Zhihu.TOPIC_URL);
        request.setMethod("post");
        request.setRequestBody(
                HttpRequestBody.form(
                        new HashMap<String, Object>(4) {
                            {
                                put("method", "next");
                                put("params", param);
                            }
                        },
                        "utf-8"));
        request.setCharset("utf-8");
        return request;
    }


}
