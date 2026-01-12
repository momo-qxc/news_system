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
@Schema(description = "管理员实体类")
@TableName("tb_admin")
public class AdminModel extends Model<AdminModel> implements Serializable {

    @TableId
    @Schema(description = "主键")
    private int aid;

    @TableField("adminname")
    @Schema(description = "用户名")
    private String adminame;

    @TableField
    @Schema(description = "密码")
    private String pwd;

    @TableField("isadmin")
    @Schema(description = "管理员类型 (0:普通管理员, 1:超级管理员)")
    private Integer isadmin;

    @TableField
    @Schema(description = "审核状态 (0:未审核, 1:已审核) 超级管理员默认为NULL或1")
    private Integer status;

    // 用于登录的构造函数
    public AdminModel(String adminame, String pwd, Integer isadmin) {
        this.adminame = adminame;
        this.pwd = pwd;
        this.isadmin = isadmin;
    }

}
