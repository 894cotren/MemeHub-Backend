package com.voracityrat.memehubbackend.model.dto.picture;

import com.voracityrat.memehubbackend.model.dto.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 用户查询接受参数类
 *
 * @author voracityrat
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PictureVOPagesRequest extends PageRequest {


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
     * 综合搜索词  可以模糊搜索名称、简介、标签
     */
    private String searchText;

}
