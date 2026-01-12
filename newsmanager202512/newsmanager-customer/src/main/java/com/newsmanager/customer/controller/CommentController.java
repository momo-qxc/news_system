package com.newsmanager.customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static com.newsmanager.customer.common.GlobalConfig.SERVICE_PATH1;

@RestController
@RequestMapping("/news/comment")
public class CommentController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/getbynid")
    @Operation(description = "根据新闻ID查询评论（包含用户名）")
    public String getByNid(@RequestParam String nid, @RequestParam(required = false) String phone) {
        String url = SERVICE_PATH1 + "/comment/getbynidwithuser?nid=" + nid;

        // 如果有登录用户，根据 phone 获取 uid
        if (phone != null && !phone.isEmpty()) {
            String userJson = restTemplate.getForObject(SERVICE_PATH1 + "/user/getbyphone?phone=" + phone,
                    String.class);
            if (userJson != null && !userJson.equals("null")) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(userJson);
                    if (node.has("uid")) {
                        Integer uid = node.get("uid").asInt();
                        url += "&currentUid=" + uid;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return restTemplate.getForObject(url, String.class);
    }

    @PostMapping("/save")
    @Operation(description = "新增评论")
    public void save(@RequestParam String phone, @RequestParam String nid,
            @RequestParam String content, @RequestParam(required = false) String createdate,
            @RequestParam(required = false) boolean anonymous) {

        // 根据 phone 查询 uid
        String userJson = restTemplate.getForObject(SERVICE_PATH1 + "/user/getbyphone?phone=" + phone, String.class);
        Integer uid = null;

        if (userJson != null && !userJson.equals("null")) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(userJson);
                if (node.has("uid")) {
                    uid = node.get("uid").asInt();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (anonymous) {
            params.add("uid", "0"); // 0 表示匿名
        } else {
            params.add("uid", uid != null ? String.valueOf(uid) : "0");
        }
        params.add("nid", nid);
        params.add("content", content);
        params.add("createdate", createdate != null ? createdate
                : new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        restTemplate.postForObject(SERVICE_PATH1 + "/comment/save", request, String.class);
    }

    @DeleteMapping("/del/{cid}")
    @Operation(description = "删除评论")
    public void del(@PathVariable Integer cid) {
        restTemplate.delete(SERVICE_PATH1 + "/comment/del/" + cid);
    }

    @GetMapping("/get")
    @Operation(description = "获取所有评论(管理后台)")
    public String getAll() {
        return restTemplate.getForObject(SERVICE_PATH1 + "/comment/get", String.class);
    }

    @PutMapping("/check")
    @Operation(description = "审核评论")
    public void check(@RequestParam Integer cid, @RequestParam Integer status) {
        restTemplate.put(SERVICE_PATH1 + "/comment/update?cid=" + cid + "&status=" + status, null);
    }
}
