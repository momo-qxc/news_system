package com.newsmanager.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newsmanager.api.models.NewsModel;
import com.newsmanager.api.models.ThemeModel;
import com.newsmanager.api.models.TotalModel;
import com.newsmanager.api.models.UserModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TotalService {

    public TotalModel get() {
        TotalModel totalModel = new TotalModel();
        totalModel.setThemes(new ThemeModel().selectAll().size());
        totalModel.setNews(new NewsModel().selectAll().size());
        totalModel.setUsers(new UserModel().selectAll().size());
        return totalModel;
    }

    /**
     * 按主题统计新闻数量
     * 
     * @return 每个主题的名称和新闻数量列表
     */
    public List<Map<String, Object>> getNewsByTheme() {
        List<Map<String, Object>> result = new ArrayList<>();

        // 获取所有主题
        List<ThemeModel> themes = new ThemeModel().selectAll();

        for (ThemeModel theme : themes) {
            // 统计每个主题的新闻数量
            QueryWrapper<NewsModel> qw = new QueryWrapper<>();
            qw.eq("tid", theme.getTid());
            int count = new NewsModel().selectList(qw).size();

            Map<String, Object> item = new HashMap<>();
            item.put("category", theme.getTname());
            item.put("value", count);
            result.add(item);
        }

        return result;
    }
}