package com.newsmanager.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.newsmanager.api.models.UserModel;
import com.newsmanager.core.mapper.IUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private IUser dao;

    public List<UserModel> get() {
        return new UserModel().selectAll();
    }

    public void save(UserModel um) {
        um.insert();
    }

    public boolean checkPhoneExists(String phone) {
        QueryWrapper<UserModel> qw = new QueryWrapper<>();
        qw.eq("phone", phone);
        return new UserModel().selectCount(qw) > 0;
    }

    public void del(UserModel userModel) {
        QueryWrapper<UserModel> qw = new QueryWrapper<>();
        qw.eq("uid", userModel.getUid());
        userModel.delete(qw);
    }

    public void modify(UserModel userModel) {
        UpdateWrapper<UserModel> uw = new UpdateWrapper<>();
        uw.eq("uid", userModel.getUid());
        userModel.update(uw);
    }

    public UserModel getone(int uid) {
        return new UserModel().selectById(uid);
    }
}
