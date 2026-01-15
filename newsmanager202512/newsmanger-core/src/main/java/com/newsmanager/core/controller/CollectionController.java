package com.newsmanager.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newsmanager.api.models.CollectionModel;
import com.newsmanager.api.models.NewsModel;
import com.newsmanager.api.models.PagerTemplate;
import com.newsmanager.core.service.CollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "收藏相关接口")
@RestController
@RequestMapping("/news/collection")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @GetMapping("/get")
    @Operation(summary = "查询所有收藏")
    public List<NewsModel> get(String phone) {
        QueryWrapper<CollectionModel> qw = new QueryWrapper<>();
        qw.eq("phone", phone);
        qw.orderByDesc("colid");
        List<CollectionModel> list = new CollectionModel().selectList(qw);
        List newsids = new ArrayList();
        for (CollectionModel collectionModel : list) {
            newsids.add(collectionModel.getNid());
        }

        QueryWrapper qw2 = new QueryWrapper();
        qw2.in("nid", newsids);
        return new NewsModel().selectList(qw2);
    }

    @GetMapping("/getdetail")
    @Operation(summary = "查询收藏详情（包含新闻标题和收藏时间）")
    public List<java.util.Map<String, Object>> getDetail(String phone) {
        QueryWrapper<CollectionModel> qw = new QueryWrapper<>();
        qw.eq("phone", phone);
        qw.orderByDesc("colid");
        List<CollectionModel> collectionList = new CollectionModel().selectList(qw);

        List<java.util.Map<String, Object>> result = new ArrayList<>();
        for (CollectionModel col : collectionList) {
            NewsModel news = new NewsModel().selectById(col.getNid());
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("colid", col.getColid());
            item.put("nid", col.getNid());
            item.put("createdate", col.getCreatedate());
            item.put("ntitle", news != null ? news.getNtitle() : "未知新闻");
            result.add(item);
        }
        return result;
    }

    @GetMapping("/getdetailpaginated")
    @Operation(summary = "分页查询收藏详情（包含标题和时间）")
    public PagerTemplate getDetailPaginated(@RequestParam String phone, @RequestParam int pageno,
            @RequestParam int pagesize) {
        return collectionService.getDetailPaginated(phone, pageno, pagesize);
    }

    @GetMapping("/getbyphone")
    @Operation(summary = "根据手机号分页查询收藏的新闻")
    public PagerTemplate getByPhone(@RequestParam String phone, @RequestParam int pageno, @RequestParam int pagesize) {
        return collectionService.getByPhone(phone, pageno, pagesize);
    }

    @GetMapping("/getbyphoneandtid")
    @Operation(summary = "根据手机号和分类ID分页查询收藏的新闻")
    public PagerTemplate getByPhoneAndTid(@RequestParam String phone, @RequestParam int tid, @RequestParam int pageno,
            @RequestParam int pagesize) {
        return collectionService.getByPhoneAndTid(phone, tid, pageno, pagesize);
    }

    @GetMapping("/getone")
    @Operation(summary = "查询单项收藏")
    public CollectionModel getone(@RequestParam String colid) {
        return collectionService.getone(colid);
    }

    @PostMapping("/save")
    @Operation(summary = "新增收藏")
    public void save(CollectionModel collectionModel) {
        QueryWrapper<CollectionModel> qw = new QueryWrapper<>();
        qw.eq("nid", collectionModel.getNid());
        qw.eq("phone", collectionModel.getPhone());
        int cnt = collectionModel.selectList(qw).size();
        if (cnt == 0) {
            // 确保 colid 为 null，让数据库自动递增
            collectionModel.setColid(null);
            collectionModel.insert();
        }
    }

    @DeleteMapping("/del/{colid}")
    @Operation(summary = "删除收藏")
    public void del(@PathVariable Integer colid) {
        collectionService.del(new CollectionModel(colid));
    }

    @PutMapping("/update")
    @Operation(summary = "修改收藏记录")
    public void modify(CollectionModel collectionModel) {
        collectionService.modify(collectionModel);
    }
}
