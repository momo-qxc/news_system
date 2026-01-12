package com.newsmanager.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newsmanager.api.models.HistoryModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IHistory extends BaseMapper<HistoryModel> {
}
