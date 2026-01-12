package com.newsmanager.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newsmanager.api.models.NoticeModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface INotice extends BaseMapper<NoticeModel> {
}
