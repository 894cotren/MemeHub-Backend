package com.voracityrat.memehubbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.voracityrat.memehubbackend.model.dto.picture.FavoritePicturePagesRequest;
import com.voracityrat.memehubbackend.model.dto.picture.FavoritePictureRequest;
import com.voracityrat.memehubbackend.model.entity.UserPicture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.voracityrat.memehubbackend.model.vo.picture.PicturePagesVO;

import java.util.List;
import java.util.Set;

/**
* @author voracityrat
* @description 针对表【user_picture(用户收藏图片-收藏表)】的数据库操作Service
* @createDate 2025-06-28 16:56:05
*/
public interface UserPictureService extends IService<UserPicture> {

    /**
     * 用户收藏图片接口
     * @param favoritePictureRequest
     * @return
     */
    boolean userFavoritePicture (FavoritePictureRequest favoritePictureRequest);


    /**
     * 用户取消收藏图片接口
     * @param favoritePictureRequest
     * @return
     */
    boolean userUnfavoritePicture(FavoritePictureRequest favoritePictureRequest);


    /**
     * 分页查询用户收藏列表
     * @param favoritePicturePagesRequest
     * @return
     */
    Page<PicturePagesVO> getFavoritePicturePages (FavoritePicturePagesRequest favoritePicturePagesRequest);

    /**
     * 根据用户id和图片id集合，返回图片里有哪些图片是用户收藏过的
     * @param picIds
     * @param loginUserId
     * @return
     */
    Set<Long> favoriteInPictureIds(List<Long> picIds, Long loginUserId);
}
