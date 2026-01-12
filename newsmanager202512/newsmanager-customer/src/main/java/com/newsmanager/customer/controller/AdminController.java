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
@RequestMapping("/news/admin")
public class AdminController {
    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/login")
    @Operation(description = "管理员登录,返回：0=失败,1=成功,2=待审核")
    public Integer login(@RequestParam String adminame, @RequestParam String pwd, @RequestParam int isadmin) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("adminame", adminame);
        params.add("pwd", pwd);
        params.add("isadmin", String.valueOf(isadmin));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        return restTemplate.postForObject(SERVICE_PATH1 + "/admin/login", request, Integer.class);
    }

    @PostMapping("/regist")
    @Operation(description = "管理员注册")
    public Boolean regist(@RequestParam String adminame, @RequestParam String pwd, @RequestParam int isadmin) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("adminame", adminame);
        params.add("pwd", pwd);
        params.add("isadmin", String.valueOf(isadmin));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        return restTemplate.postForObject(SERVICE_PATH1 + "/admin/regist", request, Boolean.class);
    }

    @GetMapping("/get")
    @Operation(description = "获取所有管理员列表")
    public String getAll() {
        return restTemplate.getForObject(SERVICE_PATH1 + "/admin/get", String.class);
    }

    @PutMapping("/modifystatus")
    @Operation(description = "修改管理员审核状态")
    public void modifyStatus(@RequestParam int aid, @RequestParam int status) {
        restTemplate.put(SERVICE_PATH1 + "/admin/modifystatus?aid=" + aid + "&status=" + status, null);
    }

    @DeleteMapping("/del/{aid}")
    @Operation(description = "删除管理员")
    public void del(@PathVariable int aid) {
        restTemplate.delete(SERVICE_PATH1 + "/admin/del/" + aid);
    }
}
