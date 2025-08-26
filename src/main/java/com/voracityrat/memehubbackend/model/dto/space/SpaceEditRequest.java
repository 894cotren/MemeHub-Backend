package com.voracityrat.memehubbackend.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * @author grey
 *  用户编辑空间信息
 */
@Data
public class SpaceEditRequest implements Serializable {

    /**
     * 空间 id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    private static final long serialVersionUID = 1L;
}
