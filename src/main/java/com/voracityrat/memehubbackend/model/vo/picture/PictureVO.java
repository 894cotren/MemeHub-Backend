package com.voracityrat.memehubbackend.model.vo.picture;

import cn.hutool.json.JSONUtil;
import com.voracityrat.memehubbackend.model.entity.Picture;
import com.voracityrat.memehubbackend.model.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 图片VO类   （有详细图片参数的VO）
 */
@Data
public class PictureVO implements Serializable {
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
     * 图片体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片宽高比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;


    /**
     * 图片状态 0-待审核 1-审核通过 2-审核驳回 3-违规下架
     */
    private Integer reviewStatus;

    /**
     * 审核备注
     */
    private String reviewMessage;

    /**
     * 审核人 ID
     */
    private Long reviewerId;

    /**
     * 审核时间
     */
    private Date reviewTime;

    /**
     * 上传的用户
     */
    private User uploadUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    /**
     * 将图片实体类转换为vo类
     *
     * @param picture 实体类
     * @return
     */
    public static PictureVO objToVo(Picture picture) {
        PictureVO pictureVO = new PictureVO();
        BeanUtils.copyProperties(picture, pictureVO);
        //转换json标签为数组
        pictureVO.setTags(JSONUtil.toList(picture.getTags(), String.class));
        return pictureVO;
    }

    /**
     * 将vo类转换为实体类
     *
     * @param pictureVO
     * @return
     */
    public static Picture voToObj(PictureVO pictureVO) {
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureVO, picture);
        //需要将vo中的数组转换为json
        picture.setTags(JSONUtil.toJsonStr(pictureVO.getTags()));
        return picture;
    }
}
