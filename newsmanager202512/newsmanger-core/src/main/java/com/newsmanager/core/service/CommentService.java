package com.newsmanager.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.newsmanager.api.models.CommentModel;
import com.newsmanager.core.mapper.IComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private IComment dao;

    public List<CommentModel> get() {
        return new CommentModel().selectAll();
    }

    public List<CommentModel> get(int nid){
        QueryWrapper<CommentModel> qw=new QueryWrapper<>();
        qw.eq("nid",nid);
        qw.orderByDesc("createdate");
        return new CommentModel().selectList(qw);
    }

    public void save(CommentModel cm) {
        cm.insert();
    }

    public void del(CommentModel commentModel) {
        QueryWrapper<CommentModel> qw = new QueryWrapper<>();
        qw.eq("cid", commentModel.getCid());
        commentModel.delete(qw);
    }

    public void modify(CommentModel commentModel) {
        UpdateWrapper<CommentModel> uw = new UpdateWrapper<>();
        uw.eq("cid", commentModel.getCid());
        commentModel.update(uw);
    }

    public CommentModel getone(String cid) {
        return new CommentModel().selectById(cid);
    }
}
