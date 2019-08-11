package com.example;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

public class SpringBootDemoTests {

    @Test
    public void testQueue() {
        Queue<String> queue = new LinkedTransferQueue<>();
        queue.offer("a");
        queue.offer("b");
        queue.offer("c");
        queue.offer("d");
        queue.offer("e");
        for (String str : queue) {
            System.out.println(queue.poll());
        }
        System.out.println(queue);
        System.out.println(queue.poll());
    }

    @Test
    public void testPathMatcher() {
        PathMatcher matcher = new AntPathMatcher();
        System.out.println(matcher.match("/**", ""));
    }

    @Test
    public void testEquals() {
        System.out.println(Objects.equals(null, null));
    }

    @Test
    public void testQueryWrapper() {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_id", 1);
        wrapper.eq("role_id", 1);
        wrapper.or();
        wrapper.eq("user_id", 2);
        wrapper.eq("role_id", 3);
        wrapper.or();
        System.out.println(wrapper.getCustomSqlSegment());
    }

    @Test
    public void testRandomString() {
        System.out.println(RandomStringUtils.randomAlphanumeric(6));
    }

    @Test
    public void testSplit() {
        System.out.println(Arrays.toString("574290057@qq.com".split("@")));
    }

    @Test
    public void testTime() {
        // 获得系统的时间，单位为毫秒,转换为妙
        long totalMilliSeconds = System.currentTimeMillis();
        long totalSeconds = totalMilliSeconds/1000;
        // 求出现在的秒
        long currentSecond = totalSeconds%60;
        // 求出现在的分
        long totalMinutes = totalSeconds/60;
        long currentMinute = totalMinutes%60;
        // 求出现在的小时
        long totalHour = totalMinutes/60;
        long currentHour = totalHour%24;
        // 显示时间
        System.out.println("总毫秒为： "+totalMilliSeconds);
        System.out.println(currentHour+":"+currentMinute+":"+currentSecond+" GMT");
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date(totalMilliSeconds)));
    }

}
