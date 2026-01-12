package com.newsmanager.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.newsmanager.api.models.HistoryModel;
import com.newsmanager.core.mapper.IHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryService {

    @Autowired
    private IHistory dao;

    public List<HistoryModel> get() {
        return new HistoryModel().selectAll();
    }

    public void save(HistoryModel hm) {
        hm.insert();
    }

    public void del(HistoryModel historyModel) {
        QueryWrapper<HistoryModel> qw = new QueryWrapper<>();
        qw.eq("hid", historyModel.getHid());
        historyModel.delete(qw);
    }

    public void modify(HistoryModel historyModel) {
        UpdateWrapper<HistoryModel> uw = new UpdateWrapper<>();
        uw.eq("hid", historyModel.getHid());
        historyModel.update(uw);
    }

    public HistoryModel getone(int hid) {
        return new HistoryModel().selectById(hid);
    }
}
