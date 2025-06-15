package com.voracityrat.memehubbackend.common;


import com.voracityrat.memehubbackend.exception.ErrorCode;

/**
 * 快速返回
 * @author grey
 */
public class ResultUtil {

    /**
     * 成功返回
     * @param data
     * @return
     * @param <T>
     */

    public static <T>  BaseResponse<T> success(T data){
        return new BaseResponse<>(20000,"OK",data);
    }


    /**
     * 失败
     * @param errorCode
     * @return
     * @param <T>
     */
    public static <T>  BaseResponse<T> failed(ErrorCode errorCode){
        return new BaseResponse<>(errorCode.getCode(),errorCode.getMessage(),null);
    }

    /**
     * 失败
     * @param errorCode
     * @param message
     * @return
     * @param <T>
     */
    public static <T>  BaseResponse<T> failed(ErrorCode errorCode,String message){
        return new BaseResponse<>(errorCode.getCode(),message,null);
    }

    public static BaseResponse<?> failed(int code, String message) {
        return new BaseResponse<>(code,message,null);
    }
}
