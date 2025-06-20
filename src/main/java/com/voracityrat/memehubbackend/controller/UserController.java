package com.voracityrat.memehubbackend.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.voracityrat.memehubbackend.annotaion.AuthCheck;
import com.voracityrat.memehubbackend.common.BaseResponse;
import com.voracityrat.memehubbackend.common.ResultUtil;
import com.voracityrat.memehubbackend.constant.UserConstant;
import com.voracityrat.memehubbackend.exception.BusinessException;
import com.voracityrat.memehubbackend.exception.ErrorCode;
import com.voracityrat.memehubbackend.model.dto.DeleteRequest;
import com.voracityrat.memehubbackend.model.dto.user.*;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.vo.LoginUserVo;
import com.voracityrat.memehubbackend.model.vo.UserVo;
import com.voracityrat.memehubbackend.service.UserService;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author grey
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (ObjectUtils.isEmpty(userRegisterRequest)) {
            return ResultUtil.failed(ErrorCode.PARAMS_ERROR, "注册体为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long userId = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtil.success(userId);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVo> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(userLoginRequest) || ObjectUtils.isEmpty(request) ) {
            return ResultUtil.failed(ErrorCode.PARAMS_ERROR, "登录请求体为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        LoginUserVo loginUserVo = userService.userLogin(userAccount, userPassword,request);
        return ResultUtil.success(loginUserVo);
    }

    /**
     * 获取当前用户
     */
    @GetMapping("/getLoginUser")
    public BaseResponse<LoginUserVo> getLoginUser(HttpServletRequest request){
        if (request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        return ResultUtil.success(userService.getLoginUserVo(loginUser));
    }

    /**
     * 用户登出
     */
    @GetMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request){
        if (request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtil.success(result);
    }


    /**
     * 用户添加
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_USER)
    public BaseResponse<Boolean> userAdd(@RequestBody UserAddRequest userAddRequest) {
        if (ObjectUtils.isEmpty(userAddRequest)) {
            return ResultUtil.failed(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userAdd(userAddRequest);
        return ResultUtil.success(result);
    }

    /**
     * 用户删除
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_USER)
    public BaseResponse<Boolean> userDelete(@RequestBody DeleteRequest deleteRequest) {
        /**
         * 入参：用户id，封装一个deleterequest请求体来获取id吧
         * 1. 校验用户id有效性
         * 2. 进行删除
         * 出参：boolean是否删除成功
         */
        //校验用户id有效性
        if (ObjectUtils.isEmpty(deleteRequest)) {
            return ResultUtil.failed(ErrorCode.PARAMS_ERROR);
        }
        if (deleteRequest.getId() <= 0L) {
            return ResultUtil.failed(ErrorCode.PARAMS_ERROR);
        }
        //进行删除
        boolean result = userService.removeById(deleteRequest.getId());
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除用户失败");
        }
        return ResultUtil.success(result);
    }

    /**
     * 用户更新
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_USER)
    public BaseResponse<Boolean> userUpdate(@RequestBody UserUpdateRequest userUpdateRequest) {

        //校验非空
        if (ObjectUtils.isEmpty(userUpdateRequest)) {
            return ResultUtil.failed(ErrorCode.PARAMS_ERROR);
        }
        //进行更新
        boolean result = userService.updateUser(userUpdateRequest);
        return ResultUtil.success(result);
    }

    /**
     * 分页查询用户
     */
    @PostMapping("/pageList")
    @AuthCheck(mustRole = UserConstant.ADMIN_USER)
    public BaseResponse<Page<UserVo>> userPageList(@RequestBody UserPageListRequest userPageListRequest) {

        //校验非空
        if (ObjectUtils.isEmpty(userPageListRequest)) {
            return ResultUtil.failed(ErrorCode.PARAMS_ERROR);
        }
        //进行分页查询
        Page<UserVo> userVoPage = userService.userPageList(userPageListRequest);
        return ResultUtil.success(userVoPage);
    }


    /**
     * 根据用户ID查询用户
     */
    @GetMapping("/getUserById")
    @AuthCheck(mustRole = UserConstant.ADMIN_USER)
    public BaseResponse<UserVo> getUserVoById(long id) {
        //参数校验
        if (id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"非法id");
        }
        //进行查询
        User user = userService.getById(id);
        return ResultUtil.success(userService.getUserVo(user));
    }




}
