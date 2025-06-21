package com.voracityrat.memehubbackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

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
     * 用户角色：user/vip/admin 其他权益待定
     */
    private String userRole;

    /**
     * 会员编号
     */
    private String vipNumber;


    /**
     * 收藏数
     */
    private Integer favoriteCount;

    /**
     * 会员过期时间
     */
    private Date vipExpireTime;


}
