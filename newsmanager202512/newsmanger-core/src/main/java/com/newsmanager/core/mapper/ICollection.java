package com.newsmanager.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newsmanager.api.models.CollectionModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ICollection extends BaseMapper<CollectionModel> {
}
