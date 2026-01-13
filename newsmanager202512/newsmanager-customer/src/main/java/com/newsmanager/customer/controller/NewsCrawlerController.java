package com.newsmanager.customer.controller;

import com.newsmanager.customer.service.NewsCrawlerService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/news/crawler")
public class NewsCrawlerController {

    @Autowired
    private NewsCrawlerService crawlerService;

    @PostMapping("/run")
    @Operation(description = "启动新闻爬虫")
    public String run(@RequestBody Map<String, Object> params) {
        if (crawlerService.isRunning()) {
            return "任务正在运行中，请勿重复启动";
        }

        Integer tid = null;
        if (params.get("tid") != null) {
            try {
                tid = Integer.valueOf(params.get("tid").toString());
            } catch (Exception e) {
            }
        }

        Integer limit = 10;
        if (params.get("limit") != null) {
            try {
                limit = Integer.valueOf(params.get("limit").toString());
            } catch (Exception e) {
            }
        }

        crawlerService.runCrawler(tid, limit);
        return "任务已成功启动";
    }

    @GetMapping("/logs")
    @Operation(description = "获取采集日志")
    public List<String> getLogs() {
        return crawlerService.getLogs();
    }

    @GetMapping("/status")
    @Operation(description = "获取运行状态")
    public boolean getStatus() {
        return crawlerService.isRunning();
    }

    @PostMapping("/clear-logs")
    @Operation(description = "清空日志")
    public void clearLogs() {
        crawlerService.clearLogs();
    }
}
