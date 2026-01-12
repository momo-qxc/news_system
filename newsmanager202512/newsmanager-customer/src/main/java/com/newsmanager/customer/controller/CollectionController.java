package com.newsmanager.customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static com.newsmanager.customer.common.GlobalConfig.SERVICE_PATH1;

@RestController
@RequestMapping("/news/collection")
public class CollectionController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/get")
    public String get(String phone) {
        return restTemplate.getForObject(
                SERVICE_PATH1 + "/news/collection/get?phone=" + phone, String.class);
    }

    @GetMapping("/getdetail")
    @Operation(description = "查询收藏详情（包含新闻标题和收藏时间）")
    public String getDetail(String phone) {
        return restTemplate.getForObject(
                SERVICE_PATH1 + "/news/collection/getdetail?phone=" + phone, String.class);
    }

    @GetMapping("/getbyphone")
    @Operation(description = "根据手机号分页查询收藏的新闻")
    public String getByPhone(@RequestParam String phone, @RequestParam int pageno, @RequestParam int pagesize) {
        return restTemplate.getForObject(SERVICE_PATH1 + "/news/collection/getbyphone?phone=" + phone + "&pageno="
                + pageno + "&pagesize=" + pagesize, String.class);
    }

    @GetMapping("/getbyphoneandtid")
    @Operation(description = "根据手机号和分类ID分页查询收藏的新闻")
    public String getByPhoneAndTid(@RequestParam String phone, @RequestParam int tid, @RequestParam int pageno,
            @RequestParam int pagesize) {
        return restTemplate.getForObject(SERVICE_PATH1 + "/news/collection/getbyphoneandtid?phone=" + phone + "&tid="
                + tid + "&pageno=" + pageno + "&pagesize=" + pagesize, String.class);
    }

    @PostMapping("/save")
    @Operation(description = "新增收藏")
    public void save(@RequestParam String phone, @RequestParam String nid,
            @RequestParam(required = false) String createdate) {
        org.springframework.util.MultiValueMap<String, String> params = new org.springframework.util.LinkedMultiValueMap<>();
        params.add("phone", phone);
        params.add("nid", nid);
        params.add("createdate", createdate != null ? createdate
                : new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        // colid 不传，让数据库自动递增

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
        org.springframework.http.HttpEntity<org.springframework.util.MultiValueMap<String, String>> request = new org.springframework.http.HttpEntity<>(
                params, headers);

        restTemplate.postForObject(SERVICE_PATH1 + "/news/collection/save", request, String.class);
    }

    @DeleteMapping("/del/{colid}")
    @Operation(description = "删除收藏")
    public void del(@PathVariable Integer colid) {
        restTemplate.delete(SERVICE_PATH1 + "/news/collection/del/" + colid);
    }
}
