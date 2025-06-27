package com.voracityrat.memehubbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.voracityrat.memehubbackend.model.dto.picture.PicturePagesRequest;
import com.voracityrat.memehubbackend.model.dto.picture.PictureUpdateRequest;
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

    /**
     * 管理员更新图片信息
     * @param pictureUpdateRequest
     * @return
     */
    boolean updatePicture(PictureUpdateRequest pictureUpdateRequest);


    /**
     * 管理员根据图片id获取未脱敏图片信息
     * @param picId
     * @return
     */
    Picture getPictureByIdForAdmin(Long picId);


    /**
     * 管理员可获得的未脱敏，不需要限制条数的图片分页查询
     * @param picturePagesRequest
     * @return
     */
    Page<Picture> getPicturePages(PicturePagesRequest picturePagesRequest);
}
