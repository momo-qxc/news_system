package com.newsmanager202512.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class newsmanagereureka7002 {
    public static void main(String[] args) {
        SpringApplication.run(newsmanagereureka7002.class, args);
    }
}