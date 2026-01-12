package com.newsmanager.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newsmanager.api.models.UserLoginModel;
import com.newsmanager.api.models.UserModel;
import com.newsmanager.core.service.RedisService;
import com.newsmanager.core.service.UserService;
import com.newsmanager.core.util.NumberUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户相关接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;

    @GetMapping("/get")
    @Operation(summary = "查询所有用户")
    public List<UserModel> get() {
        return userService.get();
    }

    @GetMapping("/getone")
    @Operation(summary = "查询单个用户")
    // @Parameter(description = "用户ID")
    public UserModel getone(@RequestParam int uid) {
        return userService.getone(uid);
    }

    @GetMapping("/getbyphone")
    @Operation(summary = "根据手机号查询用户")
    public UserModel getByPhone(@RequestParam String phone) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserModel> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("phone", phone);
        return new UserModel().selectOne(qw);
    }

    @PostMapping("/save")
    @Operation(summary = "新增用户")
    public void save(@ModelAttribute UserModel userModel) {
        userService.save(userModel);
    }

    @DeleteMapping("/del/{uid}")
    @Operation(summary = "删除用户")
    public void del(@PathVariable int uid) {
        UserModel user = new UserModel();
        user.setUid(uid);
        userService.del(user);
    }

    @PutMapping("/update")
    @Operation(summary = "修改用户信息")
    public void modify(UserModel userModel) {
        userService.modify(userModel);
    }

    @GetMapping("/getcode")
    @Operation(summary = "获取手机验证码")
    public void getcode(String phone) {
        System.out.println("手机号码:" + phone);
        // 1.生成四位随机数
        String val = NumberUtil.getrandom();
        // 2.存储至redis数据库
        redisService.set(phone, val);
        // System.out.println("*******************"+redisService.get(phone));
        // 3.推送手机短信
        System.out.println("[短信服务]：验证码为：" + val + "，如非本人请忽略！");
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public boolean loginvalidate(@RequestBody UserLoginModel userModel) {
        boolean flag = false;
        QueryWrapper<UserModel> qw = new QueryWrapper<>();
        qw.eq("phone", userModel.getPhone());
        int val = new UserModel().selectList(qw).size();
        if (val > 0) {
            Object validatecode = redisService.get(userModel.getPhone()).toString();
            if (validatecode != null) {
                if (validatecode.equals(userModel.getCode()))
                    flag = true;
            }
        }
        return flag;
    }

    @PostMapping("/add")
    public String add(@RequestBody UserModel userModel) {
        // 检查手机号是否已存在
        if (userService.checkPhoneExists(userModel.getPhone())) {
            return "exists";
        }
        userService.save(userModel);
        return "true";
    }

}
