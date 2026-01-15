package com.newsmanager.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Schema(description = "分页模板实体类")
public class PagerTemplate implements Serializable {

    // 当前页码
    private long pageno;

    // 每页显示记录数
    private long pagesize;

    // 总记录数
    private long total;

    // 总页数
    private long totalpage;

    // 分页查询当前页的结果集
    private List<?> list;
}