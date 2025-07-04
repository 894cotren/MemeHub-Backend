package com.voracityrat.memehubbackend.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.voracityrat.memehubbackend.model.entity.Picture;
import com.voracityrat.memehubbackend.model.entity.UserPicture;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author voracityrat
* @description 针对表【user_picture(用户收藏图片-收藏表)】的数据库操作Mapper
* @createDate 2025-06-28 16:56:05
* @Entity com.voracityrat.memehubbackend.model.entity.UserPicture
*/
public interface UserPictureMapper extends BaseMapper<UserPicture> {

    /**
     * 用户收藏列表查询，根据userId进行两表联查
     * @param page
     * @param userId
     * @return
     */
    Page<Picture> getFavoritePicturePages(Page<Picture> page, @Param("userId") long userId);

}




