package com.example.javabasics.mq.code;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class JmsProducer {

    //默认连接用户名
    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
    //默认连接密码
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    //默认连接地址
    private static final String BROKEURL = ActiveMQConnection.DEFAULT_BROKER_URL;
    //发送的消息数量
    private static final int SENDNUM = 10;

    public static void main(String[] args) {
        ConnectionFactory connectionFactory;
        Connection connection = null;
        Session session;
        Destination destination;
        MessageProducer messageProducer;

        connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKEURL);

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            /*
            createSession参数取值
            * 1、为true表示启用事务
            * 2、消息的确认模式
            * AUTO_ACKNOWLEDGE  自动签收
            * CLIENT_ACKNOWLEDGE 客户端自行调用acknowledge方法签收
            * DUPS_OK_ACKNOWLEDGE 不是必须签收，消费可能会重复发送
            * 在第二次重新传送消息的时候，消息
               头的JmsDelivered会被置为true标示当前消息已经传送过一次，
               客户端需要进行消息的重复处理控制。
            * */
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue("HelloWAM");
            messageProducer = session.createProducer(destination);
            for (int i = 0; i < SENDNUM; i++) {
                String msg = "发送消息" + i + " " + System.currentTimeMillis();
                TextMessage message = session.createTextMessage(msg);
                System.out.println("发送消息:" + msg);
                messageProducer.send(message);
            }
            session.commit();


        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }


    }


}
