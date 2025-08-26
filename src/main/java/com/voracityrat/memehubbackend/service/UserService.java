package com.voracityrat.memehubbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.voracityrat.memehubbackend.model.dto.user.UserAddRequest;
import com.voracityrat.memehubbackend.model.dto.user.UserPageListRequest;
import com.voracityrat.memehubbackend.model.dto.user.UserUpdateRequest;
import com.voracityrat.memehubbackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.voracityrat.memehubbackend.model.vo.user.LoginUserVO;
import com.voracityrat.memehubbackend.model.vo.user.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 根据传入的user获取到脱敏后的登录用户数据
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVo(User user);

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

    /**
     * 添加用户
     *
     * @param userAddRequest
     * @return
     */
    boolean userAdd(UserAddRequest userAddRequest);

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param
     * @return
     */
    boolean updateUser(UserUpdateRequest userUpdateRequest, User loginUser);

    /**
     * 用户分页查询
     *
     * @param userPageListRequest
     * @return
     */
    Page<UserVO> userPageList(UserPageListRequest userPageListRequest);

    /**
     * 用户User脱敏为UserVo
     * @param user
     * @return
     */
    UserVO getUserVo(User user);


    /**
     * 根据用户们的id获取他们的名称，用于图片上传者信息展示
     *
     * @param userIds
     * @return
     */
    List<User> getUserNameByIds(List<Long> userIds);


    /**
     * 根据传入用户判断是否为管理员
     * @param loginUser
     * @return
     */
    Boolean isAdmin(User loginUser);
}
