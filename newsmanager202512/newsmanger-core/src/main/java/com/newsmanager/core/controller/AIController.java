package com.newsmanager.core.controller;

import com.newsmanager.core.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/total")
@Tag(name = "AIController", description = "AI 智能审核接口")
@CrossOrigin // 允许跨域
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/ai/auditComments")
    @Operation(summary = "AI 智能审核评论 (列表)")
    public List<Map<String, Object>> auditComments(@RequestBody List<String> contents) {
        return aiService.auditComments(contents);
    }

    @PostMapping("/ai/auditAll")
    @Operation(summary = "AI 智能审核所有待审核评论")
    public Map<Integer, Map<String, Object>> auditAllPendingComments() {
        // 1. 获取所有待审核评论 (status = 0)
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.newsmanager.api.models.CommentModel> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("status", 0);
        // 使用 ActiveRecord 模式查询 (确保 CommentModel 已继承 Model)
        List<com.newsmanager.api.models.CommentModel> pendingComments = new com.newsmanager.api.models.CommentModel()
                .selectList(qw);

        // 2. 调用 AI 审核逻辑
        return aiService.auditCommentsWithCid(pendingComments);
    }
}
