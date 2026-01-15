package com.newsmanager.core.controller;

import com.newsmanager.api.models.NewsModel;
import com.newsmanager.api.models.PagerTemplate;
import com.newsmanager.core.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "新闻文章接口")
@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/get")
    @Operation(summary = "分页查询新闻信息(仅审核通过)")
    public PagerTemplate get(@RequestParam long pageno, @RequestParam long pagesize) {
        return newsService.get(pageno, pagesize);
    }

    @GetMapping("/getAll")
    @Operation(summary = "分页查询所有新闻(管理后台)")
    public PagerTemplate getAll(
            @RequestParam long pageno,
            @RequestParam long pagesize,
            @RequestParam(required = false) String sortProp,
            @RequestParam(required = false) String sortOrder) {
        return newsService.getAll(pageno, pagesize, sortProp, sortOrder);
    }

    @GetMapping("/getone")
    @Operation(summary = "根据ID查询新闻")
    public NewsModel getone(@RequestParam String nid) {
        return newsService.getone(nid);
    }

    @GetMapping("/getbytid")
    @Operation(description = "根据tid查询新闻信息")
    public PagerTemplate getbytid(@RequestParam int pageno, @RequestParam int pagesize, @RequestParam int tid) {
        return newsService.getnewsbytid(pageno, pagesize, tid);
    }

    @GetMapping("/getbytitle")
    @Operation(summary = "根据标题查询新闻")
    public NewsModel getbytitle(@RequestParam String title) {
        return newsService.getbytitle(title);
    }

    @GetMapping("/getnewsbykeyword")
    @Operation(summary = "根据关键字查询新闻")
    public PagerTemplate getnewsbykeyword(@RequestParam int pageno, @RequestParam int pagesize,
            @RequestParam String keyword) {
        return newsService.getnewsbykeyword(pageno, pagesize, keyword);
    }

    @GetMapping("/getnewsbytidandkeyword")
    @Operation(summary = "根据分类和关键字查询新闻")
    public PagerTemplate getnewsbytidandkeyword(@RequestParam int pageno, @RequestParam int pagesize,
            @RequestParam int tid, @RequestParam String keyword) {
        return newsService.getnewsbytidandkeyword(pageno, pagesize, tid, keyword);
    }

    @PostMapping("/save")
    @Operation(summary = "新增新闻文章")
    public void save(@RequestBody NewsModel newsModel) {
        newsService.save(newsModel);
    }

    @DeleteMapping("/del/{nid}")
    @Operation(summary = "删除新闻文章")
    public void del(@PathVariable String nid) {
        newsService.del(new NewsModel(nid));
    }

    @PutMapping("/update")
    @Operation(summary = "修改新闻文章(全量)")
    public void modify(@RequestBody NewsModel newsModel) {
        newsService.modify(newsModel);
    }

    @PutMapping("/checknews")
    @Operation(summary = "修改新闻：更新审核状态(指定字段)")
    public void checknews(@RequestParam String nid, @RequestParam Integer status) {
        newsService.checknews(nid, status);
    }

    @PutMapping("/updatelcnt")
    @Operation(summary = "修改新闻：更新点赞数量(指定字段)")
    public void updatelcnt(@RequestParam String nid, @RequestParam Integer cnt) {
        newsService.updatelcnt(nid, cnt);
    }

    @GetMapping("/countByDate")
    @Operation(summary = "统计指定日期的新闻数量")
    public int countByDate(@RequestParam String date) {
        return newsService.countByDate(date);
    }

    @DeleteMapping("/deleteByDate")
    @Operation(summary = "删除指定日期的所有新闻（超级管理员）")
    public int deleteByDate(@RequestParam String date) {
        return newsService.deleteByDate(date);
    }

    @GetMapping("/getByDate")
    @Operation(summary = "按日期分页查询新闻（管理后台）")
    public PagerTemplate getByDate(
            @RequestParam long pageno,
            @RequestParam long pagesize,
            @RequestParam String date,
            @RequestParam(required = false) String sortProp,
            @RequestParam(required = false) String sortOrder) {
        return newsService.getByDate(pageno, pagesize, date, sortProp, sortOrder);
    }
}
