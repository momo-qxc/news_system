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
@RequestMapping("/news/notice")
public class NoticeController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/get")
    @Operation(description = "获取所有公告")
    public String getAll() {
        return restTemplate.getForObject(SERVICE_PATH1 + "/notice/get", String.class);
    }

    @GetMapping("/getbytarget")
    @Operation(description = "根据目标获取公告: 0=用户端, 1=管理员, 2=所有人")
    public String getByTarget(@RequestParam int target) {
        // target=0时返回 target=0 或 target=2 的公告（用户端+所有人）
        // target=1时返回 target=1 或 target=2 的公告（管理员+所有人）
        return restTemplate.getForObject(SERVICE_PATH1 + "/notice/getbytarget?target=" + target, String.class);
    }

    @PostMapping("/save")
    @Operation(description = "新增公告")
    public void save(@RequestParam String content, @RequestParam int target,
            @RequestParam(required = false, defaultValue = "1") int visible,
            @RequestParam(required = false, defaultValue = "2") int priority) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("content", content);
        params.add("target", String.valueOf(target));
        params.add("visible", String.valueOf(visible));
        params.add("priority", String.valueOf(priority));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        restTemplate.postForObject(SERVICE_PATH1 + "/notice/save", request, String.class);
    }

    @PutMapping("/update")
    @Operation(description = "修改公告")
    public void update(@RequestParam String noid, @RequestParam String content, @RequestParam int target,
            @RequestParam(required = false, defaultValue = "1") int visible,
            @RequestParam(required = false, defaultValue = "2") int priority) {
        restTemplate.put(SERVICE_PATH1 + "/notice/update?noid=" + noid + "&content=" + content + "&target=" + target +
                "&visible=" + visible + "&priority=" + priority, null);
    }

    @DeleteMapping("/del")
    @Operation(description = "删除公告")
    public void del(@RequestParam String noid) {
        restTemplate.delete(SERVICE_PATH1 + "/notice/del?noid=" + noid);
    }
}
