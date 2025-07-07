package com.voracityrat.memehubbackend.model.vo.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 传给前端供修改创建图片的下拉框数据
 */
@Data
public class PictureTagCategoryVO implements Serializable {

    /**
     *图片标签列表
     */
    private List<String> tagList;
    /**
     * 图片分类列表
     */
    private List<String> categoryList;


}
