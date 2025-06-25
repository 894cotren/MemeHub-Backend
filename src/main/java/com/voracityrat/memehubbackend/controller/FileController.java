package com.voracityrat.memehubbackend.controller;

import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.utils.IOUtils;
import com.voracityrat.memehubbackend.annotaion.AuthCheck;
import com.voracityrat.memehubbackend.common.BaseResponse;
import com.voracityrat.memehubbackend.common.ResultUtil;
import com.voracityrat.memehubbackend.constant.UserConstant;
import com.voracityrat.memehubbackend.exception.BusinessException;
import com.voracityrat.memehubbackend.exception.ErrorCode;
import com.voracityrat.memehubbackend.utils.CosUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author voracityrat
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private CosUtil cosUtil;

    @AuthCheck(mustRole = UserConstant.ADMIN_USER)
    @PostMapping("/test/upload")
    public BaseResponse<PutObjectResult> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
        //获取源文件名
        String filename = multipartFile.getOriginalFilename();
        //设置保存到本地的路径
        String filePath = String.format("/test/%s", filename);
        File file = null;
        PutObjectResult putObjectResult = null;
        try {
            //为该路径创建一个临时文件
            file = File.createTempFile(filePath, null);
            //为该文件填充数据
            multipartFile.transferTo(file);
            //上传文件
            putObjectResult = cosUtil.putObject(filePath, file);
            return ResultUtil.success(putObjectResult);
        } catch (Exception e) {
            log.error("文件上传错误：", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        } finally {
            if (file != null) {
                boolean deleteFlag = file.delete();
                if (!deleteFlag) {
                    log.error("临时文件删除失败，路径={}", filePath);
                }
            }
        }
    }

    /**
     * 文件下载
     *
     * @param filePath
     * @param response
     * @throws IOException
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_USER)
    @PostMapping("/test/download")
    public void testDownloadFile(String filePath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInputStream = null;
        byte[] byteArray = null;
        try {
            //获取到下载的文件的数据到内存里
            COSObject cosObject = cosUtil.getObject(filePath);
            cosObjectInputStream = cosObject.getObjectContent();
            byteArray = IOUtils.toByteArray(cosObjectInputStream);
            //写入到响应体中
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filePath);
            response.getOutputStream().write(byteArray);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("文件下载错误" + e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件下载错误");
        } finally {
            if (cosObjectInputStream != null) {
                cosObjectInputStream.close();
            }
        }
    }

}
