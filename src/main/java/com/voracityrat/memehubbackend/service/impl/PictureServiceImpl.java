package com.voracityrat.memehubbackend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qcloud.cos.utils.StringUtils;
import com.voracityrat.memehubbackend.exception.BusinessException;
import com.voracityrat.memehubbackend.exception.ErrorCode;
import com.voracityrat.memehubbackend.exception.ThrowUtil;
import com.voracityrat.memehubbackend.model.dto.file.UploadPictureResult;
import com.voracityrat.memehubbackend.model.dto.picture.PicturePagesRequest;
import com.voracityrat.memehubbackend.model.dto.picture.PictureUpdateRequest;
import com.voracityrat.memehubbackend.model.dto.picture.PictureUploadRequest;
import com.voracityrat.memehubbackend.model.entity.Picture;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.vo.PictureVO;
import com.voracityrat.memehubbackend.service.PictureService;
import com.voracityrat.memehubbackend.mapper.PictureMapper;
import com.voracityrat.memehubbackend.utils.PictureCosUtil;
import jdk.nashorn.internal.ir.IfNode;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author voracityrat
 * @description 针对表【picture(图片表)】的数据库操作Service实现
 * @createDate 2025-06-25 14:41:40
 */
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
        implements PictureService {

    @Resource
    private PictureCosUtil pictureCosUtil;

    @Resource
    private PictureMapper pictureMapper;

    @Override
    public PictureVO uploadPicture(PictureUploadRequest pictureUploadRequest, MultipartFile multipartFile, User loginUser) {
        /**
         * 入参：图片请求id的reqeust(可为空，区分是第一次新增还是第二次编辑)、当前登录对象，图片文件
         * 1. 参数校验、权限校验
         * 2. >判断是否是第一次?是第一次要上传cos嘛?如果不是第一次还需要上传吗?需要查询数据库对比一下url是否相同？避免重复上传？需要这一步吗？  算了不考虑那么复杂，都传
         *    上传cos，获取到图片基本信息
         * 3. 把图片基本信息封装，保存到数据库，获取到图片的id
         * 4. 返回该图片信息 id、url
         * 出参：图片id、url
         */
        // 1. 参数校验、权限校验
        //当前登陆用户不能为空
        ThrowUtil.throwIf(loginUser==null, ErrorCode.NO_AUTH_ERROR);
        // 判断是更新还是新增，如果是更新那么需要判断图片存在不存在
        Long pictureId=pictureUploadRequest.getId();
        if (pictureId!=null){
            boolean exists = this.lambdaQuery().eq(Picture::getId, pictureId).exists();
            ThrowUtil.throwIf(!exists,ErrorCode.NOT_FOUND_ERROR,"图片不存在");
        }
        //2.上传cos，获取到图片基本信息
        //配置用户上传前缀,公共的放在public路径下，并且按照用户id划分保存的目录
        String uploadPathPrefix=String.format("public/%s",loginUser.getId());
        UploadPictureResult uploadPictureResult = pictureCosUtil.pictureUpload(multipartFile, uploadPathPrefix);
        // 3. 把图片基本信息封装，保存到数据库，获取到图片的id
        Picture picture = new Picture();
        picture.setPicUrl(uploadPictureResult.getUrl());
        picture.setPicName(uploadPictureResult.getPicName());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());
        //如果传入的图片id不为空，那么得设置进来根据这个id更新。
        if(pictureId!=null){
            picture.setId(pictureId);
            picture.setUpdateTime(new Date());
        }
        //插入到数据库，如果没有id就是添加，那么会把id回写过来的，所以可以返回该对象
        boolean result = this.saveOrUpdate(picture);
        ThrowUtil.throwIf(!result,ErrorCode.SYSTEM_ERROR,"图片上传失败，数据库操作失败");
        return PictureVO.objToVo(picture);
    }

    @Override
    public boolean updatePicture(PictureUpdateRequest pictureUpdateRequest) {
        /**
         * 1. 参数校验
         *    1. 更新对象不能为空
         *    2. 必须要有图片id
         *    3. 如果有url 需要校验url长度
         *    4. 如果有简介 需要校验简介长度
         *    5. 如果有图片名称需要校验图片名称长度
         * 2.查询验证图片是否存在！
         * 3. 进行图片更新
         */
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest,picture);
        validPicture(picture);
        //查询验证图片是否存在！
        QueryWrapper<Picture> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(Picture::getId,picture.getId());
        long count = this.count(queryWrapper);
        ThrowUtil.throwIf(count<=0,ErrorCode.NOT_FOUND_ERROR);
        //进行图片更新
        boolean result = this.updateById(picture);
        ThrowUtil.throwIf(!result,ErrorCode.OPERATION_ERROR,"图片更新失败");
        return true;
    }

    @Override
    public Picture getPictureByIdForAdmin(Long picId) {
        /**
         * 1. 权限校验  controller做了
         * 2. 参数校验
         *    1. id不为空
         * 3. 数据库查询
         * 4. 返回图片数据
         */
        if (picId ==null || picId <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Picture picture = this.getById(picId);
        ThrowUtil.throwIf(picture==null,ErrorCode.NOT_FOUND_ERROR);
        return picture;
    }

    @Override
    public Page<Picture> getPicturePages(PicturePagesRequest picturePagesRequest) {
        /**
         * 1. 参数校验
         * 2. 权限校验
         * 3. 分页查询
         * 4. 分页结果返回
         */
        //参数校验
        ThrowUtil.throwIf(picturePagesRequest==null,ErrorCode.PARAMS_ERROR);
        long pageNum = picturePagesRequest.getPageNum();
        long pageSize = picturePagesRequest.getPageSize();
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 1) {
            pageSize = 7;
        }
        Page<Picture> picturePages = pictureMapper.getPicturePages(new Page<>(pageNum, pageSize), picturePagesRequest);
        ThrowUtil.throwIf(picturePages==null,ErrorCode.OPERATION_ERROR);
        return picturePages;
    }

    private void validPicture(Picture picture) {
        /**
         * 1. 参数校验
         *    1. 更新对象不能为空
         *    2. 必须要有图片id
         *    3. 如果有简介 需要校验简介长度
         *    4. 如果有图片名称需要校验图片名称长度
         */
        ThrowUtil.throwIf(picture==null,ErrorCode.PARAMS_ERROR);
        Long picId = picture.getId();
        if (picId==null || picId<=0){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String introduction = picture.getIntroduction();
        //如果有简介 需要校验简介长度
        if (StrUtil.isNotBlank(introduction)){
            if (128<introduction.length()){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"简介过长，不能超过128位");
            }
        }
        //如果有图片名称需要校验图片名称长度
        String picName = picture.getPicName();
        if (StrUtil.isNotBlank(picName)){
            if (32<picName.length()){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"图片名称，不能超过32位");
            }
        }
        String category = picture.getCategory();
        if (StrUtil.isNotBlank(category)){
            if (16<category.length()){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"图片简介，不能超过16位");
            }
        }
    }
}




