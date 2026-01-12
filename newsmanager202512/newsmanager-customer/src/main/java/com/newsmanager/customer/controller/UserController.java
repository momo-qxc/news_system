package com.newsmanager.customer.controller;

import com.newsmanager.api.models.UserLoginModel;
import com.newsmanager.api.models.UserModel;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.newsmanager.customer.common.GlobalConfig.SERVICE_PATH1;

@RestController
@RequestMapping("/news/users")
public class UserController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/getcode")
    public String getcode(String phone) {
        return restTemplate.getForObject(SERVICE_PATH1 + "/user/getcode?phone=" + phone, String.class);
    }

    @PostMapping("/login")
    public String loginvalidate(@RequestParam String phone, @RequestParam String code) {
        UserLoginModel userModel = new UserLoginModel(phone, code);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserLoginModel> request = new HttpEntity<>(userModel, headers);
        return restTemplate.postForObject(SERVICE_PATH1 + "/user/login", request, String.class);
    }

    @PostMapping("/add")
    public String save(@ModelAttribute UserModel userModel) {
        return restTemplate.postForObject(SERVICE_PATH1 + "/user/add", userModel, String.class);
    }

    @GetMapping("/getbyphone")
    @Operation(description = "根据手机号查询用户")
    public String getbyphone(@RequestParam String phone) {
        return restTemplate.getForObject(SERVICE_PATH1 + "/user/getbyphone?phone=" + phone, String.class);
    }

    @GetMapping("/get")
    @Operation(description = "获取所有用户列表")
    public String getAll() {
        return restTemplate.getForObject(SERVICE_PATH1 + "/user/get", String.class);
    }

    @DeleteMapping("/del/{uid}")
    @Operation(description = "删除用户")
    public void del(@PathVariable int uid) {
        restTemplate.delete(SERVICE_PATH1 + "/user/del/" + uid);
    }
}
