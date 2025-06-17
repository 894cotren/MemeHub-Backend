package com.voracityrat.memehubbackend.exception;


/**
 * 方便跑异常的工具类
 *  但是使用下来，感觉这个要么太宽，要么不好调试，可读性不好，所以闲置着吧。
 * @author grey
 */
public class ThrowUtil {

    /**
     * 如果条件为真，抛异常
     * @param condition
     * @param errorCode
     */
    public static void throwIf(boolean condition,ErrorCode errorCode){
        if (condition){
            throw new BusinessException(errorCode);
        }
    }

    /**
     * 如果条件为真，抛异常
     * @param condition
     * @param runtimeException
     */
    public static void throwIf(boolean condition,RuntimeException runtimeException){
        if (condition){
            throw runtimeException;
        }
    }

    /**
     * 如果条件为真，抛异常
     * @param condition
     * @param errorCode
     * @param message
     */
    public static void throwIf(boolean condition,ErrorCode errorCode,String message){
        if (condition){
            throw new BusinessException(errorCode,message);
        }
    }

}
