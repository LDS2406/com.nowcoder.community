package com.nowcoder.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTests {
    public static void main(String[] args) {
        //实例化阻塞队列，生产者消费者共有一个阻塞队列
        BlockingQueue queue = new ArrayBlockingQueue(10);//队列的容量
        new Thread(new Producer(queue)).start();
        new Thread(new Consume(queue)).start();
        new Thread(new Consume(queue)).start();
        new Thread(new Consume(queue)).start();
    }
}

//生产者线程
class Producer implements Runnable{
//当实例化这个线程的时候，要求调用方将阻塞队列传入，因为这个线程要交给阻塞队列管理
    private BlockingQueue<Integer> queue;//变量用来接收传入的阻塞队列
    //在实例化Producer这个类的时候将queue传入
    public Producer(BlockingQueue<Integer> queue){//有参的构造器
        this.queue = queue;
    }
    @Override
    public void run() {
        try {
            //生产100个数据
            for (int i = 0; i < 100; i++){
                Thread.sleep(20);//停顿20ms，每20ms生产一个数据i，把i交给队列管理
                queue.put(i);       //当前线程名
                System.out.println(Thread.currentThread().getName() + "生产：" + queue.size());//生产完了一个数据后队列中有多少数据
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

//消费者线程
class Consume implements Runnable{
    //当实例化这个线程的时候，要求调用方将阻塞队列传入，因为这个线程要交给阻塞队列管理
    private BlockingQueue<Integer> queue;//变量用来接收传入的阻塞队列
    //在实例化Producer这个类的时候将queue传入
    public Consume(BlockingQueue<Integer> queue){//有参的构造器
        this.queue = queue;
    }
    @Override
    public void run() {
        try {
            //只要有数据就一直消费
            while (true){
                //每次循环获取数据
                Thread.sleep(new Random().nextInt(1000));
                queue.take();//使用数据
                System.out.println(Thread.currentThread().getName() + "消费：" + queue.size());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}