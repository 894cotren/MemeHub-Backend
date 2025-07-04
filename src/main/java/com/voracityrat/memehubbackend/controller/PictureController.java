package com.voracityrat.memehubbackend.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.voracityrat.memehubbackend.annotaion.AuthCheck;
import com.voracityrat.memehubbackend.common.BaseResponse;
import com.voracityrat.memehubbackend.common.ResultUtil;
import com.voracityrat.memehubbackend.constant.UserConstant;
import com.voracityrat.memehubbackend.exception.BusinessException;
import com.voracityrat.memehubbackend.exception.ErrorCode;
import com.voracityrat.memehubbackend.exception.ThrowUtil;
import com.voracityrat.memehubbackend.model.dto.picture.*;
import com.voracityrat.memehubbackend.model.entity.Picture;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.vo.PicturePagesVO;
import com.voracityrat.memehubbackend.model.vo.PictureVO;
import com.voracityrat.memehubbackend.service.PictureService;
import com.voracityrat.memehubbackend.service.UserPictureService;
import com.voracityrat.memehubbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author grey
 */
@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    @Resource
    private UserPictureService userPictureService;

    @PostMapping("/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN)
    public BaseResponse<PictureVO> uploadPicture(@RequestPart("file")MultipartFile multipartFile,
                                                 PictureUploadRequest pictureUploadRequest,
                                                 HttpServletRequest request){
        //获取当前用户
        User loginUser = userService.getLoginUser(request);
        //图片上传
        PictureVO pictureVO = pictureService.uploadPicture(pictureUploadRequest, multipartFile, loginUser);
        return ResultUtil.success(pictureVO);

    }

    /**
     * 仅管理员可用更新
     * @param pictureUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest){
        ThrowUtil.throwIf(pictureUpdateRequest==null, ErrorCode.PARAMS_ERROR);
        boolean result = pictureService.updatePicture(pictureUpdateRequest);
        return ResultUtil.success(result);
    }


    /**
     * 管理员根据id查询
     * @param id
     * @return
     */
    @GetMapping("/getPictureByIdForAdmin")
    @AuthCheck(mustRole = UserConstant.ADMIN)
    public BaseResponse<Picture> getPictureByIdForAdmin(@RequestParam Long id){
        if (id ==null || id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Picture picture = pictureService.getPictureByIdForAdmin(id);
        return ResultUtil.success(picture);
    }

    /**
     * 管理员分页查询 ，返回未脱敏图片信息
     *
     * @param picturePagesRequest
     * @return
     */
    @PostMapping("/getPicturePages")
    @AuthCheck(mustRole = UserConstant.ADMIN)
    public BaseResponse<Page<Picture>> getPicturePages(@RequestBody PicturePagesRequest picturePagesRequest){
        ThrowUtil.throwIf(picturePagesRequest==null,ErrorCode.PARAMS_ERROR);
        Page<Picture> picturePages = pictureService.getPicturePages(picturePagesRequest);
        return ResultUtil.success(picturePages);
    }


    /**
     * 用户分页查询，返回高度脱敏图片信息
     *
     * @param pictureVOPagesRequest
     * @return
     */
    @PostMapping("/getPicturePagesVO")
    @AuthCheck(mustRole = UserConstant.ADMIN)
    public BaseResponse<Page<PicturePagesVO>> getPicturePagesVO(@RequestBody PictureVOPagesRequest pictureVOPagesRequest) {
        ThrowUtil.throwIf(pictureVOPagesRequest == null, ErrorCode.PARAMS_ERROR);
        int pageSize = pictureVOPagesRequest.getPageSize();
        ThrowUtil.throwIf(pageSize > 20, ErrorCode.OPERATION_ERROR, "用户不允许查询每页20条以上");
        Page<PicturePagesVO> pages = pictureService.getPictureVOPages(pictureVOPagesRequest);
        return ResultUtil.success(pages);
    }


    /**
     * 用户收藏图片
     *
     * @param favoritePictureRequest
     * @param request
     * @return
     */
    @PostMapping("/favoritePicture")
    public BaseResponse<Boolean> userFavoritePicture(FavoritePictureRequest favoritePictureRequest, HttpServletRequest request) {
        ThrowUtil.throwIf(favoritePictureRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        favoritePictureRequest.setUserId(loginUser.getId());
        boolean result = userPictureService.userFavoritePicture(favoritePictureRequest);
        return ResultUtil.success(result);
    }


    /**
     * 用户取消收藏图片
     *
     * @param favoritePictureRequest
     * @param request
     * @return
     */
    @PostMapping("/unfavoritePicture")
    public BaseResponse<Boolean> userUnfavoritePicture(FavoritePictureRequest favoritePictureRequest, HttpServletRequest request) {
        ThrowUtil.throwIf(favoritePictureRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        favoritePictureRequest.setUserId(loginUser.getId());
        boolean result = userPictureService.userUnfavoritePicture(favoritePictureRequest);
        return ResultUtil.success(result);
    }

    /**
     * 用户分页查询收藏信息
     * @param favoritePicturePagesRequest
     * @param request
     * @return
     */
    @PostMapping("/getFavoritePicturePages")
    public BaseResponse<Page<PicturePagesVO>> getFavoritePicturePages(FavoritePicturePagesRequest favoritePicturePagesRequest,
                                                                HttpServletRequest request){
        ThrowUtil.throwIf(favoritePicturePagesRequest==null,ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        favoritePicturePagesRequest.setUserId(loginUser.getId());
        Page<PicturePagesVO> favoritePicturePages = userPictureService.getFavoritePicturePages(favoritePicturePagesRequest);
        return ResultUtil.success(favoritePicturePages);
    }

}
