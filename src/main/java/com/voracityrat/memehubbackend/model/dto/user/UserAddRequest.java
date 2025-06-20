package com.voracityrat.memehubbackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;
/**
 * 用户添加request
 *
 * @author grey
 */
@Data
public class UserAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 用户简介
     */
    private String userProfile;


    /**
     * 用户角色：user/vip/admin 其他权益待定
     */
    private String userRole;


}
