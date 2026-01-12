package com.newsmanager.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.newsmanager.api.models.AdminModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IAdmin extends BaseMapper<AdminModel> {
}
