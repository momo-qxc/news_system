package com.newsmanager.core.controller;

import com.newsmanager.api.models.AdminModel;
import com.newsmanager.core.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员控制器
 * 提供管理员的增删改查、登录、注册等功能
 */
@Tag(name = "管理员相关接口")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 查询所有管理员
     * 
     * @return 管理员列表
     */
    @GetMapping("/get")
    @Operation(summary = "查询所有管理员")
    public List<AdminModel> get() {
        return adminService.get();
    }

    /**
     * 根据ID查询单个管理员
     * 
     * @param aid 管理员ID
     * @return 管理员信息
     */
    @GetMapping("/getone")
    @Operation(summary = "查询单个管理员")
    // @Parameter(description = "管理员ID")
    public AdminModel getone(@RequestParam int aid) {
        return adminService.getone(aid);
    }

    /**
     * 删除管理员
     * 
     * @param aid 管理员ID
     */
    @DeleteMapping("/del/{aid}")
    @Operation(summary = "删除管理员")
    public void del(@PathVariable int aid) {
        AdminModel adminModel = new AdminModel();
        adminModel.setAid(aid);
        adminService.del(adminModel);
    }

    /**
     * 修改管理员完整信息（全量更新）
     * 
     * @param adminModel 管理员实体类，包含所有要更新的字段
     */
    @PutMapping("/update")
    @Operation(summary = "修改管理员信息")
    public void modify(@ModelAttribute AdminModel adminModel) {
        adminService.modify(adminModel);
    }

    /**
     * 修改管理员状态
     * 
     * @param aid    管理员ID
     * @param status 管理员状态 (0:普通管理员, 1:超级管理员)
     */
    @PutMapping("/modifystatus")
    @Operation(summary = "修改管理员状态")
    public void modifystatus(@RequestParam int aid, @RequestParam int status) {
        adminService.modifystatus(aid, status);
    }

    /**
     * 修改管理员密码
     * 
     * @param aid 管理员ID
     * @param pwd 新密码（明文，后端会自动MD5加密）
     */
    @PutMapping("/modifypwd")
    @Operation(summary = "修改管理员密码")
    public void modifypwd(@RequestParam int aid, @RequestParam String pwd) {
        adminService.modifypassword(aid, pwd);
    }

    /**
     * 管理员登录
     * 
     * @param adminame 管理员用户名
     * @param pwd      密码（明文，后端会自动MD5加密后比对）
     * @param isadmin  管理员类型 (0:普通管理员, 1:超级管理员)
     * @return 0=登录失败, 1=登录成功, 2=账号待审核
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public int login(@RequestParam String adminame, @RequestParam String pwd, @RequestParam int isadmin) {
        int result = adminService.login(new AdminModel(adminame, pwd, isadmin));
        if (result == 0) {
            return 0; // 登录失败
        }
        // 检查普通管理员的审核状态
        if (isadmin == 0) {
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AdminModel> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            qw.eq("adminname", adminame);
            AdminModel admin = new AdminModel().selectOne(qw);
            if (admin != null && admin.getStatus() != null && admin.getStatus() == 0) {
                return 2; // 账号待审核
            }
        }
        return 1; // 登录成功
    }

    /**
     * 注册新管理员
     * 
     * @param adminame 用户名
     * @param pwd      密码（明文，后端会自动MD5加密后存储）
     * @param isadmin  管理员类型 (0:普通管理员, 1:超级管理员)
     * @return true=注册成功, false=用户名已存在
     */
    @PostMapping("/regist")
    @Operation(summary = "注册管理员")
    public boolean regist(@RequestParam String adminame, @RequestParam String pwd, @RequestParam int isadmin) {
        // 检查用户名是否已存在
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AdminModel> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("adminname", adminame);
        AdminModel existing = new AdminModel().selectOne(qw);
        if (existing != null) {
            return false; // 用户名已存在
        }

        AdminModel adminModel = new AdminModel();
        adminModel.setAdminame(adminame);
        adminModel.setPwd(pwd);
        adminModel.setIsadmin(isadmin);
        // 超级管理员默认已审核，普通管理员默认未审核
        adminModel.setStatus(isadmin == 1 ? 1 : 0);
        adminService.regist(adminModel);
        return true;
    }

}
