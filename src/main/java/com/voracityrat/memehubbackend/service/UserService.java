package com.voracityrat.memehubbackend.service;

import com.voracityrat.memehubbackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.voracityrat.memehubbackend.model.vo.LoginUserVo;

import javax.servlet.http.HttpServletRequest;

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

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 根据传入的user获取到脱敏后的登录用户数据
     * @param user
     * @return
     */
    LoginUserVo getLoginUserVo(User user);

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 当前用户退出登录
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);
}
