package com.voracityrat.memehubbackend.model.vo.picture;

import cn.hutool.json.JSONUtil;
import com.voracityrat.memehubbackend.model.entity.Picture;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 图片VO类                （没有图片规格信息的VO）
 * @author voracityrat
 */
@Data
public class PicturePagesVO implements Serializable {

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
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建用户昵称
     */
    private String userName;

    /**
     * 当前用户是否收藏
     */
    private Boolean isFavorite=false;



    private static final long serialVersionUID = 1L;

    /**
     * 将图片实体类转换为PicturePagesVO类
     *
     * @param picture 实体类
     * @return
     */
    public static PicturePagesVO objToPagesVo(Picture picture) {
        PicturePagesVO picturePagesVO = new PicturePagesVO();
        BeanUtils.copyProperties(picture, picturePagesVO);
        //转换json标签为数组
        picturePagesVO.setTags(JSONUtil.toList(picture.getTags(), String.class));
        return picturePagesVO;
    }

}
