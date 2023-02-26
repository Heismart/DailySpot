package cn.grokit.dailyspot.thread;

import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 爬取自定义线程池，限制并发数
 *
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/18
 * @license MIT
 * @since 0.1.0
 */
@Slf4j
public class ScrapeThreadPool extends ThreadPoolExecutor {

    private final RateLimiter rateLimiter;

    public ScrapeThreadPool(int threadNum) {
        super(threadNum, threadNum << 3, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("Scrape-%d").build(),
                (r, executor) -> executor.execute(r)
        );
        this.rateLimiter = RateLimiter.create(threadNum);
    }

    /**
     * 描述：限制并发数
     *
     * @param t the thread that will run task {@code r}
     * @param r the task that will be executed
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        rateLimiter.acquire();
    }

}
