package com.newsmanager.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.newsmanager.api.models.NoticeModel;
import com.newsmanager.core.mapper.INotice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NoticeService {

    @Autowired
    private INotice dao;

    public List<NoticeModel> get() {
        return new NoticeModel().selectAll();
    }

    public void save(NoticeModel noticeModel) {
        // 生成ID: 年月日时分秒 + 3位随机数
        String timestamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String randomNum = String.format("%03d", new java.util.Random().nextInt(1000));
        noticeModel.setNoid(timestamp + randomNum);
        noticeModel.setCreatedate(new Date().toLocaleString());
        dao.insert(noticeModel);
    }

    public void del(NoticeModel noticeModel) {
        QueryWrapper<NoticeModel> qw = new QueryWrapper<>();
        qw.eq("noid", noticeModel.getNoid());
        noticeModel.delete(qw);
    }

    public void modify(NoticeModel noticeModel) {
        UpdateWrapper<NoticeModel> uw = new UpdateWrapper<>();
        uw.eq("noid", noticeModel.getNoid());
        noticeModel.update(uw);
    }

    public NoticeModel getone(int nid) {
        return new NoticeModel().selectById(nid);
    }
}
