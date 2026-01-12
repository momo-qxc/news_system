package com.newsmanager.customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static com.newsmanager.customer.common.GlobalConfig.SERVICE_PATH1;

@RestController
@RequestMapping("/news/like")
public class LikeController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/check")
    @Operation(description = "检查是否已点赞")
    public String check(@RequestParam String phone, @RequestParam String nid) {
        return restTemplate.getForObject(
                SERVICE_PATH1 + "/news/like/check?phone=" + phone + "&nid=" + nid,
                String.class);
    }

    @PostMapping("/toggle")
    @Operation(description = "切换点赞状态")
    public String toggle(@RequestParam String phone, @RequestParam String nid) {
        return restTemplate.postForObject(
                SERVICE_PATH1 + "/news/like/toggle?phone=" + phone + "&nid=" + nid,
                null,
                String.class);
    }

    @GetMapping("/count")
    @Operation(description = "获取新闻点赞数")
    public String count(@RequestParam String nid) {
        return restTemplate.getForObject(
                SERVICE_PATH1 + "/news/like/count?nid=" + nid,
                String.class);
    }
}
