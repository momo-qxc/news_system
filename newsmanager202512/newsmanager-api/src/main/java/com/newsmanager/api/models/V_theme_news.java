package com.newsmanager.api.models;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@TableName("v_theme_news")
public class V_theme_news extends Model<V_theme_news> {

    @TableField
    private String tname;

    @TableField
    private int cnt;
}