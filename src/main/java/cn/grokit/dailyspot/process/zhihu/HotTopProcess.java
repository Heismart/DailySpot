package cn.grokit.dailyspot.process.zhihu;

import cn.grokit.dailyspot.entity.zhihu.HotTopVo;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static cn.grokit.dailyspot.constant.Zhihu.*;


/**
 * 知乎热榜爬取
 *
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/18
 * @license MIT
 * @since 0.1.0
 */
@Slf4j
public class HotTopProcess implements PageProcessor {

    @Override
    public void process(Page page) {
        HotTopVo hotTopVo = JSON.parseObject(page.getJson().get(), HotTopVo.class);
        generateDoc(hotTopVo);
    }

    /**
     * 生成归档文件
     *
     * @param hotTopVo 热榜数据
     */
    private void generateDoc(HotTopVo hotTopVo) {
        AtomicInteger count = new AtomicInteger(1);
        String doc =
                hotTopVo.getData().stream()
                        .map(
                                datum ->
                                        StrUtil.format(
                                                TOP_FORMAT,
                                                count.getAndIncrement(),
                                                datum.getTarget().getTitle(),
                                                StrUtil.format(QUESTION_URL, datum.getTarget().getId())
                                        )
                        ).collect(Collectors.joining("\n"));
        StringBuffer buffer = new StringBuffer();
        String date = DateUtil.date().toString(DatePattern.NORM_DATE_PATTERN);
        buffer
                .append("# ")
                .append(date)
                .append("\n")
                .append("共:")
                .append(count.get())
                .append("条\n")
                .append(doc);
        log.info("爬取知乎热榜:\n{}", buffer);
        String filePath = StrUtil.format(ARCHIVERS_FORMAT, PACKAGE_PATH, date);
        String jsonPath = StrUtil.format(JSON_FORMAT, PACKAGE_PATH, date);
        FileUtil.writeUtf8String(buffer.toString(), filePath);
        FileUtil.writeUtf8String(JSON.toJSONString(hotTopVo.getData(), JSONWriter.Feature.PrettyFormat), jsonPath);
    }

    @Override
    public Site getSite() {
        return Site.me()
                .setDomain(ZHIHU_URL)
                .setCharset(StandardCharsets.UTF_8.toString())
                .setRetryTimes(3)
                .setTimeOut(10000);
    }
}
