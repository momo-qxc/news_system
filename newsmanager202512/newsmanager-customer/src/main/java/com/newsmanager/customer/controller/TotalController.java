package com.newsmanager.customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static com.newsmanager.customer.common.GlobalConfig.SERVICE_PATH1;

@RestController
@RequestMapping("/news/total")
public class TotalController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/total")
    @Operation(description = "获取统计数据")
    public String getTotal() {
        return restTemplate.getForObject(SERVICE_PATH1 + "/total/total", String.class);
    }

    @GetMapping("/newsByTheme")
    @Operation(description = "按主题统计新闻数量")
    public String getNewsByTheme() {
        return restTemplate.getForObject(SERVICE_PATH1 + "/total/newsByTheme", String.class);
    }
}
