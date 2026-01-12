package com.newsmanager.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newsmanager.api.models.UserModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUser extends BaseMapper<UserModel> {
}
