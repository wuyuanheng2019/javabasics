package com.example.javabasics.threadpool.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FixedSizeThreadPool {

    /**
     * 1、需要一个仓库
     * 仓库用来存放线程，循环利用
     */
    private BlockingQueue<Runnable> blockingQueue;

    /**
     * 2、需要一个放线程的集合
     * 通过观察，为什么使用list来存放线程，当队列中的线程弹出去后，
     * 在关闭的时候我们并不能够监控到该线程的状态，是否执行完成，或正在执行
     */
    private List<Thread> workers;

    /**
     * 3、需要一个码农来干活
     * 线程中需要处理什么事情（也就是业务逻辑）
     */
    public static class Worker extends Thread {
        //持有线程池对象
        private FixedSizeThreadPool pool;

        public Worker(FixedSizeThreadPool pool) {
            this.pool = pool;
        }

        @Override
        public void run() {
            //去仓库拿东西，把所有的东西都拿完（也就是执行队列中的方法）
            //线程池已经关闭 并且 队列中的数据全部弹出，那么不去执行
            while (this.pool.isWorking || this.pool.blockingQueue.size() > 0) {
                Runnable task = null;
                try {
                    //如果线程池没有关闭，那么使用阻塞方法（也就是等待下一个任务执行）
                    if (this.pool.isWorking) {
                        task = this.pool.blockingQueue.take();
                    } else {
                        //如果线程池已经关闭，那么调用返回特殊值的方法
                        task = this.pool.blockingQueue.poll();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (task != null) {
                    //调用线程的run方法，队列中存放的是线程
                    task.run();
                    System.out.println("线程" + Thread.currentThread().getName() + "  执行完毕");
                }
            }
        }
    }

    //4、需要初始化仓库和线程集合
    public FixedSizeThreadPool(int poolSize, int taskSize) {
        //线程池队列的大小，线程集合的大小（任务的大小）
        if (poolSize <= 0 || taskSize <= 0) {
            throw new IllegalArgumentException("非法参数！");
        }
        this.blockingQueue = new LinkedBlockingQueue<>(taskSize);
        this.workers = Collections.synchronizedList(new ArrayList<>());

        //在没有执行完之前，我们期望最多有多少个线程来执行任务
        for (int i = 0; i < poolSize; i++) {
            Worker worker = new Worker(this);
            //此时调用，队列中并没有数据
            worker.start();
            //添加到线程集合中
            workers.add(worker);
        }
    }

    //5、需要向仓库放任务的方法，不需要返回一个特殊值
    public boolean submit(Runnable task) {
        if (this.isWorking) {
            return this.blockingQueue.offer(task);
        } else {
            return false;
        }
    }

    //6、需要向仓库放任务的方法，阻塞
    public void excute(Runnable task) {
        try {
            this.blockingQueue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //7、需要一个关闭线程池的方法
    //a、关闭的时候，仓库要停止有新的线程进来
    //b、关闭的时候，如果仓库还有东西，我们需要执行完代码
    //c、关闭的时候如果去仓库拿东西，那么不能阻塞
    //d、关闭的时候，我们要把阻塞的线程全部中断
    private volatile boolean isWorking = true;

    public void shutDown() {
        this.isWorking = false;
        for (Thread thread : workers) {
            if (thread.getState().equals(Thread.State.BLOCKED) || thread.getState().equals(Thread.State.WAITING)) {
                thread.interrupt();
            }
        }
    }

    public static void main(String[] args) {
        //调用初始化方法，重点理解阻塞
        FixedSizeThreadPool pool = new FixedSizeThreadPool(3, 6);
        for (int i = 0; i < 6; i++) {
            pool.submit(
                    //创建没匿名内部类，放入队列中
                    new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("一个线程被放入了我们的仓库中！");
                            try {
                                //线程休眠
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                System.out.println("一个线程被唤醒了！");
                            }
                        }
                    });
        }
        //如果没有调用关闭线程池的方法，此时线程是没有销毁的
        //一直在占用内存空间
        pool.shutDown();
    }
}
