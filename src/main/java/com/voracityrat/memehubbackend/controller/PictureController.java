package com.voracityrat.memehubbackend.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.voracityrat.memehubbackend.annotaion.AuthCheck;
import com.voracityrat.memehubbackend.common.BaseResponse;
import com.voracityrat.memehubbackend.common.ResultUtil;
import com.voracityrat.memehubbackend.constant.UserConstant;
import com.voracityrat.memehubbackend.exception.BusinessException;
import com.voracityrat.memehubbackend.exception.ErrorCode;
import com.voracityrat.memehubbackend.exception.ThrowUtil;
import com.voracityrat.memehubbackend.model.dto.DeleteRequest;
import com.voracityrat.memehubbackend.model.dto.picture.*;
import com.voracityrat.memehubbackend.model.entity.Picture;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.vo.picture.PicturePagesVO;
import com.voracityrat.memehubbackend.model.vo.picture.PictureTagCategoryVO;
import com.voracityrat.memehubbackend.model.vo.picture.PictureVO;
import com.voracityrat.memehubbackend.service.PictureService;
import com.voracityrat.memehubbackend.service.UserPictureService;
import com.voracityrat.memehubbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * 根据图片id获取到PictureVO  脱敏后图片信息
     * @param id
     * @return
     */
    @GetMapping("/getPictureVOById")
    public BaseResponse<PictureVO> getPictureVOById(@RequestParam Long id){
        if (id ==null || id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Picture picture = pictureService.getPictureByIdForAdmin(id);
        PictureVO pictureVO = new PictureVO();
        BeanUtils.copyProperties(picture,pictureVO);
        if (!StrUtil.isBlank(picture.getTags())){
            pictureVO.setTags(JSONUtil.toList(picture.getTags(),String.class));
        }
        return ResultUtil.success(pictureVO);
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
    public BaseResponse<Page<PicturePagesVO>> getPicturePagesVO(@RequestBody PictureVOPagesRequest pictureVOPagesRequest,
                                                                HttpServletRequest request) {
        ThrowUtil.throwIf(pictureVOPagesRequest == null, ErrorCode.PARAMS_ERROR);
        int pageSize = pictureVOPagesRequest.getPageSize();
        ThrowUtil.throwIf(pageSize > 20, ErrorCode.OPERATION_ERROR, "用户不允许查询每页20条以上");
        Long loginUserId = userService.getLoginUser(request).getId();
        Page<PicturePagesVO> pages = pictureService.getPictureVOPages(pictureVOPagesRequest,loginUserId);
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


    /**
     * 获取临时的前端创建/修改时图片的属性下拉框，前期暂时手动定义
     * @return
     */
    @GetMapping("/getTagCategoryList")
    public BaseResponse<PictureTagCategoryVO> getTagCategoryList(){
        //前期暂时自定义标签和分类
        List<String> tagList= Arrays.asList("日常","二次元","生活");
        List<String> categoryList=Arrays.asList("地狱","日常","宠物","哲学","抽象","治愈","蓝调");
        PictureTagCategoryVO pictureTagCategoryVO = new PictureTagCategoryVO();
        pictureTagCategoryVO.setTagList(tagList);
        pictureTagCategoryVO.setCategoryList(categoryList);
        return ResultUtil.success(pictureTagCategoryVO);
    }


    /**
     * 图片根据id删除
     * @param deleteRequest
     * @return
     */
    @PostMapping("/deletePictureById")
    public BaseResponse<Boolean> deletePictureById(@RequestBody DeleteRequest deleteRequest){
        if (deleteRequest ==null || deleteRequest.getId() <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean ret = pictureService.removeById(deleteRequest.getId());
        if (!ret){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"删除图片失败");
        }
        return ResultUtil.success(true);
    }

}
