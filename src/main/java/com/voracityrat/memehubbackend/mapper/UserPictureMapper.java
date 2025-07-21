package com.voracityrat.memehubbackend.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.voracityrat.memehubbackend.model.dto.picture.FavoritePicturePagesRequest;
import com.voracityrat.memehubbackend.model.entity.Picture;
import com.voracityrat.memehubbackend.model.entity.UserPicture;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

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
     * @param
     * @return
     */
    Page<Picture> getFavoritePicturePages(Page<Picture> page, @Param("pagesRequest") FavoritePicturePagesRequest pagesRequest);

    Set<Long> favoriteInPictureIds(@Param("pictureIds") List<Long> picIds, @Param("userId") Long loginUserId);
}




