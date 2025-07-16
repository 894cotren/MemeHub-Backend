package com.voracityrat.memehubbackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

@Data
public class PictureReviewRequest implements Serializable {

    /**
     * 图片id
     */
    private Long id;

    /**
     * 图片状态 0-待审核 1-审核通过 2-审核驳回 3-违规下架
     */
    private Integer reviewStatus;

    /**
     * 审核备注
     */
    private String reviewMessage;

}
