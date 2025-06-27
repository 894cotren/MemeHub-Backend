package com.voracityrat.memehubbackend.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.voracityrat.memehubbackend.model.dto.picture.PicturePagesRequest;
import com.voracityrat.memehubbackend.model.entity.Picture;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author voracityrat
* @description 针对表【picture(图片表)】的数据库操作Mapper
* @createDate 2025-06-25 14:41:40
* @Entity com.voracityrat.memehubbackend.model.entity.Picture
*/
public interface PictureMapper extends BaseMapper<Picture> {

    Page<Picture> getPicturePages(IPage<Picture> page,@Param("query") PicturePagesRequest picturePagesRequest);

}




