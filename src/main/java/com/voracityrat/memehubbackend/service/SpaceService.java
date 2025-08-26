package com.voracityrat.memehubbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.voracityrat.memehubbackend.model.dto.space.SpaceAddRequest;
import com.voracityrat.memehubbackend.model.dto.space.SpaceQueryRequest;
import com.voracityrat.memehubbackend.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.vo.space.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author grey
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-08-26 21:25:36
*/
public interface SpaceService extends IService<Space> {

    /**
     * 空间校验,验证空间相关字段合法性。
     * @param space
     * @param add
     */
    public void validSpace(Space space, boolean add);


    /**
     * 根据空间级别填充对应的数据，也就是最大图片数量和空间大小相关的
     * @param space
     */
    public void fillSpaceBySpaceLevel(Space space);


    /**
     * 根据space获取spaceVO，里面填充了创建人信息
     * @param space
     * @param request
     * @return
     */
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * spacePage分页脱敏
     * @param spacePage
     * @param request
     * @return
     */
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage,HttpServletRequest request);


    /**
     * 获取分页查询wrapper
     */
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);


    /**
     * 创建空间
     */

    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);
}
