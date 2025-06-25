package com.voracityrat.memehubbackend.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.voracityrat.memehubbackend.config.CosClientConfig;
import com.voracityrat.memehubbackend.exception.BusinessException;
import com.voracityrat.memehubbackend.exception.ErrorCode;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * 通用的文件上传的一个工具类
 *
 * @author voracityrat
 */
@Component
public class CosUtil {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;


    /**
     * 上传文件到cos
     *
     * @param key  这个是文件存储到桶里的唯一key，
     *             指定文件上传到 COS 上的路径，即对象键。例如对象键为 folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
     * @param file 被上传文件。
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        if (file == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传文件为空");
        }
        // 指定文件将要存放的存储桶
        String bucketName = cosClientConfig.getBucket();
        // 指定文件上传到 COS 上的路径，即对象键。例如对象键为 folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载对象
     *
     * @param key 对象在桶里的唯一ID
     * @return
     */
    public COSObject getObject(String key) {
        // Bucket 的命名格式为 BucketName-APPID ，此处填写的存储桶名称必须为此格式
        String bucketName = cosClientConfig.getBucket();
        // 指定文件在 COS 上的路径，即对象键。例如对象键为 folder/picture.jpg，则表示下载的文件 picture.jpg 在 folder 路径下

        // 方法1 获取下载输入流
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        return cosClient.getObject(getObjectRequest);
    }

    /**
     * 上传图片，并且要求返回一些上传图片的基本信息
     *
     * @param key
     * @param file
     * @return
     */
    public PutObjectResult putPictureObject(String key, File file) {

        if (file == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传文件为空");
        }
        // 指定文件将要存放的存储桶
        String bucketName = cosClientConfig.getBucket();
        // 指定文件上传到 COS 上的路径，即对象键。例如对象键为 folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
        //添加获取图片基本信息的设置
        PicOperations picOperations = new PicOperations();
        picOperations.setIsPicInfo(1);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }
}
