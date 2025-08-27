package com.voracityrat.memehubbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.voracityrat.memehubbackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.voracityrat.memehubbackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.voracityrat.memehubbackend.model.entity.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.vo.spaceuser.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author voracityrat
* @description 针对表【space_user(空间用户关联)】的数据库操作Service
* @createDate 2025-09-01 09:14:48
*/
public interface SpaceUserService extends IService<SpaceUser> {

    /**
     * 创建空间成员
     *
     * @param spaceUserAddRequest
     * @return
     */
    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest, User loginUser);

    /**
     * 校验团队空间权限。
     * 根据当前登录用户id，给定的空间id，目标需要的权限进行一个校验。无返回值，无权限直接抛异常
     * @param spaceId
     * @param loginUserId
     * @param targetPermission
     */
    void checkSpaceAuth(Long spaceId, Long loginUserId, String targetPermission);

    /**
     * 校验空间成员
     *
     * @param spaceUser
     * @param add       是否为创建时检验
     */
    void validSpaceUser(SpaceUser spaceUser, boolean add);

    /**
     * 获取空间成员包装类（单条）
     *
     * @param spaceUser
     * @param request
     * @return
     */
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    /**
     * 获取空间成员包装类（列表）
     *
     * @param spaceUserList
     * @return
     */
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);

    /**
     * 获取查询对象
     *
     * @param spaceUserQueryRequest
     * @return
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

}
