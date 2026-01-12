package com.newsmanager.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newsmanager.api.models.LikeModel;
import com.newsmanager.api.models.NewsModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "点赞相关接口")
@RestController
@RequestMapping("/news/like")
public class LikeController {

    @GetMapping("/check")
    @Operation(summary = "检查是否已点赞")
    public Map<String, Object> check(@RequestParam String phone, @RequestParam String nid) {
        QueryWrapper<LikeModel> qw = new QueryWrapper<>();
        qw.eq("phone", phone);
        qw.eq("nid", nid);
        LikeModel like = new LikeModel().selectOne(qw);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", like != null);
        result.put("lid", like != null ? like.getLid() : null);
        return result;
    }

    @PostMapping("/toggle")
    @Operation(summary = "切换点赞状态")
    public Map<String, Object> toggle(@RequestParam String phone, @RequestParam String nid) {
        QueryWrapper<LikeModel> qw = new QueryWrapper<>();
        qw.eq("phone", phone);
        qw.eq("nid", nid);
        LikeModel existing = new LikeModel().selectOne(qw);

        Map<String, Object> result = new HashMap<>();

        if (existing != null) {
            // 已点赞，取消点赞
            existing.deleteById();
            // 更新 tb_news.cnt - 1
            updateNewsCnt(nid, -1);
            result.put("liked", false);
            result.put("message", "已取消点赞");
        } else {
            // 未点赞，添加点赞
            LikeModel like = new LikeModel();
            like.setPhone(phone);
            like.setNid(nid);
            like.setCreatedate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            like.insert();
            // 更新 tb_news.cnt + 1
            updateNewsCnt(nid, 1);
            result.put("liked", true);
            result.put("message", "点赞成功");
        }
        return result;
    }

    private void updateNewsCnt(String nid, int delta) {
        NewsModel news = new NewsModel().selectById(nid);
        if (news != null) {
            Integer cnt = news.getCnt();
            news.setCnt((cnt != null ? cnt : 0) + delta);
            news.updateById();
        }
    }

    @GetMapping("/count")
    @Operation(summary = "获取新闻点赞数")
    public Map<String, Object> count(@RequestParam String nid) {
        NewsModel news = new NewsModel().selectById(nid);
        Map<String, Object> result = new HashMap<>();
        result.put("cnt", news != null ? (news.getCnt() != null ? news.getCnt() : 0) : 0);
        return result;
    }
}
