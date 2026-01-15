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

@Service
public class CollectionService {

    @Autowired
    private ICollection dao;

    public List<CollectionModel> get(String phone) {
        QueryWrapper<CollectionModel> qw = new QueryWrapper<>();
        qw.eq("phone", phone);
        qw.orderByDesc("colid");
        return new CollectionModel().selectList(qw);
    }

    // 根据手机号分页获取收藏的新闻，支持关键词搜索
    public PagerTemplate getByPhone(String phone, int pageno, int pagesize, String keyword) {
        QueryWrapper<NewsModel> newsQw = new QueryWrapper<>();
        // 使用 inSql 锁定当前手机号收藏的所有 nid
        newsQw.inSql("nid", "select nid from tb_collection where phone = '" + phone + "'");

        if (keyword != null && !keyword.trim().isEmpty()) {
            newsQw.like("ntitle", keyword);
        }

        // 排序按新闻发布时间倒序（或者通过 join tb_collection 按 colid 倒序，
        // 这里简化处理，优先保证搜索功能的正确性）
        newsQw.orderByDesc("createdate");

        IPage<NewsModel> newsPage = new NewsModel().selectPage(new Page<>(pageno, pagesize), newsQw);

        PagerTemplate pt = new PagerTemplate();
        pt.setList(newsPage.getRecords());
        pt.setPageno(newsPage.getCurrent());
        pt.setPagesize(newsPage.getSize());
        pt.setTotal(newsPage.getTotal());
        pt.setTotalpage(newsPage.getPages());
        return pt;
    }

    // 根据手机号和分类ID分页获取收藏的新闻，支持关键词搜索
    public PagerTemplate getByPhoneAndTid(String phone, int tid, int pageno, int pagesize, String keyword) {
        QueryWrapper<NewsModel> newsQw = new QueryWrapper<>();
        newsQw.inSql("nid", "select nid from tb_collection where phone = '" + phone + "'");
        newsQw.eq("tid", tid);

        if (keyword != null && !keyword.trim().isEmpty()) {
            newsQw.like("ntitle", keyword);
        }

        newsQw.orderByDesc("createdate");

        IPage<NewsModel> newsPage = new NewsModel().selectPage(new Page<>(pageno, pagesize), newsQw);

        PagerTemplate pt = new PagerTemplate();
        pt.setList(newsPage.getRecords());
        pt.setPageno(newsPage.getCurrent());
        pt.setPagesize(newsPage.getSize());
        pt.setTotal(newsPage.getTotal());
        pt.setTotalpage(newsPage.getPages());
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

    // 分页获取收藏详情（包含新闻标题）
    public PagerTemplate getDetailPaginated(String phone, int pageno, int pagesize) {
        QueryWrapper<CollectionModel> qw = new QueryWrapper<>();
        qw.eq("phone", phone);
        qw.orderByDesc("colid");

        IPage<CollectionModel> pager = new Page<>(pageno, pagesize);
        IPage<CollectionModel> mypage = new CollectionModel().selectPage(pager, qw);

        List<java.util.Map<String, Object>> resultList = new java.util.ArrayList<>();
        for (CollectionModel col : mypage.getRecords()) {
            NewsModel news = new NewsModel().selectById(col.getNid());
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("colid", col.getColid());
            item.put("nid", col.getNid());
            item.put("createdate", col.getCreatedate());
            item.put("ntitle", news != null ? news.getNtitle() : "未知新闻");
            resultList.add(item);
        }

        PagerTemplate pt = new PagerTemplate();
        pt.setList(resultList);
        pt.setPageno(mypage.getCurrent());
        pt.setPagesize(mypage.getSize());
        pt.setTotal(mypage.getTotal());
        pt.setTotalpage(mypage.getPages());
        return pt;
    }
}
