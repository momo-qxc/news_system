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
@Schema(description = "新闻主题实体类")
@TableName("tb_theme")
@RequiredArgsConstructor
public class ThemeModel extends Model<ThemeModel> implements Serializable {
    @Schema(description = "主题ID")
    @TableId
    @NonNull
    private int tid;

    @Schema(description = "主题名称")
    @TableField
    private String tname;
}
