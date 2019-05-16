package com.example.javabasics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableJms
@EnableSwagger2
@SpringBootApplication
public class JavabasicsApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavabasicsApplication.class, args);
    }

}
