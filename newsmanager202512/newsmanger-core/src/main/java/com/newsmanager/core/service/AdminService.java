package com.newsmanager.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.newsmanager.api.models.AdminModel;
import com.newsmanager.core.mapper.IAdmin;
import com.newsmanager.core.util.MD5Parse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private IAdmin dao;

    /**
     * 查询所有管理员
     * 
     * @return 管理员列表
     */
    public List<AdminModel> get() {
        return new AdminModel().selectAll();
    }

    /**
     * 删除管理员
     * 
     * @param adminModel 包含要删除的管理员ID的对象
     */
    public void del(AdminModel adminModel) {
        QueryWrapper<AdminModel> qw = new QueryWrapper<>();
        qw.eq("aid", adminModel.getAid());
        adminModel.delete(qw);
    }

    /**
     * 根据ID查询单个管理员
     * 
     * @param aid 管理员ID
     * @return 管理员信息
     */
    public AdminModel getone(int aid) {
        return new AdminModel().selectById(aid);
    }

    /**
     * 管理员登录验证（MD5加密校验）
     * 
     * @param adminModel 管理员登录模型，需包含：管理员用户名（adminame）、密码（pwd）、管理员状态（status）
     * @return 登录成功返回1（存在匹配记录），失败返回0（无匹配记录）
     */
    public int login(AdminModel adminModel) {
        adminModel.setPwd(MD5Parse.encryptMD5(adminModel.getPwd()));
        QueryWrapper<AdminModel> qw = new QueryWrapper<>();
        qw.eq("adminname", adminModel.getAdminame());
        qw.eq("pwd", adminModel.getPwd());
        qw.eq("isadmin", adminModel.getIsadmin());
        return adminModel.selectList(qw).size();
    }

    /**
     * 注册新管理员
     * 
     * @param adminModel 管理员实体类，密码会自动MD5加密后存储
     */
    public void regist(AdminModel adminModel) {
        adminModel.setPwd(MD5Parse.encryptMD5(adminModel.getPwd()));
        adminModel.insert();
    }

    /**
     * 修改管理员完整信息（全量更新）
     * 
     * @param adminModel 管理员实体类，包含所有要更新的字段
     */
    public void modify(AdminModel adminModel) {
        UpdateWrapper<AdminModel> uw = new UpdateWrapper<>();
        uw.eq("aid", adminModel.getAid());
        adminModel.update(uw);
    }

    /**
     * 修改管理员状态
     * 
     * @param aid    管理员ID
     * @param status 管理员状态 (0:普通管理员, 1:超级管理员)
     */
    public void modifystatus(int aid, int status) {
        UpdateWrapper<AdminModel> uw = new UpdateWrapper<>();
        uw.eq("aid", aid);
        uw.set("status", status);
        new AdminModel().update(uw);
    }

    /**
     * 修改管理员密码（MD5加密）
     * 
     * @param aid 管理员ID
     * @param pwd 新密码（明文，会自动MD5加密）
     */
    public void modifypassword(int aid, String pwd) {
        UpdateWrapper<AdminModel> uw = new UpdateWrapper<>();
        uw.eq("aid", aid);
        uw.set("pwd", MD5Parse.encryptMD5(pwd));
        new AdminModel().update(uw);
    }
}
