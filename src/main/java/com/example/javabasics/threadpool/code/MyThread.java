package com.example.javabasics.threadpool.code;

public class MyThread extends Thread {
    private FixedSizeThreadPool pool;

    @Override
    public void run() {
        System.out.println("实现线程的方式一");
    }

    public static void main(String[] args) {
        //创建方式一
        MyThread myThread = new MyThread();
        myThread.start();
        //创建方式二
        MyRunnable myRunnable = new MyRunnable();
        myRunnable.run();
    }

}
