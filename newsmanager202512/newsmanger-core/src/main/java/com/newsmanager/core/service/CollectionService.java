package com.newsmanager.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.newsmanager.api.models.CollectionModel;
import com.newsmanager.api.models.NewsModel;
import com.newsmanager.api.models.PagerTemplate;
import com.newsmanager.core.mapper.ICollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollectionService {

    @Autowired
    private ICollection dao;

    public List<CollectionModel> get(String phone) {
        QueryWrapper<CollectionModel> qw = new QueryWrapper<>();
        qw.eq("phone", phone);
        return new CollectionModel().selectList(qw);
    }

    // 根据手机号分页获取收藏的新闻
    public PagerTemplate getByPhone(String phone, int pageno, int pagesize) {
        // 先获取该用户所有收藏的新闻ID
        QueryWrapper<CollectionModel> colQw = new QueryWrapper<>();
        colQw.eq("phone", phone);
        List<CollectionModel> collections = new CollectionModel().selectList(colQw);
        List<String> nids = collections.stream().map(CollectionModel::getNid).collect(Collectors.toList());

        PagerTemplate pt = new PagerTemplate();
        if (nids.isEmpty()) {
            pt.setList(List.of());
            pt.setPageno(pageno);
            pt.setPagesize(pagesize);
            pt.setTotal(0);
            pt.setTotalpage(0);
            return pt;
        }

        // 分页查询这些新闻
        QueryWrapper<NewsModel> newsQw = new QueryWrapper<>();
        newsQw.in("nid", nids);
        newsQw.orderByDesc("createdate");
        IPage<NewsModel> pager = new Page<>(pageno, pagesize);
        IPage<NewsModel> mypage = new NewsModel().selectPage(pager, newsQw);

        pt.setList(mypage.getRecords());
        pt.setPageno(mypage.getCurrent());
        pt.setPagesize(mypage.getSize());
        pt.setTotal(mypage.getTotal());
        pt.setTotalpage(mypage.getPages());
        return pt;
    }

    // 根据手机号和分类ID分页获取收藏的新闻
    public PagerTemplate getByPhoneAndTid(String phone, int tid, int pageno, int pagesize) {
        // 先获取该用户所有收藏的新闻ID
        QueryWrapper<CollectionModel> colQw = new QueryWrapper<>();
        colQw.eq("phone", phone);
        List<CollectionModel> collections = new CollectionModel().selectList(colQw);
        List<String> nids = collections.stream().map(CollectionModel::getNid).collect(Collectors.toList());

        PagerTemplate pt = new PagerTemplate();
        if (nids.isEmpty()) {
            pt.setList(List.of());
            pt.setPageno(pageno);
            pt.setPagesize(pagesize);
            pt.setTotal(0);
            pt.setTotalpage(0);
            return pt;
        }

        // 分页查询这些新闻，并按分类筛选
        QueryWrapper<NewsModel> newsQw = new QueryWrapper<>();
        newsQw.in("nid", nids);
        newsQw.eq("tid", tid);
        newsQw.orderByDesc("createdate");
        IPage<NewsModel> pager = new Page<>(pageno, pagesize);
        IPage<NewsModel> mypage = new NewsModel().selectPage(pager, newsQw);

        pt.setList(mypage.getRecords());
        pt.setPageno(mypage.getCurrent());
        pt.setPagesize(mypage.getSize());
        pt.setTotal(mypage.getTotal());
        pt.setTotalpage(mypage.getPages());
        return pt;
    }

    public void save(CollectionModel cm) {
        cm.insert();
    }

    public void del(CollectionModel collectionModel) {
        QueryWrapper<CollectionModel> qw = new QueryWrapper<>();
        qw.eq("colid", collectionModel.getColid());
        collectionModel.delete(qw);
    }

    public void modify(CollectionModel collectionModel) {
        UpdateWrapper<CollectionModel> uw = new UpdateWrapper<>();
        uw.eq("colid", collectionModel.getColid());
        collectionModel.update(uw);
    }

    public CollectionModel getone(String colid) {
        return new CollectionModel().selectById(colid);
    }
}
