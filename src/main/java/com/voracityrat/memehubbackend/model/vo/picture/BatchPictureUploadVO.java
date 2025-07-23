package com.voracityrat.memehubbackend.model.vo.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BatchPictureUploadVO implements Serializable {

    /**
     * 上传失败的图片信息列表
     */
    private List<FailedUploadInfo> failedList;

    /**
     * 总上传数量
     */
    private Integer totalCount;

    /**
     * 失败数量
     */
    private Integer failedCount;

    @Data
    public static class FailedUploadInfo implements Serializable {
        /**
         * 文件名
         */
        private String fileName;

        /**
         * 失败原因
         */
        private String reason;

        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
} 