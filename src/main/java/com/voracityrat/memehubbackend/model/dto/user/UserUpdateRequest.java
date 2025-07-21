package com.voracityrat.memehubbackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新request
 *
 * @author grey
 */
@Data
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户角色：user/admin 其他权益待定
     */
    private String userRole;

    /**
     * 可收藏上限
     */
    private Integer favoriteLimit;



}
