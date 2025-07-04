package com.voracityrat.memehubbackend.model.dto.picture;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.voracityrat.memehubbackend.model.dto.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 管理员查询接收查询参数类
 * @author voracityrat
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PicturePagesRequest extends PageRequest {

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

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建开始时间
     */
    private Date startTime;

    /**
     * 创建结束时间
     */
    private Date endTime;

    /**
     * 综合搜索词  可以模糊搜索名称、简介、标签
     */
    private String searchText;

}
