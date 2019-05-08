package com.example.javabasics.threadpool.code;

import java.util.concurrent.CountDownLatch;

public class MaxThreadsDemo {

    public static void main(String[] args) {
        /*
            创建一万个线程，结合jdk工具，来监测内存以及cpu的运行情况
            如果创建线程非常多，会产生什么现象
         */
        //使用cmd启用
        CountDownLatch cd = new CountDownLatch(1);
        try {
            Thread.sleep(20000L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 10000; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        cd.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            System.out.println("i=  " + i);
        }
    }
}
