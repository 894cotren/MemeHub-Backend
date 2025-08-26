package com.voracityrat.memehubbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.voracityrat.memehubbackend.model.dto.picture.*;
import com.voracityrat.memehubbackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.vo.picture.PicturePagesVO;
import com.voracityrat.memehubbackend.model.vo.picture.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
     *
     * @param pictureUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updatePicture(PictureUpdateRequest pictureUpdateRequest, User loginUser);


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

    /**
     * 用户分页查询，获得脱敏后的数据
     *
     * @param pictureVOPagesRequest
     * @param loginUserId
     * @return
     */
    Page<PicturePagesVO> getPictureVOPages(PictureVOPagesRequest pictureVOPagesRequest, Long loginUserId);

    /**
     * 将图片数据脱敏为用户能看的PicturePagesVO   这里是list 脱敏
     * @param pictureList
     * @return
     */
    List<PicturePagesVO> getPicturePagesVOList(List<Picture> pictureList,Long loginUserId);


    /**
     * 图片审核
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 用户头像上传
     * @param multipartFile
     * @param loginUser
     * @return
     */
    String uploadUserAvatar(MultipartFile multipartFile, User loginUser);

    /**
     * 根据图片和登录用户，判断当前用户是否有权限修改、删除该图片。
     * 如果是公共图库只有管理员或者创建者才可以
     * 如果是空间图库，只有创建者才可以
     * @param loginUser
     * @param picture
     */
    void checkPictureAuth(User loginUser,Picture picture);


}
