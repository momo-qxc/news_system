package com.newsmanager.core.controller;

import com.newsmanager.api.models.NoticeModel;
import com.newsmanager.core.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "公告相关接口")
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/get")
    @Operation(summary = "查询所有公告")
    public List<NoticeModel> get() {
        return noticeService.get();
    }

    @GetMapping("/getbytarget")
    @Operation(summary = "根据目标查询公告")
    public List<NoticeModel> getByTarget(@RequestParam int target) {
        // 返回指定目标的公告 + 所有人可见的公告(target=2)
        // 只返回 visible=1 的公告，按优先级(升序:1高2中3低)和时间(降序)排序
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<NoticeModel> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("visible", 1);
        qw.and(wrapper -> wrapper.eq("target", target).or().eq("target", 2));
        qw.orderByAsc("priority").orderByDesc("createdate");
        return new NoticeModel().selectList(qw);
    }

    @GetMapping("/getone")
    @Operation(summary = "查询单个公告")
    public NoticeModel getone(@RequestParam int noid) {
        return noticeService.getone(noid);
    }

    @PostMapping("/save")
    @Operation(summary = "新增公告")
    public void save(@ModelAttribute NoticeModel noticeModel) {
        noticeService.save(noticeModel);
    }

    @DeleteMapping("/del")
    @Operation(summary = "删除公告")
    public void del(@RequestParam String noid) {
        NoticeModel noticeModel = new NoticeModel();
        noticeModel.setNoid(noid);
        noticeService.del(noticeModel);
    }

    @PutMapping("/update")
    @Operation(summary = "修改公告信息")
    public void modify(@RequestParam String noid, @RequestParam String content, @RequestParam int target,
            @RequestParam(required = false, defaultValue = "1") int visible,
            @RequestParam(required = false, defaultValue = "2") int priority) {
        NoticeModel noticeModel = new NoticeModel();
        noticeModel.setNoid(noid);
        noticeModel.setContent(content);
        noticeModel.setTarget(target);
        noticeModel.setVisible(visible);
        noticeModel.setPriority(priority);
        noticeService.modify(noticeModel);
    }
}
