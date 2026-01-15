package com.newsmanager.core.controller;

import com.newsmanager.api.models.CommentModel;
import com.newsmanager.api.models.PagerTemplate;
import com.newsmanager.core.service.CommentService;
import com.newsmanager.core.service.SensitiveWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "评论相关接口")
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private SensitiveWordService sensitiveWordService;

    @GetMapping("/get")
    @Operation(summary = "分页查询所有评论（按时间倒序）")
    public PagerTemplate get(
            @RequestParam(defaultValue = "1") int pageno,
            @RequestParam(defaultValue = "10") int pagesize,
            @RequestParam(required = false) String sortProp,
            @RequestParam(required = false) String sortOrder) {
        return commentService.get(pageno, pagesize, sortProp, sortOrder);
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
    public Map<String, Object> save(@ModelAttribute CommentModel commentModel) {
        Map<String, Object> result = new HashMap<>();

        // 检查评论内容是否包含敏感词
        String sensitiveWord = sensitiveWordService.findSensitiveWord(commentModel.getContent());
        if (sensitiveWord != null) {
            result.put("success", false);
            result.put("message", "评论含有违禁词，请修改后重新提交");
            return result;
        }

        // 让数据库自动生成 cid
        commentModel.setCid(null);
        // 默认设置为待审核状态 (0)
        commentModel.setStatus(0);
        commentService.save(commentModel);

        result.put("success", true);
        result.put("message", "评论提交成功");
        return result;
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
