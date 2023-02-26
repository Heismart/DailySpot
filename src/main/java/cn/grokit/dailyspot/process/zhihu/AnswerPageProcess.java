package cn.grokit.dailyspot.process.zhihu;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.handler.PatternProcessor;

/**
 * 爬取知乎文章回答 （暂不实现）
 *
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/18
 * @license MIT
 * @since 0.1.0
 */
public class AnswerPageProcess extends PatternProcessor {
    /**
     * @param pattern url pattern to handle
     */
    public AnswerPageProcess(String pattern) {
        super(pattern);
    }

    @Override
    public MatchOther processPage(Page page) {
        return null;
    }

    @Override
    public MatchOther processResult(ResultItems resultItems, Task task) {
        return null;
    }
}
