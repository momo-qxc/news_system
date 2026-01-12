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
@Schema(description = "用户信息实体类")
@TableName("tb_user")
public class UserModel extends Model<UserModel> implements Serializable {

    @Schema(description = "用户ID")
    @TableId(type = IdType.AUTO)
    private Integer uid;

    @Schema(description = "用户名")
    @TableField
    private String username;

    @Schema(description = "手机号")
    @TableField
    private String phone;

    @Schema(description = "性别(M:男, F:女)")
    @TableField
    private String sex;

    @Schema(description = "年龄")
    @TableField
    private Integer age;

    @Schema(description = "头像路径")
    @TableField
    private String pic;
}
