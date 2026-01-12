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
@Schema(description = "收藏实体类")
@TableName("tb_collection")
public class CollectionModel extends Model<CollectionModel> implements Serializable {

    @Schema(description = "收藏ID")
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Integer colid;

    @Schema(description = "用户手机号")
    @TableField
    private String phone;

    @Schema(description = "新闻ID")
    @TableField
    private String nid;

    @Schema(description = "创建时间")
    @TableField
    private String createdate;

    public CollectionModel(Integer colid) {
        this.colid = colid;
    }
}
