package com.newsmanager.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.newsmanager.api.models.NewsModel;
import com.newsmanager.api.models.ThemeModel;
import com.newsmanager.core.mapper.ITheme;
import com.newsmanager.core.mapper.INews;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ThemeService {

    @Autowired
    private ITheme dao;

    @Autowired
    private INews newsDao;

    public List<ThemeModel> get() {
        return new ThemeModel().selectAll();
    }

    public void save(ThemeModel tm) {
        tm.insert();
    }

    @Transactional
    public void del(ThemeModel themeModel) {
        // 先删除该主题下的所有新闻
        QueryWrapper<NewsModel> newsQw = new QueryWrapper<>();
        newsQw.eq("tid", themeModel.getTid());
        newsDao.delete(newsQw);

        // 再删除主题
        QueryWrapper<ThemeModel> qw = new QueryWrapper<>();
        qw.eq("tid", themeModel.getTid());
        themeModel.delete(qw);
    }

    public void modify(ThemeModel themeModel) {
        UpdateWrapper<ThemeModel> uw = new UpdateWrapper<>();
        uw.eq("tid", themeModel.getTid());
        themeModel.update(uw);
    }

    public ThemeModel getone(int tid) {
        return new ThemeModel().selectById(tid);
    }
}
