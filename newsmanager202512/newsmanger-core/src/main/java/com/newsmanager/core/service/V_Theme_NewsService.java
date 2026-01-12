package com.newsmanager.core.service;


import com.newsmanager.api.models.V_theme_news;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class V_Theme_NewsService {

    public List<V_theme_news> get(){
        return new V_theme_news().selectAll();
    }
}
