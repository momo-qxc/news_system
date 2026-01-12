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
@Schema(description = "公告实体类")
@TableName("tb_notice")
public class NoticeModel extends Model<NoticeModel> implements Serializable {

    @Schema(description = "公告ID")
    @TableId(type = IdType.INPUT)
    private String noid;

    @Schema(description = "公告内容")
    @TableField
    private String content;

    @Schema(description = "发布日期")
    @TableField
    private String createdate;

    @Schema(description = "展示对象: 0=用户端, 1=客户端管理员, 2=所有人")
    @TableField
    private Integer target;

    @Schema(description = "是否展示: 0=隐藏, 1=展示")
    @TableField
    private Integer visible;

    @Schema(description = "优先级: 1=高, 2=中, 3=低")
    @TableField
    private Integer priority;
}
