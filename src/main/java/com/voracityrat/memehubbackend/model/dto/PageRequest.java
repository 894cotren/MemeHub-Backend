package com.voracityrat.memehubbackend.model.dto;

import lombok.Data;


@Data
public class PageRequest {

    /**
     * 分页查询当前页
     */
    private int pageNum;

    /**
     * 分页查询每页大小
     */
    private int pageSize;

    /**
     * 分页查询排序字段
     */
    private String sortField;

    /**
     * 分页查询排序方式 ： 升序，降序 ;这里默认降序
     */
    private String sortOrder = "descend";
}
