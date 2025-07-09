package com.voracityrat.memehubbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.voracityrat.memehubbackend.exception.BusinessException;
import com.voracityrat.memehubbackend.exception.ErrorCode;
import com.voracityrat.memehubbackend.exception.ThrowUtil;
import com.voracityrat.memehubbackend.model.dto.picture.FavoritePicturePagesRequest;
import com.voracityrat.memehubbackend.model.dto.picture.FavoritePictureRequest;
import com.voracityrat.memehubbackend.model.entity.Picture;
import com.voracityrat.memehubbackend.model.entity.UserPicture;
import com.voracityrat.memehubbackend.model.vo.picture.PicturePagesVO;
import com.voracityrat.memehubbackend.service.PictureService;
import com.voracityrat.memehubbackend.service.UserPictureService;
import com.voracityrat.memehubbackend.mapper.UserPictureMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author voracityrat
 * @description 针对表【user_picture(用户收藏图片-收藏表)】的数据库操作Service实现
 * @createDate 2025-06-28 16:56:05
 */
@Service
public class UserPictureServiceImpl extends ServiceImpl<UserPictureMapper, UserPicture>
        implements UserPictureService {

    @Resource
    private UserPictureMapper userPictureMapper;

    @Lazy
    @Resource
    private PictureService pictureService;

    /**
     * 用户收藏图片
     *
     * @param favoritePictureRequest
     * @return
     */
    @Override
    public boolean userFavoritePicture(FavoritePictureRequest favoritePictureRequest) {
        /**
         * 1. 参数校验
         * 2. 数据插入
         * 3. 返回结果
         */
        ThrowUtil.throwIf(favoritePictureRequest == null, ErrorCode.PARAMS_ERROR);
        Long userId = favoritePictureRequest.getUserId();
        Long picId = favoritePictureRequest.getPicId();
        if (userId == null || picId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //检查是否已收藏
        QueryWrapper<UserPicture> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserPicture::getPicId, picId);
        queryWrapper.lambda().eq(UserPicture::getUserId, userId);
        long count = this.count(queryWrapper);
        ThrowUtil.throwIf(count > 0L, ErrorCode.OPERATION_ERROR, "已收藏");
        //插入参数组装
        UserPicture userPicture = new UserPicture();
        BeanUtils.copyProperties(favoritePictureRequest, userPicture);
        boolean result = this.save(userPicture);
        ThrowUtil.throwIf(!result, ErrorCode.SYSTEM_ERROR, "收藏图片异常，收藏失败");
        return result;
    }

    @Override
    public boolean userUnfavoritePicture(FavoritePictureRequest favoritePictureRequest) {

        /**
         *1. 参数校验
         * 2. 查询数据是否存在
         * 3. 删除收藏数据
         * 4. 返回结果
         */
        //参数校验
        ThrowUtil.throwIf(favoritePictureRequest == null, ErrorCode.PARAMS_ERROR);
        Long userId = favoritePictureRequest.getUserId();
        Long picId = favoritePictureRequest.getPicId();
        if (userId == null || picId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //数据删除
        QueryWrapper<UserPicture> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserPicture::getPicId, picId);
        queryWrapper.lambda().eq(UserPicture::getUserId, userId);
        boolean result = this.remove(queryWrapper);
        ThrowUtil.throwIf(!result, ErrorCode.OPERATION_ERROR, "取消收藏失败！");
        return result;
    }

    @Override
    public Page<PicturePagesVO> getFavoritePicturePages(FavoritePicturePagesRequest favoritePicturePagesRequest) {
        /**
         * 1. 参数校验
         * 2. 直接分页查询用户已收藏的图片信息
         * 3. 对图片信息脱敏处理
         * 4. 返回脱敏后的图片信息
         */
        //参数校验
        ThrowUtil.throwIf(favoritePicturePagesRequest == null, ErrorCode.PARAMS_ERROR);
        Long userId = favoritePicturePagesRequest.getUserId();
        long pageNum = favoritePicturePagesRequest.getPageNum();
        long pageSize = favoritePicturePagesRequest.getPageSize();
        if (userId == null || userId < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户为空或用户无效");
        }
        pageNum = pageNum < 1 ? 1L : pageNum;
        pageSize = pageSize < 1 ? 10L : pageSize;

        //分页查询
        Page<Picture> favoritePicturePages = userPictureMapper.getFavoritePicturePages(new Page<>(pageNum, pageSize), userId);
        ThrowUtil.throwIf(favoritePicturePages == null, ErrorCode.SYSTEM_ERROR, "分页查询失败");
        //数据脱敏
        List<PicturePagesVO> picturePagesVOList = pictureService.getPicturePagesVOList(favoritePicturePages.getRecords(), favoritePicturePagesRequest.getUserId());
        Page<PicturePagesVO> pictureVoPage = new Page<>(pageNum, pageSize);
        pictureVoPage.setTotal(favoritePicturePages.getTotal());
        pictureVoPage.setRecords(picturePagesVOList);
        return pictureVoPage;
    }

    /**
     * 返回图片id集合里用户收藏过的id
     * @param picIds
     * @param loginUserId
     * @return
     */
    @Override
    public Set<Long> favoriteInPictureIds(List<Long> picIds, Long loginUserId) {
        if (picIds == null || loginUserId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判断用户是否收藏图片失败");
        }
        if (picIds.isEmpty()) {
            return new HashSet<>();
        }
        return userPictureMapper.favoriteInPictureIds(picIds, loginUserId);
    }
}




