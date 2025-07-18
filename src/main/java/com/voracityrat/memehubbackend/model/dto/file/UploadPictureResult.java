package com.voracityrat.memehubbackend.model.dto.file;

import lombok.Data;

/**
 * 对接上传cos返回图片信息值的专门的类。
 */
@Data
public class UploadPictureResult {

    /**
     * 源图片URL
     */
    private String originUrl;

    /**
     * 图片地址
     */
    private String url;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 文件体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private int picWidth;

    /**
     * 图片高度
     */
    private int picHeight;

    /**
     * 图片宽高比
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

}
