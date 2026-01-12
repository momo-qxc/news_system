package com.newsmanager.core.controller;

import com.newsmanager.api.models.CommentModel;
import com.newsmanager.core.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "评论相关接口")
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/get")
    @Operation(summary = "查询所有评论")
    public List<CommentModel> get() {
        return commentService.get();
    }

    @GetMapping("/getbynid")
    @Operation(summary = "根据新闻ID查询评论")
    public List<CommentModel> getByNid(@RequestParam String nid) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CommentModel> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("nid", nid);
        qw.orderByDesc("createdate");
        return new CommentModel().selectList(qw);
    }

    @GetMapping("/getbynidwithuser")
    @Operation(summary = "根据新闻ID查询评论（包含用户名）")
    public java.util.List<java.util.Map<String, Object>> getByNidWithUser(
            @RequestParam String nid,
            @RequestParam(required = false) Integer currentUid) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CommentModel> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("nid", nid);
        qw.orderByDesc("createdate");
        List<CommentModel> comments = new CommentModel().selectList(qw);

        java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        for (CommentModel comment : comments) {
            Integer status = comment.getStatus();
            Integer commentUid = comment.getUid();

            // 过滤逻辑：status=1 所有人可见，status=0 仅评论者本人可见
            if (status == null || status == 0) {
                // 待审核评论，只有评论者本人可见
                if (currentUid == null || !currentUid.equals(commentUid)) {
                    continue; // 跳过，不显示给其他人
                }
            }

            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("cid", comment.getCid());
            map.put("uid", commentUid);
            map.put("content", comment.getContent());
            map.put("createdate", comment.getCreatedate());
            map.put("nid", comment.getNid());
            map.put("status", status != null ? status : 0);

            // 根据 uid 查询用户名
            if (commentUid != null && commentUid > 0) {
                com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.newsmanager.api.models.UserModel> uqw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
                uqw.eq("uid", commentUid);
                com.newsmanager.api.models.UserModel user = new com.newsmanager.api.models.UserModel().selectOne(uqw);
                if (user != null && user.getUsername() != null) {
                    map.put("username", user.getUsername());
                } else {
                    map.put("username", "匿名用户");
                }
            } else {
                map.put("username", "匿名用户");
            }
            result.add(map);
        }
        return result;
    }

    @GetMapping("/getone")
    @Operation(summary = "查询单条评论")
    // @Parameter(description = "评论ID")
    public CommentModel getone(@RequestParam String cid) {
        return commentService.getone(cid);
    }

    @PostMapping("/save")
    @Operation(summary = "新增评论")
    // @Parameter(description = "评论实体类")
    public void save(@ModelAttribute CommentModel commentModel) {
        // 让数据库自动生成 cid
        commentModel.setCid(null);
        // 默认待审核状态
        commentModel.setStatus(0);
        commentService.save(commentModel);
    }

    @DeleteMapping("/del/{cid}")
    @Operation(summary = "删除评论")
    public void del(@PathVariable Integer cid) {
        CommentModel comment = new CommentModel();
        comment.setCid(cid);
        commentService.del(comment);
    }

    @PutMapping("/update")
    @Operation(summary = "修改评论内容/状态")
    public void modify(CommentModel commentModel) {
        commentService.modify(commentModel);
    }
}
