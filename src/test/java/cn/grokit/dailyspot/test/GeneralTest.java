package cn.grokit.dailyspot.test;

import cn.grokit.dailyspot.constant.Zhihu;
import cn.grokit.dailyspot.thread.ScrapeThreadPool;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.LinkedListMultimap;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 *
 * @author Kenny Luo (grokit.cn/beqoo.cn)
 * @create 2023/2/18
 * @license MIT
 * @since 0.1.0
 */
public class GeneralTest {

    @Test
    public void testConstant(){
        System.out.println(Zhihu.PACKAGE_PATH);
    }


    @Test
    public void test() {
        System.out.println(String.format(Zhihu.TOPIC_PARAM, 123123, 12));
    }


    @Test
    public void spiltCookie() {
        LinkedListMultimap<String, Object> linkedListMultimap = LinkedListMultimap
                .<String, Object>create();
        linkedListMultimap.put("123", "123456");
        linkedListMultimap.put("123", "123456");
        linkedListMultimap.put("123", "456789");
        linkedListMultimap.put("123", "987654");
        linkedListMultimap.put("123", "321654");
        for (Map.Entry<String, Object> entry : linkedListMultimap.entries()) {
            System.out.println("key=" + entry.getKey() + "value=" + entry.getValue());
        }
    }


    @Test
    public void stream() throws InterruptedException {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        new Thread(() -> {
            while (true) {
                list.add(RandomUtil.randomString(5));
            }
        }).start();

        TimeUnit.SECONDS.sleep(5);

        List<String> strs = list.stream().filter(s -> s.startsWith("a")).collect(Collectors.toList());
        System.out.println(strs);
    }

    @Test
    public void test1() {
        ArrayList<String> list = new ArrayList<>();
        String s = Optional.ofNullable(list)
                .filter(arrayList -> arrayList.size() > 0)
                .map(arrayList -> arrayList.get(0))
                .orElseThrow(() -> new RuntimeException("数据为空"));
        System.out.println(s);
    }

    @Test
    public void threadTest() throws InterruptedException {
        ScrapeThreadPool screpeThreadPool = new ScrapeThreadPool(3);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 100; i++) {
            screpeThreadPool.submit(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println(DateUtil.now().toString() + ",threadName=" + Thread.currentThread().getName());
            });
        }
        countDownLatch.countDown();

        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }

}
