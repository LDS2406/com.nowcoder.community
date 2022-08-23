package com.nowcoder.community;

import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTest {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTest.class);

    // JDK普通线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    // JDK可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    // Spring普通线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    // Spring可执行定时任务的线程池
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private AlphaService alphaService;

/*
* 在main方法中启动一个线程，如果线程挂了，main不会立刻结束
* 在test方法中，启动的子线程和当前线程是并发的，test方法后面没有逻辑就立刻结束了，不管启动的线程有没有完成
* 在test方法启动完线程后等一下，等执行完再结束，让线程sleep一下
* */

    private void sleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // 1.JDK普通线程池
    @Test
    public void testExecutorService() {
        //需要给线程池一个任务来执行，会分配一个线程来执行任务，这个任务就是一个线程体，实现Runnable接口来提供线程体
        //任务是输出日志
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ExecutorService");
            }
        };

        for (int i = 0; i < 10; i++) {
            executorService.submit(task);//线程池会分配一个线程来执行线程体
        }

        sleep(10000);
    }

    // 2.JDK定时任务线程池
    @Test
    public void testScheduledExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ScheduledExecutorService");
            }
        };

        //可以设置时间间隔不断执行                              延迟时间            时间间隔        声明时间单位
        scheduledExecutorService.scheduleAtFixedRate(task, 10000, 1000, TimeUnit.MILLISECONDS);

        sleep(30000);
    }

    // 3.Spring普通线程池
    @Test
    public void testThreadPoolTaskExecutor() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ThreadPoolTaskExecutor");
            }
        };

        for (int i = 0; i < 10; i++) {
            taskExecutor.submit(task);
        }

        sleep(10000);
    }

    // 4.Spring定时任务线程池
    @Test
    public void testThreadPoolTaskScheduler() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ThreadPoolTaskScheduler");
            }
        };

        Date startTime = new Date(System.currentTimeMillis() + 10000);//设置开始执行时间
        taskScheduler.scheduleAtFixedRate(task, startTime, 1000);

        sleep(30000);
    }

    // 5.Spring普通线程池(简化)
    @Test
    public void testThreadPoolTaskExecutorSimple() {
        for (int i = 0; i < 10; i++) {
            alphaService.execute1();//在程序底层会以多线程的方式去调用这个线程体
        }

        sleep(10000);
    }

    // 6.Spring定时任务线程池(简化)
    @Test
    public void testThreadPoolTaskSchedulerSimple() {

        sleep(30000);
    }

}
