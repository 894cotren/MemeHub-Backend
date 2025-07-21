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
     * 创建开始时间
     */
    private Date startTime;

    /**
     * 创建结束时间
     */
    private Date endTime;


    /**
     * 是否有用户id，如果有 ，并对比当前登录用户是否一致，是的话查寻当前用户的上传图片
     */
    private Long userId;


    /**
     * 综合搜索词  可以模糊搜索名称、简介、标签
     */
    private String searchText;

}
