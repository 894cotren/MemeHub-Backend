package com.voracityrat.memehubbackend.controller;


import cn.hutool.core.util.ObjUtil;
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
import com.voracityrat.memehubbackend.model.entity.Space;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.vo.picture.BatchPictureUploadVO;
import com.voracityrat.memehubbackend.model.vo.picture.PicturePagesVO;
import com.voracityrat.memehubbackend.model.vo.picture.PictureTagCategoryVO;
import com.voracityrat.memehubbackend.model.vo.picture.PictureVO;
import com.voracityrat.memehubbackend.model.vo.user.LoginUserVO;
import com.voracityrat.memehubbackend.service.PictureService;
import com.voracityrat.memehubbackend.service.SpaceService;
import com.voracityrat.memehubbackend.service.UserPictureService;
import com.voracityrat.memehubbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.support.TransactionTemplate;
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

    @Resource
    private SpaceService spaceService;


    @Resource
    private TransactionTemplate transactionTemplate;

    @PostMapping("/upload")
    public BaseResponse<PictureVO> uploadPicture(@RequestPart("file")MultipartFile multipartFile,
                                                 PictureUploadRequest pictureUploadRequest,
                                                 HttpServletRequest request){
        //获取当前用户
        User loginUser = userService.getLoginUser(request);
        //图片上传
        PictureVO pictureVO = pictureService.uploadPicture(pictureUploadRequest, multipartFile, loginUser);
        return ResultUtil.success(pictureVO);
    }

    @PostMapping("/batchUpload")
    @AuthCheck(mustRole = UserConstant.ADMIN)
    public BaseResponse<BatchPictureUploadVO> batchUploadPicture(@RequestPart("file") MultipartFile file,
                                                 HttpServletRequest request){
        //获取当前用户
        User loginUser = userService.getLoginUser(request);

        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择要上传的图片");
        }

        // 创建默认的上传请求
        PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();

        // 复用现有的单文件上传方法
        PictureVO pictureVO = pictureService.uploadPicture(pictureUploadRequest, file, loginUser);

        //注意这里是前端去处理失败结果的，我们这里单个上传错误抛异常会又全局返回的，让前端处理。

        // 组装批量上传结果
        BatchPictureUploadVO result = new BatchPictureUploadVO();
        result.setFailedList(new ArrayList<>());
        result.setTotalCount(1);
        result.setFailedCount(0);

        return ResultUtil.success(result);
    }



    /**
     * 通用的图片更新   更新图片的只能是用户自己或者是管理员，用户更新需要再次审核
     * @param pictureUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest,
                                               HttpServletRequest request){
        ThrowUtil.throwIf(pictureUpdateRequest==null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        boolean result = pictureService.updatePicture(pictureUpdateRequest,loginUser);
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
    public BaseResponse<PictureVO> getPictureVOById(@RequestParam Long id,HttpServletRequest request){
        if (id ==null || id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Picture picture = pictureService.getPictureByIdForAdmin(id);
        //空间权限校验，校验一下这个图片你是否有权限。 只校验非公共图片的，也就是spaceId为非空的。
        Long spaceId = picture.getSpaceId();
        if (spaceId!=null){
            User loginUser = userService.getLoginUser(request);
            pictureService.checkPictureAuth(loginUser,picture);  //权限不够里面会抛异常的
        }
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
     *  这里尽量整通用
     * @param pictureVOPagesRequest
     * @return
     */
    @PostMapping("/getPicturePagesVO")
    public BaseResponse<Page<PicturePagesVO>> getPicturePagesVO(@RequestBody PictureVOPagesRequest pictureVOPagesRequest,
                                                                HttpServletRequest request) {
        ThrowUtil.throwIf(pictureVOPagesRequest == null, ErrorCode.PARAMS_ERROR);
        int pageSize = pictureVOPagesRequest.getPageSize();
        ThrowUtil.throwIf(pageSize > 20, ErrorCode.OPERATION_ERROR, "用户不允许查询每页20条以上");
        //此处应该修改为用户可以未登录的，未登录传入null即可，里面已经做好为空的处理了。 所以做了如下手动处理
//        Long loginUserId = userService.getLoginUser(request).getId();  这段代码如果没有登录用户会报错，我们手动处理下吧。
        //从请求体里获取到用户对象并转换
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
        LoginUserVO loginUser = (LoginUserVO) userObj;
        Long loginUserId = null;
        //如果不为空去查询
        if (ObjUtil.isNotEmpty(loginUser)){
            //查询用户信息，拿到最新的用户对象  ，防止缓存跟数据不一致。
            User laestUser = userService.getById(loginUser.getId());
            //判断是否为空
            if (laestUser==null){
                //为空抛出异常  （可能被管理员封禁了）
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
            }
            loginUserId=laestUser.getId();
        }

        //新增了空间功能。如果没传入空间id，那设置nullspaceId为true然后放行查看公共图库。
        //如果是私有空间，那么需要校验权限了,我们需要校验当前用户是不是空间创建人，是才可以查看空间图库
        Long spaceId = pictureVOPagesRequest.getSpaceId();
        if (spaceId==null){
            pictureVOPagesRequest.setNullSpaceId(true);
        }else{
            User tempLoginUser = userService.getLoginUser(request);
            Space space = spaceService.getById(spaceId);
            ThrowUtil.throwIf(space==null,ErrorCode.PARAMS_ERROR,"空间不存在");
            //如果空间存在，那么需要校验当前登录用户是否是空间的创建人了。
            if (!tempLoginUser.getId().equals(space.getUserId())){
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"当前用户并非空间管理员,无权限");
            }
            //设置为false,去查询空间图库。
            pictureVOPagesRequest.setNullSpaceId(false);
        }

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
        //这里严谨点校验一下当前用户是否为传入的吧，毕竟只能自己查自己收藏
        if (!loginUser.getId().equals(favoritePicturePagesRequest.getUserId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户只能查看自己收藏列表");
        }
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
    public BaseResponse<Boolean> deletePictureById(@RequestBody DeleteRequest deleteRequest,HttpServletRequest request){
        if (deleteRequest ==null || deleteRequest.getId() <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //权限校验
        User loginUser = userService.getLoginUser(request);
        Picture oldPicture = pictureService.getById(deleteRequest.getId());
        pictureService.checkPictureAuth(loginUser,oldPicture);
        //开启事务
        transactionTemplate.execute(status -> {
            boolean ret = pictureService.removeById(deleteRequest.getId());
            if (!ret){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"删除图片失败");
            }
            //如果图片有空间id,那么我们对相应图片的空间进行一个额度更新。  没有就是公共图库的图片嘛。
            Long spaceId = oldPicture.getSpaceId();
            if (spaceId!=null){
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, spaceId)
                        .setSql("total_count = total_count -1")
                        .setSql("total_size = total_size - " + oldPicture.getPicSize())
                        .update();
                ThrowUtil.throwIf(!update,ErrorCode.OPERATION_ERROR,"空间额度更新失败");
            }
            return oldPicture; //随便返回并无意义。虽然可以返回到transactionTemplate外面拿到值的。但这里无业务需求
        });

        return ResultUtil.success(true);
    }

    /**
     * 管理员审核图片
     *
     * @param pictureReviewRequest
     * @param request
     * @return
     */
    @PostMapping("/pictureReview")
    @AuthCheck(mustRole = UserConstant.ADMIN)
    public BaseResponse<Boolean> doPictureReview(@RequestBody PictureReviewRequest pictureReviewRequest,
                                               HttpServletRequest request) {
        if (pictureReviewRequest == null || pictureReviewRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        pictureService.doPictureReview(pictureReviewRequest, loginUser);
        return ResultUtil.success(true);
    }


    /**
     * 用户头像上传，返回上传后的头像url
     * @param multipartFile
     * @param request
     * @return
     */
    @PostMapping("/uploadUserAvatar")
    public BaseResponse<String> uploadUserAvatar(@RequestPart("file")MultipartFile multipartFile,
                                                 HttpServletRequest request){
        //获取当前用户
        User loginUser = userService.getLoginUser(request);
        //图片上传
        String userAvatar = pictureService.uploadUserAvatar(multipartFile, loginUser);
        return ResultUtil.success(userAvatar);
    }



}
