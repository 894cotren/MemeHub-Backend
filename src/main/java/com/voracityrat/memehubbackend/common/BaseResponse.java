package com.voracityrat.memehubbackend.common;


import lombok.Data;
import java.io.Serializable;

/**
 * 统一返回类
 * @author grey
 */
@Data
public class BaseResponse<T> implements Serializable {
    /**
     * 状态码
     */
    private int code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回体
     */
    private T data;

    public BaseResponse(int code ,String message, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }
}
