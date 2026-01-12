package com.newsmanager.api.models;

import com.baomidou.mybatisplus.annotation.IdType;
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
@Schema(description = "浏览历史实体类")
@TableName("tb_history")
public class HistoryModel extends Model<HistoryModel> implements Serializable {

    public HistoryModel(int hid) {
        this.hid = hid;
    }

    @Schema(description = "历史ID")
    @TableId(type = IdType.AUTO)
    private int hid;

    @Schema(description = "用户ID")
    @TableField
    private Integer uid;

    @Schema(description = "新闻ID")
    @TableField
    private String nid;

    @Schema(description = "浏览时间")
    @TableField
    private String createdate;
}
