package com.newsmanager.core.controller;


import com.newsmanager.api.models.V_theme_news;
import com.newsmanager.core.service.V_Theme_NewsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "统计接口")
@RestController
@RequestMapping("/v_theme_news")
public class V_Theme_NewsController {

    @Autowired
    private V_Theme_NewsService vThemeNewsService;

    @GetMapping("/get")
    public List<V_theme_news> get() {
        return vThemeNewsService.get();
    }
}