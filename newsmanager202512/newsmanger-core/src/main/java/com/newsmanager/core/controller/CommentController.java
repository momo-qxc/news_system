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

    @GetMapping("/getbynidwithtree")
    @Operation(summary = "根据新闻ID查询评论树（嵌套结构）")
    public List<CommentModel> getByNidWithTree(
            @RequestParam String nid,
            @RequestParam(required = false) Integer currentUid) {
        return commentService.getbynidwithtree(nid, currentUid);
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
        // 为了测试和即时可见，暂时默认设置为已通过状态 (1)
        commentModel.setStatus(1);
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

    @PostMapping("/like")
    @Operation(summary = "点赞评论")
    public void like(@RequestParam Integer cid, @RequestParam Integer uid) {
        commentService.likeComment(cid, uid);
    }

    @PostMapping("/unlike")
    @Operation(summary = "取消点赞")
    public void unlike(@RequestParam Integer cid, @RequestParam Integer uid) {
        commentService.unlikeComment(cid, uid);
    }
}
