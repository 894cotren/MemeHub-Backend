package com.voracityrat.memehubbackend.constant;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户模块所需常量
 * @author grey
 */
public interface UserConstant {

    /**
     * 用户session信息存储的Key
     */
    String USER_LOGIN_STATUS="user_login_status";

    // region
    /**
     * 普通用户角色常量
     */
    String USER ="user";

    /**
     * 管理员角色常量
     */
    String ADMIN ="admin";


    /**
     * 用户锁列表
     */
    Map<Long, Object> LOCK_MAP = new ConcurrentHashMap<>();
    //endregion

}
