package com.newsmanager202512.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class newsmanagereureka7003 {
    public static void main(String[] args) {
        SpringApplication.run(newsmanagereureka7003.class, args);
    }
}