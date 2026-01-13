package com.newsmanager.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newsmanager.api.models.CommentLikeModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ICommentLike extends BaseMapper<CommentLikeModel> {
}
