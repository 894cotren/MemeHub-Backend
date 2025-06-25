package com.voracityrat.memehubbackend.service;

import com.voracityrat.memehubbackend.model.dto.picture.PictureUploadRequest;
import com.voracityrat.memehubbackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

/**
* @author voracityrat
* @description 针对表【picture(图片表)】的数据库操作Service
* @createDate 2025-06-25 14:41:40
*/
public interface PictureService extends IService<Picture> {

    /**
     *
     * @param pictureUploadRequest
     * @param multipartFile
     * @param
     * @return
     */
    PictureVO uploadPicture (PictureUploadRequest pictureUploadRequest, MultipartFile multipartFile, User loginUser);
}
