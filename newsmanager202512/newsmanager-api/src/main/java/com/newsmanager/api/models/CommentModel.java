package com.newsmanager.api.models;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Schema(description = "评论实体类")
@TableName("tb_comment")
public class CommentModel extends Model<CommentModel> implements Serializable {

    @Schema(description = "评论ID")
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Integer cid;

    @Schema(description = "用户ID")
    @TableField
    private Integer uid;

    @Schema(description = "评论内容")
    @TableField
    private String content;

    @Schema(description = "评论时间")
    @TableField
    private String createdate;

    @Schema(description = "新闻ID")
    @TableField
    private String nid;

    @Schema(description = "状态(0:待审核, 1:已通过)")
    @TableField
    private Integer status;

    @Schema(description = "父评论ID")
    @TableField
    private Integer pid;

    // 以下为非数据库持久化字段，用于前端展示
    @TableField(exist = false)
    private java.util.List<CommentModel> replyList;

    @TableField(exist = false)
    private Integer likeCount;

    @TableField(exist = false)
    private Boolean isLiked;

    @TableField(exist = false)
    private String username;
}
