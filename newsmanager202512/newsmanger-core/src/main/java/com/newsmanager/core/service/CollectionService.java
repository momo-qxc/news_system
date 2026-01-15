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
        qw.orderByDesc("colid");
        return new CollectionModel().selectList(qw);
    }

    // 根据手机号分页获取收藏的新闻
    public PagerTemplate getByPhone(String phone, int pageno, int pagesize) {
        // 1. 分页查询收藏记录，按 colid 倒序
        QueryWrapper<CollectionModel> colQw = new QueryWrapper<>();
        colQw.eq("phone", phone);
        colQw.orderByDesc("colid");

        IPage<CollectionModel> colPager = new Page<>(pageno, pagesize);
        IPage<CollectionModel> colMyPage = new CollectionModel().selectPage(colPager, colQw);

        List<CollectionModel> pageCollections = colMyPage.getRecords();
        List<String> nids = pageCollections.stream().map(CollectionModel::getNid).collect(Collectors.toList());

        PagerTemplate pt = new PagerTemplate();
        pt.setPageno(colMyPage.getCurrent());
        pt.setPagesize(colMyPage.getSize());
        pt.setTotal(colMyPage.getTotal());
        pt.setTotalpage(colMyPage.getPages());

        if (nids.isEmpty()) {
            pt.setList(List.of());
            return pt;
        }

        // 2. 根据 NID 批量查询新闻，并保持顺顺序
        QueryWrapper<NewsModel> newsQw = new QueryWrapper<>();
        newsQw.in("nid", nids);
        List<NewsModel> newsList = new NewsModel().selectList(newsQw);

        // 按 nids 列表中的顺序重新排序（对应收藏时间）
        List<NewsModel> sortedList = nids.stream()
                .map(nid -> newsList.stream().filter(n -> n.getNid().equals(nid)).findFirst().orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        pt.setList(sortedList);
        return pt;
    }

    // 根据手机号和分类ID分页获取收藏的新闻
    public PagerTemplate getByPhoneAndTid(String phone, int tid, int pageno, int pagesize) {
        // 注意：按分类过滤通常意味着我们需要先拿到符合分类的新闻，再看哪些被收藏了，或者先拿收藏再过滤。
        // 为了保持“最新收藏”的排序，这里先拿收藏，再按分类过滤新闻
        QueryWrapper<CollectionModel> colQw = new QueryWrapper<>();
        colQw.eq("phone", phone);
        colQw.orderByDesc("colid");
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
        // 此处的排序如果按收藏时间，依然需要手动处理，但如果是按新闻发布时间则简单。
        // 这里为了统一，先按收藏顺序拿全集，然后进行分类过滤，会导致分页逻辑在 Java 中处理或变得复杂。
        // 简化处理：如果要按分类过滤且支持分页，通常倾向于按新闻时间排，或通过 JOIN 实现。
        // 这里先修正基本排序为按收藏时间（colid 倒序）

        List<NewsModel> allMatchNews = new NewsModel().selectList(newsQw);

        // 按照收藏顺序排序并过滤
        List<NewsModel> sortedList = nids.stream()
                .map(nid -> allMatchNews.stream().filter(n -> n.getNid().equals(nid)).findFirst().orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        int total = sortedList.size();
        int totalPage = (int) Math.ceil((double) total / pagesize);
        int start = (pageno - 1) * pagesize;
        int end = Math.min(start + pagesize, total);

        List<NewsModel> pageList = (start < total) ? sortedList.subList(start, end) : List.of();

        pt.setList(pageList);
        pt.setPageno(pageno);
        pt.setPagesize(pagesize);
        pt.setTotal(total);
        pt.setTotalpage(totalPage);
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
