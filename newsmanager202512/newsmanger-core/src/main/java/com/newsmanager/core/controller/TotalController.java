package com.newsmanager.core.controller;

import com.newsmanager.api.models.TotalModel;
import com.newsmanager.core.service.TotalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "统计接口")
@RestController
@RequestMapping("/total")
public class TotalController {

    @Autowired
    private TotalService totalService;

    @GetMapping("/total")
    @Operation(summary = "获取总体统计数据")
    public TotalModel get() {
        return totalService.get();
    }

    @GetMapping("/newsByTheme")
    @Operation(summary = "按主题统计新闻数量")
    public List<Map<String, Object>> getNewsByTheme() {
        return totalService.getNewsByTheme();
    }
}