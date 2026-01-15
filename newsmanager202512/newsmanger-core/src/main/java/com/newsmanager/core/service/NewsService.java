package com.newsmanager.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.newsmanager.api.models.NewsModel;
import com.newsmanager.api.models.PagerTemplate;
import com.newsmanager.core.mapper.INews;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NewsService {

    @Autowired
    private INews dao;

    public PagerTemplate get(long pageno, long pagesize) {
        QueryWrapper<NewsModel> qw = new QueryWrapper<>();
        qw.eq("status", 1); // 只显示已审核的新闻
        qw.orderByDesc("createdate");
        IPage<NewsModel> pager = new Page<>(pageno, pagesize);
        IPage<NewsModel> mypage = new NewsModel().selectPage(pager, qw);
        PagerTemplate pt = new PagerTemplate();
        pt.setList(mypage.getRecords());
        pt.setPageno(mypage.getCurrent());
        pt.setPagesize(mypage.getSize());
        pt.setTotal(mypage.getTotal());
        pt.setTotalpage(mypage.getPages());
        return pt;
    }

    public PagerTemplate getnewsbytid(int pageno, int pagesize, int tid) {
        QueryWrapper<NewsModel> qw = new QueryWrapper<>();
        qw.eq("tid", tid);
        qw.eq("status", 1); // 只显示已审核的新闻
        qw.orderByDesc("createdate"); // 按时间倒序排列
        IPage<NewsModel> pager = new Page<>(pageno, pagesize);
        IPage<NewsModel> mypage = new NewsModel().selectPage(pager, qw);

        PagerTemplate pt = new PagerTemplate();
        pt.setList(mypage.getRecords());
        pt.setPageno(mypage.getCurrent());
        pt.setPagesize(mypage.getSize());
        pt.setTotal(mypage.getTotal());
        pt.setTotalpage(mypage.getPages());

        return pt;
    }

    public PagerTemplate getnewsbykeyword(int pageno, int pagesize, String keyword) {
        QueryWrapper<NewsModel> qw = new QueryWrapper<>();
        qw.like("ntitle", keyword); // 模糊查询标题
        qw.eq("status", 1); // 只显示已审核的新闻
        qw.orderByDesc("createdate");

        IPage<NewsModel> pager = new Page<>(pageno, pagesize);
        IPage<NewsModel> mypage = new NewsModel().selectPage(pager, qw);

        PagerTemplate pt = new PagerTemplate();
        pt.setList(mypage.getRecords());
        pt.setPageno(mypage.getCurrent());
        pt.setPagesize(mypage.getSize());
        pt.setTotal(mypage.getTotal());
        pt.setTotalpage(mypage.getPages());
        return pt;
    }

    public PagerTemplate getnewsbytidandkeyword(int pageno, int pagesize, int tid, String keyword) {
        QueryWrapper<NewsModel> qw = new QueryWrapper<>();
        qw.eq("tid", tid); // 按照分类过滤
        qw.like("ntitle", keyword); // 模糊查询标题
        qw.eq("status", 1); // 只显示已审核的新闻
        qw.orderByDesc("createdate");

        IPage<NewsModel> pager = new Page<>(pageno, pagesize);
        IPage<NewsModel> mypage = new NewsModel().selectPage(pager, qw);

        PagerTemplate pt = new PagerTemplate();
        pt.setList(mypage.getRecords());
        pt.setPageno(mypage.getCurrent());
        pt.setPagesize(mypage.getSize());
        pt.setTotal(mypage.getTotal());
        pt.setTotalpage(mypage.getPages());
        return pt;
    }

    public void save(NewsModel newsModel) {
        if (newsModel.getNid() == null || newsModel.getNid().isEmpty()) {
            newsModel.setNid(UUID.randomUUID().toString());
        }
        newsModel.insert();
    }

    public void del(NewsModel newsModel) {
        QueryWrapper<NewsModel> qw = new QueryWrapper<>();
        qw.eq("nid", newsModel.getNid());
        newsModel.delete(qw);
    }

    public void modify(NewsModel newsModel) {
        UpdateWrapper<NewsModel> uw = new UpdateWrapper<>();
        uw.eq("nid", newsModel.getNid());
        newsModel.update(uw);
    }

    public NewsModel getone(String nid) {
        return new NewsModel().selectById(nid);
    }

    public void checknews(String nid, Integer status) {
        UpdateWrapper<NewsModel> uw = new UpdateWrapper<>();
        uw.eq("nid", nid).set("status", status);
        new NewsModel().update(uw);
    }

    public void updatelcnt(String nid, Integer cnt) {
        UpdateWrapper<NewsModel> uw = new UpdateWrapper<>();
        uw.eq("nid", nid).set("cnt", cnt);
        new NewsModel().update(uw);
    }

    // 管理后台使用：获取所有新闻（不过滤status）
    public PagerTemplate getAll(long pageno, long pagesize, String sortProp, String sortOrder) {
        QueryWrapper<NewsModel> qw = new QueryWrapper<>();

        if (sortProp != null && !sortProp.isEmpty()) {
            boolean isAsc = "ascending".equals(sortOrder);
            if ("createdate".equals(sortProp)) {
                qw.orderBy(true, isAsc, "STR_TO_DATE(REPLACE(createdate, '-', '/'), '%Y/%m/%d %H:%i:%s')");
            } else if ("status".equals(sortProp)) {
                qw.orderBy(true, isAsc, "status");
            } else {
                qw.orderBy(true, isAsc, "STR_TO_DATE(REPLACE(createdate, '-', '/'), '%Y/%m/%d %H:%i:%s')");
            }
        } else {
            qw.orderByDesc("STR_TO_DATE(REPLACE(createdate, '-', '/'), '%Y/%m/%d %H:%i:%s')");
        }

        IPage<NewsModel> pager = new Page<>(pageno, pagesize);
        IPage<NewsModel> mypage = new NewsModel().selectPage(pager, qw);
        PagerTemplate pt = new PagerTemplate();
        pt.setList(mypage.getRecords());
        pt.setPageno(mypage.getCurrent());
        pt.setPagesize(mypage.getSize());
        pt.setTotal(mypage.getTotal());
        pt.setTotalpage(mypage.getPages());
        return pt;
    }

    public NewsModel getbytitle(String title) {
        QueryWrapper<NewsModel> qw = new QueryWrapper<>();
        qw.eq("ntitle", title);
        return new NewsModel().selectOne(qw);
    }

    // 统计指定日期新闻数量（用于批量删除确认）
    public int countByDate(String date) {
        QueryWrapper<NewsModel> qw = new QueryWrapper<>();
        qw.apply("DATE(createdate) = {0}", date);
        return (int) new NewsModel().selectCount(qw);
    }

    // 删除指定日期所有新闻
    public int deleteByDate(String date) {
        QueryWrapper<NewsModel> qw = new QueryWrapper<>();
        qw.apply("DATE(createdate) = {0}", date);
        return dao.delete(qw);
    }

    // 按日期分页查询新闻（管理后台）
    public PagerTemplate getByDate(long pageno, long pagesize, String date, String sortProp, String sortOrder) {
        QueryWrapper<NewsModel> qw = new QueryWrapper<>();
        qw.apply("DATE(createdate) = {0}", date);

        if (sortProp != null && !sortProp.isEmpty()) {
            boolean isAsc = "ascending".equals(sortOrder);
            if ("createdate".equals(sortProp)) {
                qw.orderBy(true, isAsc, "createdate");
            } else if ("status".equals(sortProp)) {
                qw.orderBy(true, isAsc, "status");
            } else {
                qw.orderByDesc("createdate");
            }
        } else {
            qw.orderByDesc("createdate");
        }

        IPage<NewsModel> pager = new Page<>(pageno, pagesize);
        IPage<NewsModel> mypage = new NewsModel().selectPage(pager, qw);
        PagerTemplate pt = new PagerTemplate();
        pt.setList(mypage.getRecords());
        pt.setPageno(mypage.getCurrent());
        pt.setPagesize(mypage.getSize());
        pt.setTotal(mypage.getTotal());
        pt.setTotalpage(mypage.getPages());
        return pt;
    }
}
