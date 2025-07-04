package com.voracityrat.memehubbackend.model.dto.picture;

import com.voracityrat.memehubbackend.model.dto.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页查询用户已收藏图片接受参数类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FavoritePicturePagesRequest extends PageRequest {

    /**
     * 用户id
     */
    private Long userId;
}
