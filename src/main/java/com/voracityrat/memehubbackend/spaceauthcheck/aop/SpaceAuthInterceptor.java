package com.voracityrat.memehubbackend.spaceauthcheck.aop;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.voracityrat.memehubbackend.annotaion.AuthCheck;
import com.voracityrat.memehubbackend.exception.BusinessException;
import com.voracityrat.memehubbackend.exception.ErrorCode;
import com.voracityrat.memehubbackend.exception.ThrowUtil;
import com.voracityrat.memehubbackend.model.entity.SpaceUser;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.enums.UserRoleEnum;
import com.voracityrat.memehubbackend.service.SpaceUserService;
import com.voracityrat.memehubbackend.service.UserService;
import com.voracityrat.memehubbackend.spaceauthcheck.SpaceUserAuthManager;
import com.voracityrat.memehubbackend.spaceauthcheck.annotation.SpaceAuthCheck;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
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
//@Component
//暂时不要这个健全，处理团队空间有点麻烦的。
public class SpaceAuthInterceptor {


    @Resource
    private UserService userService;

    @Resource
    private SpaceUserService spaceUserService;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    /**
     * 接口鉴权
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(permoissionCheck)")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint, SpaceAuthCheck permoissionCheck) throws Throwable {
        /**
         * 获取目标权限
         * 获取当前角色
         * 我们需要去角色空间表去查询，但是我们需要一个空间id对吧。
         * 空间ID怎么拿到？ 通过注解传递嘛？
         */
//        //获取到角色对应的枚举对象
//        String mustPermission = permoissionCheck.mustPermission();
//        //获取到当前请求对象 ，再通过request获取当前用户
//        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
//        HttpServletRequest request =((ServletRequestAttributes)requestAttributes).getRequest();
//        //这个获取方法里如果当前角色为空会抛异常的，所以能出来必定拿到了
//        User loginUser = userService.getLoginUser(request);
//        //对比当前用户的角色和权限需要的角色，校验权限是否足够
//        //需要获取空间id
//
//
//
//        QueryWrapper<SpaceUser> queryWrapper =new QueryWrapper<>();
//        queryWrapper.lambda().eq(SpaceUser::getUserId,loginUser.getId());
//        queryWrapper.lambda().eq(SpaceUser::getSpaceId,spaceId);
//        SpaceUser spaceUser = spaceUserService.getOne(queryWrapper);
//        ThrowUtil.throwIf(spaceUser==null,ErrorCode.NO_AUTH_ERROR,"无团队空间权限");
//        //如果不为空，继续校验权限是否一直
//        String spaceRole = spaceUser.getSpaceRole();
//        boolean ret = spaceUserAuthManager.checkSpaceAuth(spaceRole, mustPermission);
//        ThrowUtil.throwIf(!ret,ErrorCode.NO_AUTH_ERROR,"无团队空间权限");
//        //权限足够放行
        return joinPoint.proceed();
    }


//
//    /**
//     * 从请求参数中自动提取空间ID
//     */
//    private Long extractSpaceIdFromRequest(ProceedingJoinPoint joinPoint) {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        String[] parameterNames = signature.getParameterNames();
//        Object[] args = joinPoint.getArgs();
//
//        // 遍历所有参数，查找包含spaceId的请求体
//        for (int i = 0; i < args.length; i++) {
//            Object arg = args[i];
//            if (arg != null) {
//                Long spaceId = extractSpaceIdFromObject(arg);
//                if (spaceId != null) {
//                    return spaceId;
//                }
//            }
//        }
//        return null;
//    }
//    /**
//     * 从对象中提取空间ID
//     */
//    private Long extractSpaceIdFromObject(Object obj) {
//        if (obj == null) return null;
//
//        try {
//            // 方法1：尝试调用 getSpaceId() 方法
//            Method getSpaceIdMethod = obj.getClass().getMethod("getSpaceId");
//            if (getSpaceIdMethod != null) {
//                Object result = getSpaceIdMethod.invoke(obj);
//                if (result instanceof Long) {
//                    return (Long) result;
//                } else if (result instanceof Integer) {
//                    return ((Integer) result).longValue();
//                }
//            }
//
//            // 方法2：尝试调用 getTeamId() 方法（备用）
//            Method getTeamIdMethod = obj.getClass().getMethod("getTeamId");
//            if (getTeamIdMethod != null) {
//                Object result = getTeamIdMethod.invoke(obj);
//                if (result instanceof Long) {
//                    return (Long) result;
//                } else if (result instanceof Integer) {
//                    return ((Integer) result).longValue();
//                }
//            }
//
//            // 方法3：通过字段名直接访问（如果上面的方法都失败）
//            Field spaceIdField = getFieldByName(obj.getClass(), "spaceId");
//            if (spaceIdField != null) {
//                spaceIdField.setAccessible(true);
//                Object result = spaceIdField.get(obj);
//                if (result instanceof Long) {
//                    return (Long) result;
//                } else if (result instanceof Integer) {
//                    return ((Integer) result).longValue();
//                }
//            }
//
//        } catch (Exception e) {
//            log.warn("提取空间ID失败: {}", e.getMessage());
//        }
//
//        return null;
//    }

}
