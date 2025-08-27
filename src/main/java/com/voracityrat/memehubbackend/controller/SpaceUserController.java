package com.voracityrat.memehubbackend.controller;

import cn.hutool.core.util.ObjectUtil;
import com.voracityrat.memehubbackend.annotaion.AuthCheck;
import com.voracityrat.memehubbackend.common.BaseResponse;
import com.voracityrat.memehubbackend.common.ResultUtil;
import com.voracityrat.memehubbackend.constant.UserConstant;
import com.voracityrat.memehubbackend.exception.BusinessException;
import com.voracityrat.memehubbackend.exception.ErrorCode;
import com.voracityrat.memehubbackend.exception.ThrowUtil;
import com.voracityrat.memehubbackend.model.dto.DeleteRequest;
import com.voracityrat.memehubbackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.voracityrat.memehubbackend.model.dto.spaceuser.SpaceUserEditRequest;
import com.voracityrat.memehubbackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.voracityrat.memehubbackend.model.entity.SpaceUser;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.vo.spaceuser.SpaceUserVO;
import com.voracityrat.memehubbackend.service.SpaceUserService;
import com.voracityrat.memehubbackend.service.UserService;
import com.voracityrat.memehubbackend.spaceauthcheck.annotation.SpaceAuthCheck;
import com.voracityrat.memehubbackend.spaceauthcheck.model.SpaceUserPermissionConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 空间成员管理
 */
@RestController
@RequestMapping("/spaceUser")
@Slf4j
public class SpaceUserController {

    @Resource
    private SpaceUserService spaceUserService;

    @Resource
    private UserService userService;

    /**
     * 添加成员到空间
     */
    @PostMapping("/add")
    public BaseResponse<Long> addSpaceUser(@RequestBody SpaceUserAddRequest spaceUserAddRequest, HttpServletRequest request) {
        ThrowUtil.throwIf(spaceUserAddRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        long id = spaceUserService.addSpaceUser(spaceUserAddRequest,loginUser);
        return ResultUtil.success(id);
    }

    /**
     * 从空间移除成员
     * 传入的是空间用户表的，表里对应行的主键
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSpaceUser(@RequestBody DeleteRequest deleteRequest,
                                                 HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        SpaceUser oldSpaceUser = spaceUserService.getById(id);
        ThrowUtil.throwIf(oldSpaceUser == null, ErrorCode.NOT_FOUND_ERROR);
        //团队空间权限校验，当前用户必须为管理者
        Long spaceId = oldSpaceUser.getSpaceId();
        Long loginUserId = userService.getLoginUser(request).getId();
        spaceUserService.checkSpaceAuth(spaceId,loginUserId, SpaceUserPermissionConstant.SPACE_USER_MANAGE);
        // 操作数据库
        boolean result = spaceUserService.removeById(id);
        ThrowUtil.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtil.success(true);
    }

    /**
     * 查询某个成员在某个空间的信息
     * 管理员可用
     */
    @PostMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN)
    public BaseResponse<SpaceUser> getSpaceUser(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest) {
        // 参数校验
        ThrowUtil.throwIf(spaceUserQueryRequest == null, ErrorCode.PARAMS_ERROR);
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        Long userId = spaceUserQueryRequest.getUserId();
        ThrowUtil.throwIf(ObjectUtil.hasEmpty(spaceId, userId), ErrorCode.PARAMS_ERROR);
        // 查询数据库
        SpaceUser spaceUser = spaceUserService.getOne(spaceUserService.getQueryWrapper(spaceUserQueryRequest));
        ThrowUtil.throwIf(spaceUser == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtil.success(spaceUser);
    }

    /**
     * 查询成员信息列表
     */
    @PostMapping("/list")
    public BaseResponse<List<SpaceUserVO>> listSpaceUser(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest,
                                                         HttpServletRequest request) {
        ThrowUtil.throwIf(spaceUserQueryRequest == null, ErrorCode.PARAMS_ERROR);

        //团队空间权限校验，当前用户必须为管理者
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        Long loginUserId = userService.getLoginUser(request).getId();
        spaceUserService.checkSpaceAuth(spaceId,loginUserId, SpaceUserPermissionConstant.SPACE_USER_MANAGE);

        List<SpaceUser> spaceUserList = spaceUserService.list(
                spaceUserService.getQueryWrapper(spaceUserQueryRequest)
        );
        return ResultUtil.success(spaceUserService.getSpaceUserVOList(spaceUserList));
    }

    /**
     * 编辑成员信息（设置权限）
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editSpaceUser(@RequestBody SpaceUserEditRequest spaceUserEditRequest,
                                               HttpServletRequest request) {
        if (spaceUserEditRequest == null || spaceUserEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 进行转换
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserEditRequest, spaceUser);
        // 数据校验
        spaceUserService.validSpaceUser(spaceUser, false);
        //团队空间权限校验，当前用户必须为管理者
        Long spaceId = spaceUser.getSpaceId();
        Long loginUserId = userService.getLoginUser(request).getId();
        spaceUserService.checkSpaceAuth(spaceId,loginUserId, SpaceUserPermissionConstant.SPACE_USER_MANAGE);

        // 判断是否存在
        long id = spaceUserEditRequest.getId();
        SpaceUser oldSpaceUser = spaceUserService.getById(id);
        ThrowUtil.throwIf(oldSpaceUser == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = spaceUserService.updateById(spaceUser);
        ThrowUtil.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtil.success(true);
    }

    /**
     * 查询我加入的团队空间列表
     */
    @PostMapping("/list/my")
    public BaseResponse<List<SpaceUserVO>> listMyTeamSpace(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        SpaceUserQueryRequest spaceUserQueryRequest = new SpaceUserQueryRequest();
        spaceUserQueryRequest.setUserId(loginUser.getId());
        List<SpaceUser> spaceUserList = spaceUserService.list(
                spaceUserService.getQueryWrapper(spaceUserQueryRequest)
        );
        return ResultUtil.success(spaceUserService.getSpaceUserVOList(spaceUserList));
    }
}