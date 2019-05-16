package com.example.javabasics.mq.code.springboot;

import io.swagger.annotations.Api;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;

@RestController
@RequestMapping("/mq")
@Api(value = "MqController", tags = {"消息队列接口文档"})
public class MqController {

    @Autowired
    private Producer producer;

    @PostMapping("prepare")
    public void contextLoads() {
        Destination destination = new ActiveMQQueue("mytest.queue");
        for (int i = 0; i < 10; i++) {
            producer.sendMessage(destination, "myname is chhliu!!!");
        }
    }
}
