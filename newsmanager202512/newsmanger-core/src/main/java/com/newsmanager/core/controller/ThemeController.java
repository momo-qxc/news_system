package com.newsmanager.core.controller;

import com.newsmanager.api.models.ThemeModel;
import com.newsmanager.core.service.ThemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "新闻主题接口")
@RestController
@RequestMapping("/theme")
public class ThemeController {
    @Autowired
    private ThemeService themeService;

    @GetMapping("/get")
    @Operation(summary = "查询新闻信息")
    public List<ThemeModel> get(){
        return themeService.get();
    }

    @GetMapping("/getone")
    @Operation(summary = "查询一条新闻主题")
    public ThemeModel getone(@RequestParam int tid){
        return themeService.getone(tid);
    }

    @PostMapping("/save")
    @Operation(summary = "新增新闻主题")
    public void save(@RequestBody ThemeModel themeModel){
        themeService.save(themeModel);
    }

    @DeleteMapping("/del/{tid}")
    @Operation(summary = "删除新闻主题")
    public void del(@PathVariable int tid){
        themeService.del(new ThemeModel(tid));
    }

    @PutMapping("/update")
    @Operation(summary = "修改新闻主题")
    public void modify(ThemeModel themeModel){
        themeService.modify(themeModel);
    }

}
