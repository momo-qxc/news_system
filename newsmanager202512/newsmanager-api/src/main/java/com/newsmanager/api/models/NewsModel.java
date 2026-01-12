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
@Schema(description = "新闻文章实体类")
@TableName("tb_news")
public class NewsModel extends Model<NewsModel> implements Serializable {

    @Schema(description = "新闻ID")
    @TableId
    private String nid;

    @Schema(description = "新闻标题")
    @TableField
    private String ntitle;

    @Schema(description = "新闻内容")
    @TableField
    private String content;

    public NewsModel(String nid) {
        this.nid = nid;
    }

    @Schema(description = "发布时间")
    @TableField
    private String createdate;

    @Schema(description = "作者")
    @TableField
    private String author;

    @Schema(description = "点击量")
    @TableField
    private Integer cnt;

    @Schema(description = "状态(0:草稿, 1:发布)")
    @TableField
    private Integer status;

    @Schema(description = "所属主题ID")
    @TableField
    private Integer tid;
}
