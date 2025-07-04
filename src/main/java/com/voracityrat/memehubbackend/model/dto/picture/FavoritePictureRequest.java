package com.voracityrat.memehubbackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户收藏图片接收参数
 */
@Data
public class FavoritePictureRequest implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 图片 id
     */
    private Long picId;

    private static final long serialVersionUID = 1L;
}