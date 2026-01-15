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
    @Operation(description = "根据新闻ID查询评论树（包含用户名、点赞等）")
    public String getByNid(@RequestParam String nid, @RequestParam(required = false) String phone) {
        String url = SERVICE_PATH1 + "/comment/getbynidwithtree?nid=" + nid;

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
    @Operation(description = "新增评论/回复")
    public String save(@RequestParam String phone, @RequestParam String nid,
            @RequestParam String content, @RequestParam(required = false) String createdate,
            @RequestParam(required = false) boolean anonymous,
            @RequestParam(required = false, defaultValue = "0") Integer pid) {

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
        params.add("pid", String.valueOf(pid));
        params.add("createdate", createdate != null ? createdate
                : new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        return restTemplate.postForObject(SERVICE_PATH1 + "/comment/save", request, String.class);
    }

    @DeleteMapping("/del/{cid}")
    @Operation(description = "删除评论")
    public void del(@PathVariable Integer cid) {
        restTemplate.delete(SERVICE_PATH1 + "/comment/del/" + cid);
    }

    @GetMapping("/get")
    @Operation(description = "获取所有评论(管理后台，分页)")
    public String getAll(
            @RequestParam(defaultValue = "1") int pageno,
            @RequestParam(defaultValue = "10") int pagesize) {
        return restTemplate.getForObject(SERVICE_PATH1 + "/comment/get?pageno=" + pageno + "&pagesize=" + pagesize,
                String.class);
    }

    @PutMapping("/check")
    @Operation(description = "审核评论")
    public void check(@RequestParam Integer cid, @RequestParam Integer status) {
        restTemplate.put(SERVICE_PATH1 + "/comment/update?cid=" + cid + "&status=" + status, null);
    }

    @PostMapping("/like")
    @Operation(description = "点赞评论")
    public void like(@RequestParam Integer cid, @RequestParam String phone) {
        Integer uid = getUidByPhone(phone);
        if (uid != null) {
            restTemplate.postForObject(SERVICE_PATH1 + "/comment/like?cid=" + cid + "&uid=" + uid, null, String.class);
        }
    }

    @PostMapping("/unlike")
    @Operation(description = "取消点赞")
    public void unlike(@RequestParam Integer cid, @RequestParam String phone) {
        Integer uid = getUidByPhone(phone);
        if (uid != null) {
            restTemplate.postForObject(SERVICE_PATH1 + "/comment/unlike?cid=" + cid + "&uid=" + uid, null,
                    String.class);
        }
    }

    private Integer getUidByPhone(String phone) {
        if (phone == null || phone.isEmpty())
            return null;
        String userJson = restTemplate.getForObject(SERVICE_PATH1 + "/user/getbyphone?phone=" + phone, String.class);
        if (userJson != null && !userJson.equals("null")) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(userJson);
                if (node.has("uid")) {
                    return node.get("uid").asInt();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
