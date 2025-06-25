package com.voracityrat.memehubbackend.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.voracityrat.memehubbackend.annotaion.AuthCheck;
import com.voracityrat.memehubbackend.common.BaseResponse;
import com.voracityrat.memehubbackend.common.ResultUtil;
import com.voracityrat.memehubbackend.constant.UserConstant;
import com.voracityrat.memehubbackend.exception.BusinessException;
import com.voracityrat.memehubbackend.exception.ErrorCode;
import com.voracityrat.memehubbackend.model.dto.DeleteRequest;
import com.voracityrat.memehubbackend.model.dto.picture.PictureUploadRequest;
import com.voracityrat.memehubbackend.model.dto.user.*;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.vo.LoginUserVO;
import com.voracityrat.memehubbackend.model.vo.PictureVO;
import com.voracityrat.memehubbackend.model.vo.UserVO;
import com.voracityrat.memehubbackend.service.PictureService;
import com.voracityrat.memehubbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
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

    @PostMapping("/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_USER)
    public BaseResponse<PictureVO> uploadPicture(@RequestPart("file")MultipartFile multipartFile,
                                                 PictureUploadRequest pictureUploadRequest,
                                                 HttpServletRequest request){
        //获取当前用户
        User loginUser = userService.getLoginUser(request);
        //图片上传
        PictureVO pictureVO = pictureService.uploadPicture(pictureUploadRequest, multipartFile, loginUser);
        return ResultUtil.success(pictureVO);

    }

}
