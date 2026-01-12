package com.newsmanager.api.models;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Schema(description = "点赞实体类")
@TableName("tb_like")
public class LikeModel extends Model<LikeModel> implements Serializable {

    @Schema(description = "点赞ID")
    @TableId(type = IdType.AUTO)
    private Integer lid;

    @Schema(description = "用户手机号")
    @TableField
    private String phone;

    @Schema(description = "新闻ID")
    @TableField
    private String nid;

    @Schema(description = "创建时间")
    @TableField
    private String createdate;

    public LikeModel(Integer lid) {
        this.lid = lid;
    }
}
