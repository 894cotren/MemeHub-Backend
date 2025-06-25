package com.voracityrat.memehubbackend.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIUploadResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.voracityrat.memehubbackend.config.CosClientConfig;
import com.voracityrat.memehubbackend.exception.BusinessException;
import com.voracityrat.memehubbackend.exception.ErrorCode;
import com.voracityrat.memehubbackend.exception.ThrowUtil;
import com.voracityrat.memehubbackend.model.dto.file.UploadPictureResult;
import com.voracityrat.memehubbackend.model.dto.picture.PictureUploadRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 封装CosUtil 使工具使用更加贴切我们的业务需求
 *
 * @author voracityrat
 */
@Service
@Slf4j
public class PictureCosUtil {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    @Resource
    CosUtil cosUtil;

    /**
     * 图片上传接口
     *
     * @param multipartFile    图片文件
     * @param uploadPathPrefix 图片上传前缀、文件路径  每个用户可以传入该用户特殊的文件夹
     * @return
     */
    public UploadPictureResult pictureUpload(MultipartFile multipartFile, String uploadPathPrefix) {
        /**
         * 1. 文件源信息校验
         * 2. 进行图片上传的路径拼接
         * 3. 图片上传并获取到图片参数
         * 4. 封装图片参数并返回
         */
        //1.文件校验
        validPicture(multipartFile);
        //2.进行图片上传的路径拼接
        //获取一个随机数16位
        String uuid = RandomUtil.randomString(16);
        String originalFilename = multipartFile.getOriginalFilename();
        //拼接文件名
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originalFilename));
        //拼接唯一key的  可以为不同用户放在不同的文件路径下
        String uploadPath = String.format("%s/%s", uploadPathPrefix, uploadFileName);
        //3. 图片上传并获取到图片参数
        File file = null;
        PutObjectResult putObjectResult = null;
        try {
            //为该路径创建一个临时文件
            file = File.createTempFile(uploadPath, null);
            //为该文件填充数据
            multipartFile.transferTo(file);
            //上传文件
            putObjectResult = cosUtil.putPictureObject(uploadPath, file);
            //4. 封装图片参数并返回
            //从返回结果里获取到图片基础信息
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            //获取图片宽高并计算宽高比
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();

            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            uploadPictureResult.setUrl(cosClientConfig.getHost()+"/"+uploadPath);
            uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicFormat(imageInfo.getFormat());
            return uploadPictureResult;
        } catch (Exception e) {
            log.error("文件上传错误：", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        } finally {
            //删除临时文件
            this.deleteTempFile(file);
        }
    }

    /**
     * 删除临时文件
     * @param file
     */
    private void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        boolean deleteFlag = file.delete();
        if (!deleteFlag) {
            log.error("临时文件删除失败，路径为：{}", file.getAbsolutePath());
        }
    }

    private void validPicture(MultipartFile multipartFile) {
        /**
         * 1. 不能为空
         * 2. 大小小于10mb
         * 3. 后缀合规
         */
        //1非空校验
        ThrowUtil.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "图片数据为空");
        //2文件大小校验
        final long ONE_MB = 1024 * 1024;
        long pictureSize = multipartFile.getSize();
        ThrowUtil.throwIf(pictureSize > ONE_MB * 10, ErrorCode.PARAMS_ERROR, "上传图片过大,不能超过10MB");
        //3文件后缀校验
        //获取文件后缀
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final List<String> ALLOW_PICTURE_SUFFIX = Arrays.asList("jepg", "png", "jpg", "webp");
        ThrowUtil.throwIf(!ALLOW_PICTURE_SUFFIX.contains(suffix), ErrorCode.PARAMS_ERROR, "不支持该图片格式上传");
    }

}
