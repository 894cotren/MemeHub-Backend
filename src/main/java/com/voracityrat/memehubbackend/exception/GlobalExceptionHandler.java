package com.voracityrat.memehubbackend.exception;

import com.voracityrat.memehubbackend.common.BaseResponse;
import com.voracityrat.memehubbackend.common.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 * @author grey
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 自定义异常BusinessException 的处理方法
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e){
        log.error("BusinessException 业务异常！",e);
        return ResultUtil.failed(e.getCode(),e.getMessage());
    }

    /**
     * 运行时异常统一处理
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e){
        log.error("RuntimeException 系统运行时异常！",e);
        return ResultUtil.failed(ErrorCode.SYSTEM_ERROR,"系统错误");
    }


}
