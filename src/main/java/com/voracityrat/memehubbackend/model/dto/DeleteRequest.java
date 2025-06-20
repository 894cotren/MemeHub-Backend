package com.voracityrat.memehubbackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用于接受删除对象id的request
 *
 * @author grey
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private long id;


}
