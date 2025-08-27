package com.voracityrat.memehubbackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.voracityrat.memehubbackend.exception.BusinessException;
import com.voracityrat.memehubbackend.exception.ErrorCode;
import com.voracityrat.memehubbackend.exception.ThrowUtil;
import com.voracityrat.memehubbackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.voracityrat.memehubbackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.voracityrat.memehubbackend.model.entity.Space;
import com.voracityrat.memehubbackend.model.entity.SpaceUser;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.enums.SpaceRoleEnum;
import com.voracityrat.memehubbackend.model.vo.space.SpaceVO;
import com.voracityrat.memehubbackend.model.vo.spaceuser.SpaceUserVO;
import com.voracityrat.memehubbackend.model.vo.user.UserVO;
import com.voracityrat.memehubbackend.service.SpaceService;
import com.voracityrat.memehubbackend.service.SpaceUserService;
import com.voracityrat.memehubbackend.mapper.SpaceUserMapper;
import com.voracityrat.memehubbackend.service.UserService;
import com.voracityrat.memehubbackend.spaceauthcheck.SpaceUserAuthManager;
import com.voracityrat.memehubbackend.spaceauthcheck.model.SpaceUserPermissionConstant;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author voracityrat
* @description 针对表【space_user(空间用户关联)】的数据库操作Service实现
* @createDate 2025-09-01 09:14:48
*/
@Service
public class SpaceUserServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser>
    implements SpaceUserService{



    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private SpaceService spaceService;


    @Resource
    @Lazy
    private SpaceUserAuthManager spaceUserAuthManager;

    @Override
    public long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest,User loginUser) {
        // 参数校验
        ThrowUtil.throwIf(spaceUserAddRequest == null, ErrorCode.PARAMS_ERROR);
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserAddRequest, spaceUser);
        validSpaceUser(spaceUser, true);
        //团队空间权限校验，当前用户必须为管理者
        Long spaceId = spaceUserAddRequest.getSpaceId();
        Long loginUserId = loginUser.getId();
        //检查是否拥有目标权限。
        checkSpaceAuth(spaceId,loginUserId, SpaceUserPermissionConstant.SPACE_USER_MANAGE);
        // 数据库操作
        boolean result = this.save(spaceUser);
        ThrowUtil.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return spaceUser.getId();
    }

    /**
     * 根据当前登录用户id，给定的空间id，目标需要的权限进行一个校验。无返回值，无权限直接抛异常
     * @param spaceId
     * @param loginUserId
     * @param targetPermission
     */
    @Override
    public void checkSpaceAuth(Long spaceId, Long loginUserId, String targetPermission) {
        User user = userService.getById(loginUserId);
        //如果是网站管理员，直接一把梭,放行。
        if (userService.isAdmin(user)){
            return;
        }
        QueryWrapper<SpaceUser> queryWrapper =new QueryWrapper<>();
        queryWrapper.lambda().eq(SpaceUser::getUserId,loginUserId);
        queryWrapper.lambda().eq(SpaceUser::getSpaceId,spaceId);
        SpaceUser spaceUser = this.getOne(queryWrapper);
        ThrowUtil.throwIf(spaceUser==null,ErrorCode.NO_AUTH_ERROR,"无团队空间权限");
        //如果不为空，继续校验权限是否一直
        String spaceRole = spaceUser.getSpaceRole();
        boolean ret = spaceUserAuthManager.checkSpaceAuth(spaceRole, targetPermission);
        ThrowUtil.throwIf(!ret,ErrorCode.NO_AUTH_ERROR,"无团队空间权限");
    }

    @Override
    public void validSpaceUser(SpaceUser spaceUser, boolean add) {
        ThrowUtil.throwIf(spaceUser == null, ErrorCode.PARAMS_ERROR);
        // 创建时，空间 id 和用户 id 必填
        Long spaceId = spaceUser.getSpaceId();
        Long userId = spaceUser.getUserId();
        //如果是团队成员进行校验的时候，我们需要进行用过户和空间的有效性校验。
        if (add) {
            ThrowUtil.throwIf(ObjectUtil.hasEmpty(spaceId, userId), ErrorCode.PARAMS_ERROR);
            User user = userService.getById(userId);
            ThrowUtil.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
            Space space = spaceService.getById(spaceId);
            ThrowUtil.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        }
        // 校验空间角色是否存在
        String spaceRole = spaceUser.getSpaceRole();
        SpaceRoleEnum spaceRoleEnum = SpaceRoleEnum.getEnumByValue(spaceRole);
        if (spaceRole != null && spaceRoleEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间角色不存在");
        }
    }

    @Override
    public SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request) {
        // 对象转封装类
        SpaceUserVO spaceUserVO = SpaceUserVO.objToVo(spaceUser);
        // 关联查询用户信息
        Long userId = spaceUser.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVo(user);
            spaceUserVO.setUser(userVO);
        }
        // 关联查询空间信息
        Long spaceId = spaceUser.getSpaceId();
        if (spaceId != null && spaceId > 0) {
            Space space = spaceService.getById(spaceId);
            SpaceVO spaceVO = spaceService.getSpaceVO(space, request);
            spaceUserVO.setSpace(spaceVO);
        }
        return spaceUserVO;
    }

    @Override
    public List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList) {
        // 判断输入列表是否为空
        if (CollUtil.isEmpty(spaceUserList)) {
            return Collections.emptyList();
        }
        // 对象列表 => 封装对象列表
        List<SpaceUserVO> spaceUserVOList = spaceUserList.stream().map(SpaceUserVO::objToVo).collect(Collectors.toList());
        // 1. 收集需要关联查询的用户 ID 和空间 ID
        Set<Long> userIdSet = spaceUserList.stream().map(SpaceUser::getUserId).collect(Collectors.toSet());
        Set<Long> spaceIdSet = spaceUserList.stream().map(SpaceUser::getSpaceId).collect(Collectors.toSet());
        // 2. 批量查询用户和空间
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        Map<Long, List<Space>> spaceIdSpaceListMap = spaceService.listByIds(spaceIdSet).stream()
                .collect(Collectors.groupingBy(Space::getId));
        // 3. 填充 SpaceUserVO 的用户和空间信息
        spaceUserVOList.forEach(spaceUserVO -> {
            Long userId = spaceUserVO.getUserId();
            Long spaceId = spaceUserVO.getSpaceId();
            // 填充用户信息
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceUserVO.setUser(userService.getUserVo(user));
            // 填充空间信息
            Space space = null;
            if (spaceIdSpaceListMap.containsKey(spaceId)) {
                space = spaceIdSpaceListMap.get(spaceId).get(0);
            }
            spaceUserVO.setSpace(SpaceVO.objToVo(space));
        });
        return spaceUserVOList;    }

    @Override
    public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
        QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
        if (spaceUserQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceUserQueryRequest.getId();
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        Long userId = spaceUserQueryRequest.getUserId();
        String spaceRole = spaceUserQueryRequest.getSpaceRole();
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "space_id", spaceId);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceRole), "space_role", spaceRole);
        return queryWrapper;
    }
}




