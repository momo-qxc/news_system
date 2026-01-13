package com.newsmanager.api.models;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Schema(description = "评论点赞实体类")
@TableName("tb_comment_like")
public class CommentLikeModel extends Model<CommentLikeModel> {

    @Schema(description = "自增ID")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description = "评论ID")
    @TableField
    private Integer cid;

    @Schema(description = "用户ID")
    @TableField
    private Integer uid;

    @Schema(description = "点赞时间")
    @TableField
    private String createdate;
}
