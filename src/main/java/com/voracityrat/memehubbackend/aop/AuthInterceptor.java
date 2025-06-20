package com.voracityrat.memehubbackend.aop;

import com.voracityrat.memehubbackend.annotaion.AuthCheck;
import com.voracityrat.memehubbackend.exception.BusinessException;
import com.voracityrat.memehubbackend.exception.ErrorCode;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.enums.UserRoleEnum;
import com.voracityrat.memehubbackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 接口权限控制
 * @author grey
 */
@Aspect
@Component
public class AuthInterceptor {


    @Resource
    private UserService userService;

    /**
     * 接口鉴权
     * @param joinPoint
     * @param authCheck
     * @return
     * @throws Throwable
     */
    @Around("@annotation(authCheck)")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        /**
         * 1. 获取到角色对应的枚举对象
         *    1. 枚举对象为空应该报错吧。权限异常   （上了权限注解但是没指定权限的情况）
         * 2. 获取到当前请求对象 ，再通过request获取当前用户
         * 3. 对比当前用户的角色和权限需要的角色，校验权限是否足够
         *    1. 权限不够直接抛异常
         * 4. 权限足够放行。
         */
        //获取到角色对应的枚举对象
        String mustRole = authCheck.mustRole();
        UserRoleEnum mustRoleEnum = UserRoleEnum.getRoleEnumByValue(mustRole);
        //枚举对象为空应该报错吧。权限异常   （上了权限注解但是没指定权限的情况）
        if (mustRoleEnum==null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"未指定权限等级");
        }
        //获取到当前请求对象 ，再通过request获取当前用户
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request =((ServletRequestAttributes)requestAttributes).getRequest();
        //这个获取方法里如果当前角色为空会抛异常的，所以能出来必定拿到了
        User loginUser = userService.getLoginUser(request);
        //对比当前用户的角色和权限需要的角色，校验权限是否足够
        UserRoleEnum userRoleEnum = UserRoleEnum.getRoleEnumByValue(loginUser.getUserRole());
        if (userRoleEnum==null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        //如果用户权限不够所指定的权限，并且不是管理员，就抛异常。
        if (!mustRoleEnum.equals(userRoleEnum) && !userRoleEnum.equals(UserRoleEnum.ADMIN_USER)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //权限足够放行
        return joinPoint.proceed();
    }
}
