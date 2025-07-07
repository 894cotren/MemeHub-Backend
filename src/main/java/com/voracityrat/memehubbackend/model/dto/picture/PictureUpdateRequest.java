package com.voracityrat.memehubbackend.model.dto.picture;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 管理员更新接收对象
 * @author voracityrat
 */
@Data
public class PictureUpdateRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 图片 url
     */
    private String picUrl;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签（JSON 数组）
     */
    private List<String> tags;


    /**
     * 图片状态 0-待审核 1-审核通过 2-审核驳回 3-违规下架
     */
    private Integer reviewStatus;

    /**
     * 审核备注
     */
    private String reviewMessage;

}
