package com.voracityrat.memehubbackend.service;

import com.voracityrat.memehubbackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author grey
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2025-06-17 22:21:55
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @return 用户主键id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * md5加密
     *
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);
}
