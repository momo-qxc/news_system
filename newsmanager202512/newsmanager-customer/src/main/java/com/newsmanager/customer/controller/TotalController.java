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

    @PostMapping("/ai/auditComments")
    @Operation(description = "AI 智能审核评论")
    public Object auditComments(@RequestBody Object contents) {
        return restTemplate.postForObject(SERVICE_PATH1 + "/total/ai/auditComments", contents, Object.class);
    }

    @PostMapping("/ai/auditAll")
    @Operation(description = "AI 智能审核所有待审核评论")
    public Object auditAll() {
        return restTemplate.postForObject(SERVICE_PATH1 + "/total/ai/auditAll", null, Object.class);
    }
}
