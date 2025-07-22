package com.voracityrat.memehubbackend.controller;


import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.voracityrat.memehubbackend.annotaion.AuthCheck;
import com.voracityrat.memehubbackend.common.BaseResponse;
import com.voracityrat.memehubbackend.common.ResultUtil;
import com.voracityrat.memehubbackend.constant.UserConstant;
import com.voracityrat.memehubbackend.exception.BusinessException;
import com.voracityrat.memehubbackend.exception.ErrorCode;
import com.voracityrat.memehubbackend.exception.ThrowUtil;
import com.voracityrat.memehubbackend.model.dto.DeleteRequest;
import com.voracityrat.memehubbackend.model.dto.user.*;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.vo.user.LoginUserVO;
import com.voracityrat.memehubbackend.model.vo.user.UserVO;
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
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(userLoginRequest) || ObjectUtils.isEmpty(request) ) {
            return ResultUtil.failed(ErrorCode.PARAMS_ERROR, "登录请求体为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        LoginUserVO loginUserVo = userService.userLogin(userAccount, userPassword, request);
        return ResultUtil.success(loginUserVo);
    }

    /**
     * 获取当前用户
     */
    @GetMapping("/getLoginUser")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
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
    @AuthCheck(mustRole = UserConstant.ADMIN)
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
    @AuthCheck(mustRole = UserConstant.ADMIN)
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
     * 用户更新   （已经做了权限校验了，不允许用户更新用户唯一账号名、用户角色，用户收藏上限）
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> userUpdate(@RequestBody UserUpdateRequest userUpdateRequest,
                                            HttpServletRequest request) {

        //校验非空
        if (ObjectUtils.isEmpty(userUpdateRequest)) {
            return ResultUtil.failed(ErrorCode.PARAMS_ERROR);
        }
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtil.throwIf(ObjUtil.isEmpty(loginUser),ErrorCode.NOT_LOGIN_ERROR,"当前未登录");
        //权限校验，只有用户更改的自己的或者自己是管理员才可以更改
        if (!loginUser.getId().equals(userUpdateRequest.getId()) && !UserConstant.ADMIN.equals(loginUser.getUserRole()) )
        {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //进行更新
        boolean result = userService.updateUser(userUpdateRequest,loginUser);
        return ResultUtil.success(result);
    }

    /**
     * 分页查询用户
     */
    @PostMapping("/pageList")
    @AuthCheck(mustRole = UserConstant.ADMIN)
    public BaseResponse<Page<UserVO>> userPageList(@RequestBody UserPageListRequest userPageListRequest) {

        //校验非空
        if (ObjectUtils.isEmpty(userPageListRequest)) {
            return ResultUtil.failed(ErrorCode.PARAMS_ERROR);
        }
        //进行分页查询
        Page<UserVO> userVoPage = userService.userPageList(userPageListRequest);
        return ResultUtil.success(userVoPage);
    }


    /**
     * 根据用户ID查询用户（管理员权限）
     */
    @GetMapping("/getUserById")
    @AuthCheck(mustRole = UserConstant.ADMIN)
    public BaseResponse<UserVO> getUserVoById(long id) {
        //参数校验
        if (id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"非法id");
        }
        //进行查询
        User user = userService.getById(id);
        //非空判断，如果为空，抛出无数据报错
        if(user==null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtil.success(userService.getUserVo(user));
    }

    /**
     * 获取当前用户信息（用于编辑）
     */
    @GetMapping("/getCurrentUser")
    public BaseResponse<UserVO> getCurrentUser(HttpServletRequest request) {
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtil.throwIf(ObjUtil.isEmpty(loginUser),ErrorCode.NOT_LOGIN_ERROR,"当前未登录");
        return ResultUtil.success(userService.getUserVo(loginUser));
    }


}
