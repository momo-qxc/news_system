package com.newsmanager.customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static com.newsmanager.customer.common.GlobalConfig.SERVICE_PATH1;

@RestController
@RequestMapping("/news/newsinfo")
public class NewsController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/get")
    @Operation(description = "分页查询新闻信息(仅审核通过)")
    public String get(@RequestParam int pageno, @RequestParam int pagesize) {
        return restTemplate.getForObject(SERVICE_PATH1 + "/news/get?pageno=" + pageno + "&pagesize=" + pagesize,
                String.class);
    }

    @GetMapping("/getAll")
    @Operation(description = "分页查询所有新闻(管理后台)")
    public String getAll(@RequestParam int pageno, @RequestParam int pagesize,
            @RequestParam(required = false) String sortProp,
            @RequestParam(required = false) String sortOrder) {
        String url = SERVICE_PATH1 + "/news/getAll?pageno=" + pageno + "&pagesize=" + pagesize;
        if (sortProp != null)
            url += "&sortProp=" + sortProp;
        if (sortOrder != null)
            url += "&sortOrder=" + sortOrder;
        return restTemplate.getForObject(url, String.class);
    }

    //
    @GetMapping("/getone")
    @Operation(description = "根据ID查询新闻")
    public String getone(@RequestParam String nid) {
        return restTemplate.getForObject(SERVICE_PATH1 + "/news/getone?nid=" + nid, String.class);
    }

    @GetMapping("/getbytid")
    @Operation(description = "根据tid查询新闻信息")
    public String getbytid(@RequestParam int pageno, @RequestParam int pagesize, @RequestParam int tid) {
        return restTemplate.getForObject(
                SERVICE_PATH1 + "/news/getbytid?pageno=" + pageno + "&pagesize=" + pagesize + "&tid=" + tid,
                String.class);
    }

    //
    @GetMapping("/getnewsbykeyword")
    @Operation(description = "根据关键字查询新闻")
    public String getnewsbykeyword(@RequestParam int pageno, @RequestParam int pagesize, @RequestParam String keyword) {
        return restTemplate.getForObject(SERVICE_PATH1 + "/news/getnewsbykeyword?pageno=" + pageno + "&pagesize="
                + pagesize + "&keyword=" + keyword, String.class);
    }

    @GetMapping("/getnewsbytidandkeyword")
    @Operation(description = "根据分类和关键字查询新闻")
    public String getnewsbytidandkeyword(@RequestParam int pageno, @RequestParam int pagesize, @RequestParam int tid,
            @RequestParam String keyword) {
        return restTemplate.getForObject(SERVICE_PATH1 + "/news/getnewsbytidandkeyword?pageno=" + pageno + "&pagesize="
                + pagesize + "&tid=" + tid + "&keyword=" + keyword, String.class);
    }

    @DeleteMapping("/del/{nid}")
    @Operation(description = "删除新闻文章")
    public void del(@PathVariable String nid) {
        restTemplate.delete(SERVICE_PATH1 + "/news/del/" + nid);
    }

    @PutMapping("/checknews")
    @Operation(description = "修改新闻：更新审核状态")
    public void checknews(@RequestParam String nid, @RequestParam Integer status) {
        restTemplate.put(SERVICE_PATH1 + "/news/checknews?nid=" + nid + "&status=" + status, null);
    }

    @PostMapping("/save")
    @Operation(description = "新增新闻文章")
    public void save(@RequestBody com.newsmanager.api.models.NewsModel newsModel) {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        org.springframework.http.HttpEntity<com.newsmanager.api.models.NewsModel> entity = new org.springframework.http.HttpEntity<>(
                newsModel, headers);
        restTemplate.postForObject(SERVICE_PATH1 + "/news/save", entity, String.class);
    }

    @GetMapping("/countByDate")
    @Operation(description = "统计指定日期的新闻数量")
    public String countByDate(@RequestParam String date) {
        return restTemplate.getForObject(SERVICE_PATH1 + "/news/countByDate?date=" + date, String.class);
    }

    @DeleteMapping("/deleteByDate")
    @Operation(description = "删除指定日期的所有新闻（超级管理员）")
    public String deleteByDate(@RequestParam String date) {
        // 使用 exchange 获取 DELETE 请求的返回值
        org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
                SERVICE_PATH1 + "/news/deleteByDate?date=" + date,
                org.springframework.http.HttpMethod.DELETE,
                null,
                String.class);
        return response.getBody();
    }

    @GetMapping("/getByDate")
    @Operation(description = "按日期分页查询新闻（管理后台）")
    public String getByDate(@RequestParam int pageno, @RequestParam int pagesize,
            @RequestParam String date,
            @RequestParam(required = false) String sortProp,
            @RequestParam(required = false) String sortOrder) {
        String url = SERVICE_PATH1 + "/news/getByDate?pageno=" + pageno + "&pagesize=" + pagesize + "&date=" + date;
        if (sortProp != null)
            url += "&sortProp=" + sortProp;
        if (sortOrder != null)
            url += "&sortOrder=" + sortOrder;
        return restTemplate.getForObject(url, String.class);
    }
}