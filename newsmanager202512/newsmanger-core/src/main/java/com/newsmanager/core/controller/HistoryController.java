package com.newsmanager.core.controller;

import com.newsmanager.api.models.HistoryModel;
import com.newsmanager.core.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "浏览历史接口")
@RestController
@RequestMapping("/history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @GetMapping("/get")
    @Operation(summary = "查询所有历史记录")
    public List<HistoryModel> get() {
        return historyService.get();
    }

    @GetMapping("/getdetail")
    @Operation(summary = "查询用户浏览历史详情")
    public List<java.util.Map<String, Object>> getByUserDetail(@RequestParam String phone) {
        // 1. 根据手机号获取用户ID
        com.newsmanager.api.models.UserModel user = new com.newsmanager.api.models.UserModel().selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.newsmanager.api.models.UserModel>()
                        .eq("phone", phone));
        if (user == null)
            return new java.util.ArrayList<>();

        // 2. 获取该用户的所有浏览历史，按时间降序
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<HistoryModel> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("uid", user.getUid()).orderByDesc("createdate");
        List<HistoryModel> historyList = new HistoryModel().selectList(qw);

        // 3. 组装详情数据
        List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        for (HistoryModel hm : historyList) {
            com.newsmanager.api.models.NewsModel news = new com.newsmanager.api.models.NewsModel()
                    .selectById(hm.getNid());
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("hid", hm.getHid());
            item.put("nid", hm.getNid());
            item.put("ntitle", news != null ? news.getNtitle() : "未知新闻");
            item.put("createdate", hm.getCreatedate());
            result.add(item);
        }
        return result;
    }

    @GetMapping("/getdetail_pager")
    @Operation(summary = "分页查询用户浏览历史详情")
    public com.newsmanager.api.models.PagerTemplate getByUserDetailPager(
            @RequestParam String phone,
            @RequestParam(defaultValue = "1") int pageno,
            @RequestParam(defaultValue = "10") int pagesize) {
        // 1. 根据手机号获取用户ID
        com.newsmanager.api.models.UserModel user = new com.newsmanager.api.models.UserModel().selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.newsmanager.api.models.UserModel>()
                        .eq("phone", phone));
        if (user == null)
            return new com.newsmanager.api.models.PagerTemplate(pageno, pagesize, 0, 0, new java.util.ArrayList<>());

        // 2. 分页获取该用户的浏览历史
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<HistoryModel> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                pageno, pagesize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<HistoryModel> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("uid", user.getUid()).orderByDesc("createdate");

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<HistoryModel> historyPage = new HistoryModel()
                .selectPage(page, qw);

        // 3. 组装详情数据
        List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        for (HistoryModel hm : historyPage.getRecords()) {
            com.newsmanager.api.models.NewsModel news = new com.newsmanager.api.models.NewsModel()
                    .selectById(hm.getNid());
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("hid", hm.getHid());
            item.put("nid", hm.getNid());
            item.put("ntitle", news != null ? news.getNtitle() : "未知新闻");
            item.put("createdate", hm.getCreatedate());
            result.add(item);
        }

        return new com.newsmanager.api.models.PagerTemplate(
                historyPage.getCurrent(),
                historyPage.getSize(),
                historyPage.getTotal(),
                historyPage.getPages(),
                result);
    }

    @GetMapping("/getone")
    @Operation(summary = "查询单条历史记录")
    // @Parameter(description = "历史ID")
    public HistoryModel getone(@RequestParam int hid) {
        return historyService.getone(hid);
    }

    @PostMapping("/save")
    @Operation(summary = "新增浏览历史")
    public void save(@ModelAttribute HistoryModel historyModel, @RequestParam(required = false) String phone) {
        if (historyModel.getUid() == null && phone != null) {
            com.newsmanager.api.models.UserModel user = new com.newsmanager.api.models.UserModel().selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.newsmanager.api.models.UserModel>()
                            .eq("phone", phone));
            if (user != null) {
                historyModel.setUid(user.getUid());
            }
        }
        historyService.save(historyModel);
    }

    @DeleteMapping("/del/{hid}")
    @Operation(summary = "删除某条历史记录")
    public void del(@PathVariable int hid) {
        historyService.del(new HistoryModel(hid));
    }

    @DeleteMapping("/clear")
    @Operation(summary = "清空指定用户的浏览历史")
    public void clearAll(@RequestParam String phone) {
        // 1. 根据手机号获取用户ID
        com.newsmanager.api.models.UserModel user = new com.newsmanager.api.models.UserModel().selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.newsmanager.api.models.UserModel>()
                        .eq("phone", phone));
        if (user != null) {
            // 2. 删除该用户的所有记录
            new HistoryModel().delete(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<HistoryModel>()
                    .eq("uid", user.getUid()));
        }
    }

    @PutMapping("/update")
    @Operation(summary = "修改历史记录")
    public void modify(HistoryModel historyModel) {
        historyService.modify(historyModel);
    }
}
