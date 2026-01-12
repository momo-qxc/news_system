package com.newsmanager.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@EnableTransactionManagement
//@MapperScan(basePackages = "com.newsmanager.core.dao")
@SpringBootApplication
public class newsmanagercore {
    public static void main(String[] args) {
        SpringApplication.run(newsmanagercore.class, args);
    }
}
